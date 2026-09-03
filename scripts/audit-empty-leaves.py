#!/usr/bin/env python3

import argparse
import json
import re
import sys
from pathlib import Path

THRESHOLD = 200

FRONTMATTER = re.compile(r"^---.*?---", re.S)
XREF = re.compile(r"^[_*\s]*(參見|參閱|另見|見\s|See\b)")
PLACEHOLDER = re.compile(r"待補|待人工補充|保留位置|TODO|待撰寫")
SOURCE_ABSENT = re.compile(
    r"原書.{0,80}?(無|沒有|未收|尚未成稿|不存在|待續|僅列出章名)"
    r"|此一版本.{0,80}?(僅列出章名|待續|尚未成稿)"
    r"|原書定稿無"
    r"|原版.{0,40}?(未包含|未收錄|不含)"
)

STRUCTURAL = re.compile(
    r"^(\d+-)?(forewords?|prefaces?|series-preface|title-page|index|audiobook"
    r"|note-to-readers|epilogue|afterword|acknowledg\w*|about-the-author|glossary)(-.*)?$"
)
EMBED = re.compile(r"<(embed|iframe)\b")

NO_SOURCE = re.compile(r"(未收|未存|沒有|無)原始檔|原始檔.{0,20}(未收|闕如|不在庫)")


def body_of(path: Path) -> str:
    return FRONTMATTER.sub("", path.read_text(encoding="utf-8"), count=1).strip()


def is_structural(rel: str, body: str) -> bool:
    return EMBED.search(body) is not None or any(STRUCTURAL.match(seg) for seg in rel.split("/"))


def classify(body: str, rel: str = "") -> str:
    if NO_SOURCE.search(body):
        return "no-source"
    if is_structural(rel, body):
        return "structural"
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
            continue
        leaves += 1
        body = body_of(index)
        if len(body) >= THRESHOLD:
            continue
        rel = str(chapter.relative_to(docs))
        found.append(
            {
                "chapter": rel,
                "kind": classify(body, rel),
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
    ap = argparse.ArgumentParser(description="稽核書 repo 的葉章：分出真欠債與體裁本來就短的章")
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
        v for r in results for k, v in r["counts"].items() if k in ("xref", "source-absent", "no-source", "structural")
    )
    print(f"合計：真欠債 {total} 章；門檻撈出但體裁本來就短、不算債的 {dismissed} 章")
    return 0


if __name__ == "__main__":
    sys.exit(main())
