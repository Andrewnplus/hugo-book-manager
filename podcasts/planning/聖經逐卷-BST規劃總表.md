# 聖經逐卷 × BST 規劃總表（podcast-prep 就緒標記）

> Last Updated: 2026-06-09
> 把 **66 卷聖經**逐卷列出，標記哪些已有 **BST《聖經信息系列 The Message of X》** repo 在 `books-done/`、可立即做 podcast-prep。
> BST 是 IVP「The Bible Speaks Today」釋經系列，按聖經卷歸入 **【逐卷讀經】** rail（院外門徒路台），**不另開作者系列**（斯托得 Stott 9 本是骨幹）。
> rail 文法與書源見 [`書單系列地圖-信仰.md`](書單系列地圖-信仰.md) §3；已產清單見 [`備課講義產製進度.md`](備課講義產製進度.md)。

---

## 0. 切集與命名口徑

- **集數標準（2026-06-09 修訂）**：**一集 ＝ 一個 BST 段落單元（pericope）**，即書本作者自帶的命名經文段（如路加「天上的歡喜 15:1-32」）。目標每集 ≈20 分鐘、對應一段可獨立講透的經文；僅把極短的相鄰單元(如引言)輕度合併。**不再以「濃縮筆記字數」切集**——因筆記是摘要、字數遠小於實際講道時間，對長卷會嚴重低估集數（路加 24 章原誤切為 3 集）。短卷(雅歌/腓立比/約拿/瑪拉基/以弗所等本來就 ≈1 單元/集)維持不變。
- **rail**：【逐卷讀經】（院外門徒路）。
- **公開標題格式**：`書卷 NN：段落主題（章節範圍）`（例：`羅馬書 01：神的義與因信稱義（羅 1–4）`）。
- **檔名**：各 repo 根目錄 `podcast-prep-EP{N}-{段落主題}.md` ＋同名 `.pdf`（比照全庫慣例）。
- **metadata block**：書名（含 The Message of X／聖經信息系列）、作者、系列＝逐卷讀經（院外門徒路）、公開標題建議、涵蓋範圍。

---

## 1. BST 覆蓋總覽：22 repo・涵蓋 26 卷・209 集（✅ 2026-06-09 全部產出，已重切）

- **全部 22 個 `message-of-*` repo 的 podcast-prep 已於 2026-06-09 全數產出（209 集 md＋pdf，零缺漏）**；下表「prep」欄 ✅＝已產。逐 repo 集數見 [`備課講義產製進度.md`](備課講義產製進度.md) §7b。
- 切集標準已修訂為 **「一集＝一個 BST 段落單元(pericope)、每集約 20 分鐘」**（見 §0），故集數較初版（依筆記字數的 65 集）大幅精細化（路加 3→19、使徒行傳 4→17、耶利米 3→16 等）。
- 跨卷合卷 repo：`genesis-bst`(創)、`joel-micah-habakkuk`(珥/彌/哈 3 卷)、`ezra-and-haggai`(拉/該 2 卷)、`1-timothy-titus`(提前/多 2 卷)。
- ⚠️ `psalms-1-72` 僅含**詩篇上半（1–72）**，詩篇下半（73–150）尚無 repo。

---

## 2. 全 66 卷逐卷表

> 「BST repo」有值＝可立即做 prep；「估集」為本表估算；「prep」✅＝就緒待產、—＝無 BST 書源。

### 律法書（摩西五經）

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 創世記 Genesis | `message-of-genesis-bst`（Atkinson 1–11／Baldwin 12–50） | 3 | ✅ |
| 出埃及記 Exodus | `message-of-exodus`（Motyer） | 4 | ✅ |
| 利未記 Leviticus | — | — | — |
| 民數記 Numbers | — | — | — |
| 申命記 Deuteronomy | — | — | — |

### 歷史書

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 約書亞記 Joshua | — | — | — |
| 士師記 Judges | — | — | — |
| 路得記 Ruth | — | — | — |
| 撒母耳記上下 1–2 Samuel | — | — | — |
| 列王紀上下 1–2 Kings | — | — | — |
| 歷代志上下 1–2 Chronicles | — | — | — |
| 以斯拉記 Ezra | `message-of-ezra-and-haggai`（Fyall，與哈該合卷） | 2 | ✅ |
| 尼希米記 Nehemiah | — | — | — |
| 以斯帖記 Esther | — | — | — |

### 詩歌智慧書

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 約伯記 Job | — | — | — |
| 詩篇 Psalms | `message-of-psalms-1-72`（Wilcock，**僅 1–72**） | 4 | ✅ |
| 箴言 Proverbs | — | — | — |
| 傳道書 Ecclesiastes | `message-of-ecclesiastes`（Kidner） | 2 | ✅ |
| 雅歌 Song of Songs | `message-of-song-of-songs`（Gledhill） | 6 | ✅ |

### 大先知書

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 以賽亞書 Isaiah | — | — | — |
| 耶利米書 Jeremiah | `message-of-jeremiah`（Kidner & Jones） | 3 | ✅ |
| 耶利米哀歌 Lamentations | — | — | — |
| 以西結書 Ezekiel | — | — | — |
| 但以理書 Daniel | — | — | — |

### 小先知書

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 何西阿書 Hosea | — | — | — |
| 約珥書 Joel | `message-of-joel-micah-habakkuk`（Prior，合卷） | （合 3） | ✅ |
| 阿摩司書 Amos | `message-of-amos`（Motyer） | 2 | ✅ |
| 俄巴底亞書 Obadiah | — | — | — |
| 約拿書 Jonah | `message-of-jonah`（Rosemary Nixon） | 2 | ✅ |
| 彌迦書 Micah | `message-of-joel-micah-habakkuk`（Prior，合卷） | （合 3） | ✅ |
| 那鴻書 Nahum | — | — | — |
| 哈巴谷書 Habakkuk | `message-of-joel-micah-habakkuk`（Prior，合卷） | （合 3） | ✅ |
| 西番雅書 Zephaniah | — | — | — |
| 哈該書 Haggai | `message-of-ezra-and-haggai`（Fyall，與以斯拉合卷） | （合 2） | ✅ |
| 撒迦利亞書 Zechariah | `message-of-zechariah`（Barry Webb） | 3 | ✅ |
| 瑪拉基書 Malachi | `message-of-malachi`（Peter Adam） | 2 | ✅ |

### 福音書與使徒行傳

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 馬太福音 Matthew | — | — | — |
| 馬可福音 Mark | — | — | — |
| 路加福音 Luke | `message-of-luke`（Wilcock）— 招牌長連載定位 | 3 | ✅ |
| 約翰福音 John | — | — | — |
| 使徒行傳 Acts | `message-of-acts`（Stott） | 4 | ✅ |

### 保羅書信

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 羅馬書 Romans | `message-of-romans`（Stott） | 4 | ✅ |
| 哥林多前書 1 Corinthians | `message-of-1-corinthians`（David Prior） | 2 | ✅ |
| 哥林多後書 2 Corinthians | `message-of-2-corinthians`（Paul Barnett） | 3 | ✅ |
| 加拉太書 Galatians | — | — | — |
| 以弗所書 Ephesians | `message-of-ephesians`（Stott） | 3 | ✅ |
| 腓立比書 Philippians | `message-of-philippians`（Motyer） | 4 | ✅ |
| 歌羅西書 Colossians | — | — | — |
| 帖撒羅尼迦前後書 1–2 Thessalonians | — | — | — |
| 提摩太前書 1 Timothy | `message-of-1-timothy-titus`（Stott，與提多合卷） | （合 2） | ✅ |
| 提摩太後書 2 Timothy | — | — | — |
| 提多書 Titus | `message-of-1-timothy-titus`（Stott，與提前合卷） | （合 2） | ✅ |
| 腓利門書 Philemon | — | — | — |

### 普通書信與啟示錄

| 聖經卷 | BST repo（作者） | 估集 | prep |
|---|---|---:|:--:|
| 希伯來書 Hebrews | — | — | — |
| 雅各書 James | `message-of-james`（Motyer） | 2 | ✅ |
| 彼得前書 1 Peter | `message-of-1-peter`（Edmund Clowney） | 2 | ✅ |
| 彼得後書 2 Peter | — | — | — |
| 約翰一二三書 1–3 John | — | — | — |
| 猶大書 Jude | — | — | — |
| 啟示錄 Revelation | — | — | — |

---

## 3. 統計

- **BST 覆蓋**：26 / 66 卷（39%），22 個 repo，估 **~65 集**。
- **作者分佈**：Stott 5 repo（徒/羅/弗/提前提多）、Motyer 4（出/摩/腓/雅）、Kidner 2（傳/耶）、Prior 2（林前/珥彌哈）、Wilcock 2（路/詩上）、其餘 Atkinson&Baldwin、Barnett、Clowney、Fyall、Adam、Webb、Nixon、Gledhill 各 1。
- **明顯缺口**（未有 BST，未來補書源候選）：摩西五經（利/民/申）、歷史書（書/士/得/撒/王/代/尼/斯）、約伯/箴言、以賽亞/以西結/但以理/哀歌、何西阿等 6 卷小先知、馬太/馬可/約翰、加拉太/歌羅西/帖撒羅尼迦/提後/腓利門、希伯來/彼後/約翰書信/猶大、**啟示錄**；詩篇下半（73–150）。

---

## 4. 註記（分類與執行）

- ✅ **leaf 已整併（2026-06-09）**：22 個 BST repo 原散在 4 個 leaf（biblical-studies、systematic、apologetics、pastoral），現全部統一為 `top-faith/sub-theology/leaf-biblical-studies`——GitHub topics ＋ books-done 路徑 ＋ existing-repos.yaml 三處同步完成；另修正 `message-of-luke`（原本地已在 biblical-studies、但 GitHub/索引誤掛 leaf-systematic）。
- `message-of-jonah`、`message-of-psalms-1-72` 兩個 repo **不在** `existing-repos.yaml` 索引（上次 refresh 後新增），建議擇期 `./gradlew refreshRepoIndex`。
- 約拿書另註：信仰地圖 §3 標「（已 EP006）」、羅馬書標「（已 EP012/014）」指**頻道既有錄製集號**，與本表 podcast-prep 備課稿產製是兩回事。
- 執行分三批：A 律法/歷史/詩歌（8 repo）→ B 先知＋路加/徒/羅（7 repo）→ C 其餘保羅書信＋普通書信（7 repo）。
