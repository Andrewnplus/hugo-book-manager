#!/usr/bin/env python3
"""深度概覽品檢。

存在的理由：2026-08-12 掃過全庫 1613 本既有概覽，78% 的「貢獻與定位」段完全
沒有任何限制／批評字眼、30% 全文沒引用年份、11% 出現空洞讚美詞。撰寫原則早就
寫著「避免行銷式誇大」，但沒有任何機制檢查，所以沒有被遵守。原則不會自己生效。

這支腳本是 /book-generate-deep-overview 步驟 5 的強制關卡，也是全庫盤點的工具。

用法:
    audit-overview.py <repo 路徑>          單本，人可讀的結果，FAIL 時 exit 1
    audit-overview.py --all [根目錄]        全庫掃描，輸出統計與最該重做的清單
    audit-overview.py --all --json         全庫掃描，輸出 JSON 供其他工具消費
"""

import argparse
import io
import json
import os
import re
import sys
import unicodedata

# 路徑相對於本腳本自己（scripts/ 在 hugo-book-manager 內），這樣 repo 被 clone
# 到別處也不會壞；books-done 與 hugo-book-manager 是 books-management 下的兄弟目錄。
_HERE = os.path.dirname(os.path.abspath(__file__))
_BOOKS_MGMT = os.path.dirname(os.path.dirname(_HERE))
DEFAULT_ROOT = os.path.join(_BOOKS_MGMT, "books-done")
DEFAULT_OUT = os.path.join(os.path.dirname(_HERE), "OVERVIEW-PROGRESS.md")

SECTIONS = ["作者的位置", "完整摘要", "定位", "這本書的限制"]
LEGACY = ["作者背景", "完整摘要", "本書的貢獻與定位"]

# 空洞讚美：說了等於沒說，而且擋住了真正的判斷
FILLER = [
    "深刻", "精彩", "值得一讀", "發人深省", "令人震撼", "不可多得",
    "經典之作", "必讀", "震撼人心", "淋漓盡致", "鞭辟入裡", "字字珠璣",
]

# 每段的下限與上限。分段設定是因為四段的工作量本來就不同：
#   作者的位置   要交代「從哪說話／跟誰吵／什麼經歷逼他這樣主張／怎麼吵贏他」四件事
#   定位         要點名書與作者，中英並列很吃字
#   完整摘要     長度由「書裡有幾條獨立的線」決定，而不是由筆記量決定
#   這本書的限制 三項可被檢驗的限制，每項寫到能被驗證大約各要 150 字
# 上限刻意寬鬆：它要擋的是灌水，不是替密度高的書設天花板。
SECTION_CHARS = {
    "作者的位置": (250, 800),
    "完整摘要": (600, 1700),
    "定位": (250, 800),
    "這本書的限制": (300, 900),
}

# 時間錨點。舊版只認 19xx/20xx，於是寫古典作品時「公元前 5 世紀」「1545 年」
# 「9 世紀」全都不算，逼人繞路去引現代版本或學者的年份，反而背離了這條檢查的
# 本意——有時間錨點 ≈ 真的讀過筆記，而不是「必須談到二十世紀」。
YEAR_RE = re.compile(
    r"公元前\s*\d+"      # 公元前 490 年 / 公元前 5 世紀
    r"|(?:19|20)\d{2}"   # 1894 / 2016，可不接「年」
    r"|\d{3,4}\s*年"     # 1545 年 / 800 年
    r"|\d{1,2}\s*世紀"   # 9 世紀 / 19 世紀
)

# 「點名可查證的東西」。舊版只認拉丁字母（英文原名、斜體英文書名），結果全中文
# 的書必定不合格——《29 張當票 2》這種台灣行業見聞錄根本沒有英文專有名詞可寫，
# 逼人硬塞只會為了過關而扭曲內容。這條的本意是「有沒有點名具體、可查證的對象」，
# 而中文書名《…》與人名同樣可查證，所以一併認列。
REF_RE = re.compile(
    r"（[A-Z][A-Za-z .\-']{3,}）"   # 中文名後接英文原名：柏拉圖（Plato）
    r"|_[A-Za-z][^_\n]{4,}_"        # 斜體英文書名：_The Republic_（不得跨行）
    r"|《[^》]{2,}》"                # 中文書名：《做工的人》
)

# 行內程式碼。底線在識別字裡很常見（`pg_visibility`），不先剝掉會讓斜體規則失控。
CODE_SPAN_RE = re.compile(r"`[^`\n]+`")

MIN_YEARS = 2
MIN_REFS = 3
MIN_LIMIT_SENTENCES = 3

# 筆記量低於此值的書，docs/ 底下通常只有章節 frontmatter、沒有任何內文。
# 這種書寫不出概覽——「完整摘要」規定只能取材自筆記，硬寫等於編造。
# 2026-08-13 實測：空殼書 21 個檔案合計約 1.5 KB，有內容的書最少也有數十 KB。
MIN_NOTES = 8000


def zh_len(t):
    return len(re.sub(r"\s+", "", t))


# CommonMark 的 right-flanking 規則：收尾的 ** 若「前面是標點、後面又不是空白或
# 標點」，就不算合法的收尾符號，整段粗體會原封不動印出星號。中文特別容易踩到，
# 因為 `**第一部〈基本原理〉**建立…` 的前面正好是 〉、後面正好是「建」。
# 2026-08-13 實測：26 本已完成的概覽裡有 8 本中招、共 31 處，線上全是裸星號。
# 兩種改法都可以：句號移到粗體外（`**…推進**。心`），或收尾後補一個全形冒號
# （`**第一部〈基本原理〉**：建立`）。goldmark 的 CJK 選項救不了這個，試過了。
_BOLD_RE = re.compile(r"\*\*([^*\n]{1,120}?)\*\*(.)")


def _is_punct(ch):
    return unicodedata.category(ch)[0] in "PS"


def dead_bold(txt):
    out = []
    for m in _BOLD_RE.finditer(txt):
        inner, nxt = m.group(1), m.group(2)
        if inner and _is_punct(inner[-1]) and not nxt.isspace() and not _is_punct(nxt):
            out.append(m.group(0))
    return out


def read_overview(repo):
    """回傳 (四段 dict, 是否為舊格式, 原始區塊)。找不到就全 None。"""
    p = os.path.join(repo, "site", "content", "_index.md")
    if not os.path.exists(p):
        return None, False, ""
    s = io.open(p, encoding="utf-8").read()

    legacy = "{{% details" in s and "深度概覽" in s
    start = s.find("{{% book-overview %}}")
    if start < 0:
        start = s.find("{{% details")
        if start < 0:
            return None, legacy, ""
    end = s.find("{{% /book-overview %}}")
    if end < 0:
        end = s.find("{{% /details %}}")
    blk = s[start:end] if end > start else s[start:]

    out = {}
    for name in SECTIONS + LEGACY:
        m = re.search(r"^##\s*%s\s*\n(.*?)(?=^##\s|\Z)" % re.escape(name), blk, re.S | re.M)
        if m:
            out[name] = m.group(1).strip()
    return out, legacy, blk


def check(repo):
    """回傳 (checks, meta)。checks 是 [(名稱, 通過?, 說明)]。"""
    secs, legacy, blk = read_overview(repo)
    checks = []
    if secs is None:
        return [("有深度概覽", False, "找不到 book-overview 或 details 區塊")], {}
    if not secs:
        return [("有深度概覽", False, "區塊存在但抓不到任何 ## 段落")], {}

    present = [n for n in SECTIONS if n in secs]
    checks.append((
        "四段齊全", len(present) == 4,
        "有 %d/4：%s%s" % (len(present), "、".join(present) or "無",
                          "（舊三段格式，需改寫）" if legacy and len(present) < 4 else ""),
    ))

    for n in SECTIONS:
        body = secs.get(n)
        lo, hi = SECTION_CHARS[n]
        if body is None:
            checks.append(("%s 長度" % n, False, "缺這一段"))
        else:
            L = zh_len(body)
            if L < lo:
                detail = "%d 字（需 %d–%d，太短）" % (L, lo, hi)
            elif L > hi:
                detail = "%d 字（需 %d–%d，灌水了）" % (L, lo, hi)
            else:
                detail = "%d 字（需 %d–%d）" % (L, lo, hi)
            checks.append(("%s 長度" % n, lo <= L <= hi, detail))

    txt = "".join(secs.get(n, "") for n in SECTIONS) or "".join(secs.values())

    # 去重後才算數。舊版用 len()，於是同一本書名重複點名三次、或同一個年份出現
    # 兩次就過門檻——那不是「點名了三個可查證的對象」，是同一個對象講三遍。
    # 2026-08-13 實測：37 本已通過的概覽裡有 2 本靠重複刷過（allure-of-gentleness
    # 的 16 處 refs 只對應 1 個相異年份；29-pawn-tickets-2 去重後 refs 剛好剩 3）。
    years = set(YEAR_RE.findall(txt))
    checks.append(("引用年份", len(years) >= MIN_YEARS,
                   "%d 個相異（需 ≥%d）" % (len(years), MIN_YEARS)))

    # 先把行內程式碼拿掉再找 refs。`pg_visibility`、`full_page_writes` 這類識別字
    # 帶底線，會被 `_斜體英文書名_` 那條規則當成斜體的起訖，把中間整段文字（連同
    # 其他所有 refs）吞成一個 match——去重後只剩 1 個，整份概覽就此判不合格。
    # 2026-08-13 實測：postgresql-14-internals 寫了 4 個人名加 2 本書名，卻被算成 2。
    refs = set(REF_RE.findall(CODE_SPAN_RE.sub(" ", txt)))
    checks.append(("點名可查證的作品／人名", len(refs) >= MIN_REFS,
                   "%d 個相異（需 ≥%d）" % (len(refs), MIN_REFS)))

    hits = [f for f in FILLER if f in txt]
    checks.append(("無空洞讚美", not hits, "、".join(hits) if hits else "無"))

    dead = dead_bold(txt)
    checks.append(("粗體都收得起來", not dead,
                   "、".join(d[:18] for d in dead[:3]) if dead else "無"))

    # 「限制」段最容易被寫成一句客套話帶過，所以單獨看它的實質內容
    lim = secs.get("這本書的限制", "")
    n_sent = len(re.findall(r"[。！？]", lim))
    checks.append((
        "限制段有實質內容", n_sent >= MIN_LIMIT_SENTENCES,
        "%d 個句子（需 ≥%d）" % (n_sent, MIN_LIMIT_SENTENCES),
    ))

    meta = dict(
        legacy=legacy,
        total=zh_len(txt),
        sections={n: zh_len(secs.get(n, "")) for n in SECTIONS},
        years=len(years), latin=len(refs), filler=len(hits),
    )
    return checks, meta


def note_volume(repo):
    """筆記總量（bytes）。只拿來排序，所以用檔案大小就夠——不必把 1618 本、
    數百 MB 的 markdown 讀進記憶體，那原本是全庫掃描最慢的一步。"""
    total = 0
    d = os.path.join(repo, "site", "content", "docs")
    for r, _, fs in os.walk(d):
        for f in fs:
            if f.endswith(".md"):
                try:
                    total += os.path.getsize(os.path.join(r, f))
                except OSError:
                    pass
    return total


def find_books(root):
    """找出 root 底下所有 Hugo book（含 site/content/_index.md 的目錄）。

    剪枝必須無條件做。舊版把 `dirs[:] = ...` 寫在過濾條件的 if 內，於是只有「已經
    走進 .git」的那一層才剪——換句話說每一個 repo 的整個物件庫都被走過一遍。
    註解宣稱全庫 6 秒，實測 32 秒，差的就是這裡。
    """
    skip = {".git", "build", "node_modules", "public", "resources"}
    out = []
    for r, dirs, fs in os.walk(root):
        dirs[:] = [d for d in dirs if d not in skip]
        if "hugo-book-template" in r:
            continue
        if r.endswith("/site/content") and "_index.md" in fs:
            out.append(os.path.dirname(os.path.dirname(r)))
    return sorted(out)


def is_written(repo):
    """這本書有沒有被 /book-generate-deep-overview 做過。

    這是「做過」的**正向標記**，而不是從「零 FAIL」反推。兩者平常看起來一樣，
    但在調整品檢標準的那一刻會分岔：判準若是「有沒有 FAIL」，每次把門檻調緊，
    做過的書就整批掉回待辦，然後被當成沒寫過的書整段重寫——而它們其實只差一個
    年份或一句話。做過但沒過品檢的書該進「待補修」，不是「待改寫」。

    標記取新格式區塊 + 四段齊全：舊的 details 三段格式永遠不滿足（段名不同），
    所以不會有舊書混進來。
    """
    p = os.path.join(repo, "site", "content", "_index.md")
    if not os.path.exists(p):
        return False
    if "{{% book-overview %}}" not in io.open(p, encoding="utf-8").read():
        return False
    secs, _, _ = read_overview(repo)
    return bool(secs) and all(n in secs for n in SECTIONS)


def run_one(repo, verbose=True):
    # 打錯路徑會走進 check() 然後報「找不到 book-overview 區塊」——聽起來像書有問題，
    # 其實是路徑有問題。永遠是 FAIL 所以不會放行壞概覽，但會浪費一輪追查。
    if not os.path.isdir(repo):
        print("路徑不存在：%s" % repo)
        return 1, {}
    checks, meta = check(repo)
    failed = [c for c in checks if not c[1]]
    if verbose:
        print("%s" % os.path.basename(repo))
        for name, ok, detail in checks:
            print("  %-4s %-16s %s" % ("PASS" if ok else "FAIL", name, detail))
        print("  → %s" % ("全部通過" if not failed else "%d 項未通過，回去重寫" % len(failed)))
    return len(failed), meta


def run_all(root, as_json):
    books = find_books(root)
    rows = []
    for b in books:
        checks, meta = check(b)
        nfail = sum(1 for c in checks if not c[1])
        rows.append(dict(
            slug=os.path.relpath(b, root), fails=nfail,
            failed=[c[0] for c in checks if not c[1]],
            notes=note_volume(b), **meta,
        ))
    if as_json:
        json.dump(rows, sys.stdout, ensure_ascii=False, indent=2)
        return 0

    n = len(rows)
    clean = sum(1 for r in rows if r["fails"] == 0)
    print("掃描 %d 本，全數通過 %d 本（%.0f%%）\n" % (n, clean, clean * 100.0 / max(n, 1)))
    agg = {}
    for r in rows:
        for f in r["failed"]:
            agg[f] = agg.get(f, 0) + 1
    print("各項未通過數：")
    for k, v in sorted(agg.items(), key=lambda x: -x[1]):
        print("  %-20s %5d 本  (%.0f%%)" % (k, v, v * 100.0 / n))

    # 筆記厚但概覽薄 = 素材就在那裡沒被用上，重做的投報率最高
    worst = sorted(rows, key=lambda r: (-r["fails"], -r.get("notes", 0)))
    print("\n最該重做的 30 本（未通過項數 → 筆記量）：")
    for r in worst[:30]:
        print("  [%d 項] %-46s 概覽 %5d 字 / 筆記 %6dk" % (
            r["fails"], r["slug"].split("/")[-1], r.get("total", 0), r.get("notes", 0) // 1000))
    return 0


def write_report(root, out_path, stamp):
    """產生 OVERVIEW-PROGRESS.md：改寫進度的單一真相。

    刻意寫成 markdown 而不是 JSON——這份是給人看的、要能在 GitHub 上直接讀，
    而且 diff 出來就是進度。機器要吃的話用 --json。
    """
    books = find_books(root)
    rows = []
    for b in books:
        checks, meta = check(b)
        nfail = sum(1 for c in checks if not c[1])
        rows.append(dict(slug=os.path.relpath(b, root), fails=nfail,
                         failed=[c[0] for c in checks if not c[1]],
                         notes=note_volume(b), written=is_written(b), **meta))
    n = len(rows)
    done = [r for r in rows if r["fails"] == 0]
    legacy = [r for r in rows if r.get("legacy")]
    pct = len(done) * 100.0 / max(n, 1)

    L = []
    L.append("# 深度概覽改寫進度\n")
    L.append("> 衍生檔，勿手改。重新產生：`python3 scripts/audit-overview.py --report`\n")
    L.append("最後更新：%s\n" % stamp)
    L.append("## 總覽\n")
    L.append("| | 數量 | 佔比 |")
    L.append("|---|---:|---:|")
    L.append("| 全庫 | %d | 100%% |" % n)
    L.append("| **已改寫並通過品檢** | **%d** | **%.1f%%** |" % (len(done), pct))
    L.append("| 仍是舊三段格式 | %d | %.1f%% |" % (len(legacy), len(legacy) * 100.0 / max(n, 1)))
    L.append("")
    bar = int(pct / 2)
    L.append("```\n[%s%s] %.1f%%\n```\n" % ("█" * bar, "·" * (50 - bar), pct))

    agg = {}
    for r in rows:
        for f in r["failed"]:
            agg[f] = agg.get(f, 0) + 1
    L.append("## 各項未通過\n")
    L.append("| 檢查 | 未通過 | 佔比 |")
    L.append("|---|---:|---:|")
    for k, v in sorted(agg.items(), key=lambda x: -x[1]):
        L.append("| %s | %d | %.0f%% |" % (k, v, v * 100.0 / n))
    L.append("")

    pending = [r for r in rows if r["fails"]]
    patch = sorted([r for r in pending if r["written"]], key=lambda r: r["slug"])
    pending = [r for r in pending if not r["written"]]
    hollow = sorted([r for r in pending if r["notes"] < MIN_NOTES], key=lambda r: r["slug"])
    todo = sorted([r for r in pending if r["notes"] >= MIN_NOTES], key=lambda r: r["slug"])
    L.append("## 待改寫（依 slug 排序）\n")
    L.append("刻意不按筆記量或未通過項數排序——挑書的順序由人決定，這裡只負責把還沒做的列全。\n")
    L.append("| # | 書 | 未通過 | 概覽字數 | 筆記 |")
    L.append("|---:|---|---:|---:|---:|")
    for i, r in enumerate(todo[:200], 1):
        L.append("| %d | `%s` | %d | %d | %dk |" % (
            i, r["slug"], r["fails"], r.get("total", 0), r["notes"] // 1000))
    if len(todo) > 200:
        L.append("")
        L.append("_（只列前 200 本，另有 %d 本未列出）_" % (len(todo) - 200))
    L.append("")

    if hollow:
        L.append("## 筆記是空的，寫不了（%d 本）\n" % len(hollow))
        L.append("docs/ 底下只有章節 frontmatter、沒有內文（< %d bytes）。"
                 "「完整摘要」規定只能取材自筆記，硬寫等於編造——先補筆記再回來。\n" % MIN_NOTES)
        for r in hollow:
            L.append("- `%s` — 筆記 %d bytes" % (r["slug"], r["notes"]))
        L.append("")

    if patch:
        L.append("## 已寫過、但有項目未過（%d 本）\n" % len(patch))
        L.append("四段都在，缺的是個別項目——補那一項就好，不要當成沒寫過整段重寫。"
                 "`--todo` 不會挑到這些書。\n")
        for r in patch:
            L.append("- `%s` — %s" % (r["slug"], "、".join(r["failed"])))
        L.append("")

    if done:
        L.append("## 已完成\n")
        for r in sorted(done, key=lambda r: r["slug"]):
            L.append("- `%s` — %d 字" % (r["slug"], r.get("total", 0)))
        L.append("")

    io.open(out_path, "w", encoding="utf-8").write("\n".join(L))
    print("寫入 %s（%d 本，已完成 %d）" % (out_path, n, len(done)))
    return 0


def run_todo(root, n):
    """印出接下來該做的 n 本 repo 路徑，一行一個。

    這是「中斷後續作」的入口：進度不存在任何狀態檔裡，而是每次重新從書本內容
    算出來——書本自己就是真相，而它們各自有 git remote。所以重開機、換機器、
    或把 OVERVIEW-PROGRESS.md 刪掉，都不會弄丟進度；重跑一次就回來了。

    整個掃描是純檔案讀取，不經過 LLM，全庫 1618 本約數秒，可以隨便重跑。

    排序刻意用 slug（而非未通過項數或筆記量）：挑書的順序是人的判斷，腳本只負責
    把還沒做的列全、而且每次列的順序一樣。

    stdout 只放「沒寫過的書」。另外兩類移到 stderr，因為它們的處置方式不同：
      - 筆記是空殼的：不是「還沒做」而是「做不了」，先補筆記。
      - 寫過但沒過品檢的：該補的是那一項，不是整段重寫（見 is_written）。
    """
    todo, hollow, patch = [], [], []
    for b in find_books(root):
        checks, _ = check(b)
        nfail = sum(1 for c in checks if not c[1])
        if not nfail:
            continue
        if is_written(b):
            patch.append((b, [c[0] for c in checks if not c[1]]))
        elif note_volume(b) < MIN_NOTES:
            hollow.append(b)
        else:
            todo.append(b)
    for b in sorted(todo)[:n]:
        print(b)
    if hollow:
        sys.stderr.write(
            "\n略過 %d 本筆記是空殼的書（docs/ 只有 frontmatter，< %d bytes）。"
            "完整摘要只能取材自筆記，硬寫等於編造：\n" % (len(hollow), MIN_NOTES))
        for b in sorted(hollow):
            sys.stderr.write("  %s\n" % b)
    if patch:
        sys.stderr.write(
            "\n略過 %d 本已寫過、但品檢有項目未過的書。這些要補的是個別項目，"
            "不要整段重寫：\n" % len(patch))
        for b, f in sorted(patch):
            sys.stderr.write("  %s — %s\n" % (b, "、".join(f)))
    return 0


def main():
    ap = argparse.ArgumentParser(description="深度概覽品檢")
    ap.add_argument("repo", nargs="?", help="單一書本 repo 路徑")
    ap.add_argument("--all", action="store_true", help="掃描整個 books-done")
    ap.add_argument("--todo", type=int, metavar="N",
                    help="印出接下來該做的 N 本 repo 路徑（續作用）")
    ap.add_argument("--report", action="store_true", help="產生 OVERVIEW-PROGRESS.md")
    ap.add_argument("--stamp", default="", help="報告的時間戳（呼叫端給，腳本不取系統時間）")
    ap.add_argument("--out", default=DEFAULT_OUT)
    ap.add_argument("--root", default=DEFAULT_ROOT)
    ap.add_argument("--json", action="store_true", help="與 --all 併用，輸出 JSON")
    a = ap.parse_args()

    if a.todo is not None:
        return run_todo(a.root, a.todo)
    if a.report:
        return write_report(a.root, a.out, a.stamp or "（未提供）")
    if a.all:
        return run_all(a.root, a.json)
    if not a.repo:
        ap.error("給一個 repo 路徑，或用 --all")
    nfail, _ = run_one(os.path.abspath(a.repo))
    return 1 if nfail else 0


if __name__ == "__main__":
    sys.exit(main())
