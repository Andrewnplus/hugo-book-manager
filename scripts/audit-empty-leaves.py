#!/usr/bin/env python3
"""空葉章稽核——分辨「真的還沒寫」與「本來就該這麼短」。

存在的理由：2026-08-28 用「葉章正文不足 200 字元＝空」這個單一門檻掃全庫，
把 `dictionary-of-paul-and-his-letters` 判成「448 條目空 231」，列進待補榜單
八天。實際去看才發現那 231 條**全部**是辭典的交叉參照條目——

    title: "阿們"
    參見 Prayer。

「阿們 → 參見 Prayer」就是這條的完整內容，一個字都不缺。同一個門檻也誤傷了
`on-top-of-tides` 的 5 章：那幾章在原書該版本裡只有章名、正文標「待續」，
書上就沒有，不是我們欠的。

教訓是**字數門檻只能當「值得看一眼」的觸發器，不能當判決**。體裁決定一章
該多長：辭典的交叉參照、索引頁、原書缺章的說明，天生就是三五十字。
所以這支腳本先用門檻撈出短葉章，再依內容把它們分成四類，只有前兩類算債。

分類（順序即優先級，先中先算）：

    placeholder    寫著「待補／待人工補充／保留位置」——我們自己留的坑     ← 算債
    blank          完全沒有正文                                        ← 算債
    xref           開頭是「參見 X」——辭典／索引的交叉參照，完整條目      ← 不算債
    source-absent  說明原書此版本就沒有這章（待續、未成稿、無此附錄）    ← 不算債
    thin           其他短內容——工具不猜，列出來給人看                   ← 待判

用法:
    audit-empty-leaves.py <repo 路徑>        單本，逐筆列出分類
    audit-empty-leaves.py --all [根目錄]      全庫，只列有債的書
    audit-empty-leaves.py --all --json       機器讀

葉章＝含 `_index.md` 且底下沒有子目錄的章節目錄（有子目錄的是母章，它的
`_index.md` 本來就常留白讓子章展開，一律不計）。
"""

import argparse
import json
import re
import sys
from pathlib import Path

THRESHOLD = 200  # 觸發檢查的正文字元數；不是判決門檻

FRONTMATTER = re.compile(r"^---.*?---", re.S)
XREF = re.compile(r"^[_*\s]*(參見|參閱|另見|見\s|See\b)")
PLACEHOLDER = re.compile(r"待補|待人工補充|保留位置|TODO|待撰寫")
# 說「原書就沒有這章」的話術散在一句話的頭尾，中間常夾版本資訊（書名、日期），
# 所以比對窗口要寬；2026-08-28 首版用 40 字，漏掉 on-top-of-tides 的整批。
SOURCE_ABSENT = re.compile(
    r"原書.{0,80}?(無|沒有|未收|尚未成稿|不存在|待續|僅列出章名)"
    r"|此一版本.{0,80}?(僅列出章名|待續|尚未成稿)"
    r"|原書定稿無"
)


def body_of(path: Path) -> str:
    return FRONTMATTER.sub("", path.read_text(encoding="utf-8"), count=1).strip()


def classify(body: str) -> str:
    if PLACEHOLDER.search(body):
        return "placeholder"
    if not body:
        return "blank"
    if XREF.match(body):
        return "xref"
    if SOURCE_ABSENT.search(body):
        return "source-absent"
    return "thin"


DEBT = {"placeholder", "blank"}


def audit(repo: Path) -> dict:
    docs = repo / "site" / "content" / "docs"
    leaves = 0
    found = []
    for index in docs.rglob("_index.md"):
        chapter = index.parent
        if any(child.is_dir() for child in chapter.iterdir()):
            continue  # 母章：留白是為了讓子章展開
        leaves += 1
        body = body_of(index)
        if len(body) >= THRESHOLD:
            continue
        found.append(
            {
                "chapter": str(chapter.relative_to(docs)),
                "kind": classify(body),
                "chars": len(body),
                "excerpt": body[:70].replace("\n", " "),
            }
        )
    counts = {}
    for item in found:
        counts[item["kind"]] = counts.get(item["kind"], 0) + 1
    return {
        "repo": repo.name,
        "path": str(repo),
        "leaves": leaves,
        "debt": sum(counts.get(k, 0) for k in DEBT),
        "thin": counts.get("thin", 0),
        "counts": counts,
        "items": found,
    }


def is_book_repo(path: Path) -> bool:
    return (path / "site" / "content" / "docs").is_dir()


def find_books(root: Path):
    for index in sorted(root.rglob("site/content/docs")):
        repo = index.parent.parent.parent
        if "/tmp/" in str(repo) + "/":
            continue
        yield repo


def print_book(result: dict, verbose: bool = True) -> None:
    label = f"{result['repo']}  ({result['leaves']} 葉章)"
    parts = [f"{k} {v}" for k, v in sorted(result["counts"].items())]
    print(f"{label}\n  {'、'.join(parts) if parts else '全部葉章都有內容'}")
    print(f"  → 真欠債 {result['debt']}、待判 {result['thin']}")
    if not verbose:
        return
    for item in result["items"]:
        mark = "!" if item["kind"] in DEBT else ("?" if item["kind"] == "thin" else " ")
        print(f"  {mark} [{item['kind']:>13s}] {item['chapter']}")
        if item["excerpt"]:
            print(f"        {item['excerpt']}")


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    ap.add_argument("target", nargs="?", default=".", help="書 repo 路徑，或 --all 的根目錄")
    ap.add_argument("--all", action="store_true", help="掃描根目錄底下所有書 repo")
    ap.add_argument("--json", action="store_true", help="輸出 JSON")
    args = ap.parse_args()

    root = Path(args.target).resolve()

    if not args.all:
        if not is_book_repo(root):
            print(f"不是書 repo（找不到 site/content/docs）：{root}", file=sys.stderr)
            return 2
        result = audit(root)
        if args.json:
            print(json.dumps(result, ensure_ascii=False, indent=2))
        else:
            print_book(result)
        return 1 if result["debt"] else 0

    results = [audit(repo) for repo in find_books(root)]
    debts = sorted(
        (r for r in results if r["debt"] or r["thin"]),
        key=lambda r: (-r["debt"], -r["thin"]),
    )
    if args.json:
        print(json.dumps(debts, ensure_ascii=False, indent=2))
        return 0
    print(f"掃描 {len(results)} 本，{len(debts)} 本有欠債或待判\n")
    for result in debts:
        print_book(result, verbose=False)
        print()
    total = sum(r["debt"] for r in results)
    dismissed = sum(
        v for r in results for k, v in r["counts"].items() if k in ("xref", "source-absent")
    )
    print(f"合計：真欠債 {total} 章；門檻撈出但體裁本來就短、不算債的 {dismissed} 章")
    return 0


if __name__ == "__main__":
    sys.exit(main())
