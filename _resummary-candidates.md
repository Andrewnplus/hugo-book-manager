# Bookstown 摘要重做候選清單

> [!IMPORTANT]
>
> **數字不再由這份檔案維護。** 哪些書太薄、薄多少，改看 <https://nplus.wiki/health/>；
> 門檻的唯一正本是 `BookHealthService`（`NEAR_EMPTY_DENSITY` / `THIN_CHARS` / `WATCH_CHARS`），
> 由 `./gradlew refreshBookHealth` 產生 portal 的 `src/data/health.json`。
>
> 這份檔案保留的是**儀表板算不出來的人工判斷**：找書連結、`📄` PDF 是否到位、
> 🗑️ 刪除／換源的裁決、以及已刪 repo 的歷史紀錄。下方表格中的字數欄位是
> 2026-08-17 的快照，僅供對照，不要據以更新。
>
> **「放掉」（盤點過、判定無法再加厚、不再追）不記在這裡**——用 `/book-waive <slug>` 記進
> portal 的 `src/data/health-waivers.yaml`，儀表板會把它移出待補榜單、列進頁尾「已放掉」名單。
> 這裡的 🗑️ 是刪除候選，不等於放掉；真的刪了就進下方「已刪 repo 紀錄」。

> 重新盤點 2026-08-17・掃描 `books-done` 下 **1,758** 個 repo（已排除 build 產物、template、tmp）。
> 判準：**總字元**（整本 MD body 量）＋**每頁均**（總字元÷章節頁數）。全庫中位數：總字元 66,916、每頁均 3,512；p10：總字元 24,255、每頁均 1,277。
> 本輪 vs 2026-07-12：**11 本通過門檻移出**、**1 本新入榜**（🆕）、**1 本已刪**（`kiss-your-but-good-bye`）。目前待處理 **72 本**
> （2026-08-18 再刪 `whats-left-without-your-business-card`、`hbrs-guide-to-emotional-intelligence`，見文末紀錄）。
> 全庫較 07-12 明顯變厚：中位數總字元 55,327 → 66,916、每頁均 2,987 → 3,512。

## 圖例

- **📄** = 原始 PDF 已在 repo 根目錄，可直接開 IntelliJ 用 `/book-rewrite-content` 重做。
- **🆕** = 本輪新入榜。
- **‼️** = 尚無找書連結，開工前要先找來源。
- **🗑️** = 已在下方刪除／換源候選名單上，動工前先確認要不要留。
- 找書連結：英文原著→OceanofPDF；非英文（欄尾 ·日／·中／·韓／·德）→Readmoo。`Readmoo 無`＝查無電子書 → 見下方刪除／換源候選。

> [!CAUTION]
>
> **「骨架完整＋內文空」有兩種成因，只看檔案分不出來。** 上一輪把 6 本列為
> 「🚧 重做骨架已建、正文未寫」並註明「別當新案重開」，但其中 5 本其實是 2026-08-09
> 的 `fix: correct corrupted transliterated names` 把中文內文當亂碼刪掉造成的資料損失，
> 那句註記讓它們被連續跳過。判定前先看 git 歷史的內容峰值：現況薄且**歷史也一直薄**
> 才是真的沒寫；**歷史曾經厚**就是內容遺失，要先救回。
>
> 反向的坑：早期 repo 都從 `讀書筆記模版` 起手（`docs/1/1/`…`docs/1/10/` 佔位頁，約 483 行），
> 第一次寫實際內容時會刪掉那些佔位頁，看起來像「遺失數百行」。那是假訊號，不要當災情。

---

## 🚧 進行中｜正在改寫、尚未 commit（勿重複開工）

> 這些 repo 的工作區有未 commit 的內容改動，是在途工作。**內容以工作區為準**，
> 別依 GitHub 上的狀態判斷完成度。

| 書名 | Repo 路徑 | 未 commit | 現況總字元 |
|---|---|---:|---:|
| 生命是長期而持續的累積 | `personal/relationships/community/life-is-a-long-term-accumulation` | 9 項 | 154,669 |

## 待處理清單

### 第一優先｜近乎空殼（每頁均 < 250）（1 本）

| 書名 | Repo 路徑 | 總字元 | 每頁均 | 找書連結 |
|---|---|---:|---:|---|
| 說話的品格 | `professional/communication/persuasion/dignity-of-speaking` | 6,648 | 229 | [Readmoo](https://readmoo.com/book/210330713000101) ·韓 |

### 第二優先｜內容稀薄（總字元 < 8,000）（15 本）

| 書名 | Repo 路徑 | 總字元 | 每頁均 | 找書連結 |
|---|---|---:|---:|---|
| 29張當票3：門簾外的人生鑑定 | `personal/mindset/growth/29-pawn-tickets-3` | 1,250 | 417 | [Readmoo](https://readmoo.com/book/210019712000101) ·中 |
| 程式設計師之禪 | `craft/engineering/coding-practice/zen-programmer` | 3,470 | 434 | [OceanofPDF](https://oceanofpdf.com/?s=the+zen+programmer) ｜ [Readmoo](https://readmoo.com/book/210119373000101) ·英 |
| 麥肯錫教我的思考武器：從邏輯思考到真正解決問題 | `professional/career/problem-solving/issue-driven` | 3,680 | 613 | [Readmoo](https://readmoo.com/book/210201484000101) ·日 |
| 學上當 | `personal/habit/productivity/learning-to-be-deceived` | 4,212 | 702 | [Readmoo](https://readmoo.com/book/210072176000101) ·中 |
| 幽谷之旅 | `faith/theology/apologetics/shadowlands` | 4,567 | 351 | [shadowlands](https://oceanofpdf.com/?s=shadowlands) (?) |
| 向下的移動 | `faith/theology/systematic/selfless-way-of-christ` | 4,822 | 964 | [the+selfless+way+of+christ](https://oceanofpdf.com/?s=the+selfless+way+of+christ) |
| 沒人敢告訴你的MBA大揭密 🗑️ | `professional/communication/persuasion/mba-confidential` | 4,992 | 416 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E6%B2%92%E4%BA%BA%E6%95%A2%E5%91%8A%E8%A8%B4%E4%BD%A0%E7%9A%84MBA%E5%A4%A7%E6%8F%AD%E5%AF%86) ·原 |
| 教出孩子的生存力 | `personal/relationships/parenting/raising-children-with-survival-skills` | 5,238 | 748 | [Readmoo](https://readmoo.com/book/210118288000101) ·日 |
| 約櫃流浪記 | `faith/theology/systematic/ichabod-toward-home` | 5,242 | 749 | [ichabod+toward+home](https://oceanofpdf.com/?s=ichabod+toward+home) |
| 跟任何人都可以聊得來 | `professional/communication/persuasion/how-to-talk-to-anyone` | 5,702 | 634 | [OceanofPDF](https://oceanofpdf.com/?s=how+to+talk+to+anyone) ｜ [Readmoo](https://share.readmoo.com/book/118779) ·英 |
| 哈佛教你打造健康人生 🗑️ | `personal/wellness/mental-health/harvard-guide-to-a-healthy-life` | 6,203 | 517 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%93%88%E4%BD%9B%E6%95%99%E4%BD%A0%E6%89%93%E9%80%A0%E5%81%A5%E5%BA%B7%E4%BA%BA%E7%94%9F) ·H |
| 上帝的混沌理論 | `faith/theology/systematic/when-god-interrupts` | 6,488 | 721 | [when+god+interrupts](https://oceanofpdf.com/?s=when+god+interrupts) |
| 你的不安，是因為太習慣受傷害 | `personal/relationships/community/your-anxiety-comes-from-being-too-used-to-getting-hurt` | 6,777 | 968 | [Readmoo](https://readmoo.com/book/210469737000101) ·韓 |
| 讀書這個荒野 | `wisdom/philosophy/ethics/reading-as-a-wilderness` | 7,299 | 912 | [Readmoo](https://readmoo.com/book/210114739000101) ·日 |
| 29張當票2：當舖裡特有的人生風景 | `personal/mindset/growth/29-pawn-tickets-2` | 7,468 | 2,489 | [Readmoo](https://readmoo.com/book/210003324000101) ·中 |

### 觀察名單｜邊緣偏薄（總字元 8,000–15,000）（56 本）

| 書名 | Repo 路徑 | 總字元 | 每頁均 | 找書連結 |
|---|---|---:|---:|---|
| 未來最需要的新人才 | `professional/career/skill-building/new-talent-for-the-future` | 8,004 | 1,001 | [Readmoo](https://readmoo.com/book/210118641000101) ·日 |
| 比翼雙飛 🗑️ | `faith/theology/systematic/flying-together-a-christian-marriage-guide` | 8,024 | 669 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E6%AF%94%E7%BF%BC%E9%9B%99%E9%A3%9B) ·中 |
| 煩惱都是自己想出來的 🗑️ | `personal/habit/productivity/worries-are-all-in-your-head` | 8,031 | 892 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E7%85%A9%E6%83%B1%E9%83%BD%E6%98%AF%E8%87%AA%E5%B7%B1%E6%83%B3%E5%87%BA%E4%BE%86%E7%9A%84) ·日 |
| Culture Making | `faith/theology/systematic/culture-making` | 8,074 | 336 | [culture+making](https://oceanofpdf.com/?s=culture+making) |
| Rich Kids: How to Raise Our Children to Be Happy and Successful in Life | `personal/habit/productivity/rich-kids` | 8,187 | 1,170 | [rich+kids+thomas+corley](https://oceanofpdf.com/?s=rich+kids+thomas+corley) |
| 寫給每個人的社會學讀本 🗑️ | `wisdom/education/self-learning/sociology-for-everyone` | 8,515 | 608 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%AF%AB%E7%B5%A6%E6%AF%8F%E5%80%8B%E4%BA%BA%E7%9A%84%E7%A4%BE%E6%9C%83%E5%AD%B8%E8%AE%80%E6%9C%AC) ·日 |
| 麥肯錫の零秒邏輯思考 🗑️ | `professional/career/problem-solving/mckinsey-zero-second-logical-thinking` | 8,673 | 1,735 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=McKinsey%20Zero-Second%20Logical%20Thinking) ·日 |
| 就業的終結 | `professional/career/skill-building/end-of-jobs` | 8,995 | 500 | [the+end+of+jobs](https://oceanofpdf.com/?s=the+end+of+jobs) |
| 社會的趨勢 | `wisdom/history/civilization/new-realities` | 9,343 | 667 | [the+new+realities+drucker](https://oceanofpdf.com/?s=the+new+realities+drucker) |
| 読書力 🗑️ | `wisdom/education/self-learning/power-of-reading` | 9,362 | 1,872 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E8%AA%AD%E6%9B%B8%E5%8A%9B) ·日 |
| 凝視生命--奇士勞斯基《十誡》的神學美學 🗑️ | `wisdom/philosophy/ethics/gazing-at-life-theological-aesthetics-of-the-decalogue` | 9,475 | 592 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%87%9D%E8%A6%96%E7%94%9F%E5%91%BD) ·中 |
| 大人學選擇 | `personal/mindset/growth/art-of-adult-decision-making` | 9,671 | 1,382 | [Readmoo](https://readmoo.com/book/210254770000101) ·中 |
| 是與非以外：基督教的倫理想像 | `faith/theology/systematic/toward-a-christian-moral-imagination` | 9,796 | 490 | [Readmoo](https://readmoo.com/book/210190337000101) ·中 |
| 上台的技術 | `professional/communication/public-speaking/stage-presentation-skills` | 9,815 | 1,091 | [Readmoo](https://readmoo.com/book/240032622000101) ·中 |
| 一流的人讀書，都在哪裡畫線？ | `wisdom/education/self-learning/where-do-top-performers-draw-the-line-when-reading` | 10,130 | 1,013 | [Readmoo](https://readmoo.com/book/210185855000101) ·日 |
| 麥肯錫的筆記術 🗑️ | `professional/career/problem-solving/mckinsey-note-taking-method` | 10,362 | 1,727 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=McKinsey%20Note-Taking%20Method) ·日 |
| 將世界菁英的工作方式整理成冊 | `professional/career/skill-building/world-class-work-methods-compiled` | 10,448 | 950 | [Readmoo](https://readmoo.com/book/210333180000101) ·日 |
| 📄 忘我的自由 | `faith/theology/systematic/freedom-of-self-forgetfulness` | 10,887 | 2,177 | 📄 已在 repo｜[freedom of self forgetfulness](https://oceanofpdf.com/?s=freedom+of+self+forgetfulness) |
| 心靈療癒自助手冊 | `personal/mindset/emotion/your-mind-an-owners-manual-for-a-better-life` | 11,099 | 1,009 | [your+mind+an+owners+manual](https://oceanofpdf.com/?s=your+mind+an+owners+manual) |
| 📄 不懂財報，也能輕鬆選出賺錢績優股 | `professional/finance/investing/five-key-numbers` | 11,234 | 1,248 | [Readmoo](https://readmoo.com/book/210067909000101) ·原 |
| 📄 順服的主 🗑️ | `faith/theology/systematic/obedient-master` | 11,250 | 3,750 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E9%A0%86%E6%9C%8D%E7%9A%84%E4%B8%BB) ·中 |
| 在咖啡廳遇見彼得．杜拉克 🗑️ | `professional/business/management/peter-drucker-cafe` | 11,292 | 1,613 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%9C%A8%E5%92%96%E5%95%A1%E5%BB%B3%E9%81%87%E8%A6%8B%E5%BD%BC%E5%BE%97%EF%BC%8E%E6%9D%9C%E6%8B%89%E5%85%8B) ·日 |
| 📄 友善的雪花 | `faith/spiritual-formation/devotional/friendly-snowflake` | 11,365 | 2,273 | [the+friendly+snowflake](https://oceanofpdf.com/?s=the+friendly+snowflake) |
| 父母會傷人 | `personal/relationships/parenting/toxic-parents` | 11,449 | 603 | [toxic+parents](https://oceanofpdf.com/?s=toxic+parents) |
| 和艦長一起 30 天玩轉 GitLab | `craft/engineering/devops/30-days-of-gitlab` | 11,604 | 1,934 | [Readmoo](https://readmoo.com/book/210262575000101) ·中 |
| If You're Not First, You're Last | `professional/business/sales/if-youre-not-first-youre-last` | 11,815 | 1,313 | [if+youre+not+first+youre+last](https://oceanofpdf.com/?s=if+youre+not+first+youre+last) |
| 喧囂中的寧靜 | `faith/theology/systematic/way-of-the-heart` | 12,200 | 2,440 | [the+way+of+the+heart+nouwen](https://oceanofpdf.com/?s=the+way+of+the+heart+nouwen) |
| 講道與講道的人 | `faith/theology/pastoral/preaching-and-preachers` | 12,274 | 722 | [preaching+and+preachers](https://oceanofpdf.com/?s=preaching+and+preachers) |
| 康乃爾最經典的思考邏輯課 | `personal/mindset/growth/how-we-know-what-isnt-so` | 12,406 | 827 | [how+we+know+what+isnt+so](https://oceanofpdf.com/?s=how+we+know+what+isnt+so) |
| 親密關係 - 通往靈魂之橋 | `personal/relationships/dating/relationship-bridge-to-the-soul` | 12,420 | 1,774 | [relationship+bridge+to+the+soul](https://oceanofpdf.com/?s=relationship+bridge+to+the+soul) (?) |
| 選民進化論 | `professional/leadership/vision/wont-get-fooled-again` | 12,573 | 967 | [wont+get+fooled+again](https://oceanofpdf.com/?s=wont+get+fooled+again) (?) |
| Just Shut Up and Do It | `personal/habit/productivity/just-shut-up-and-do-it` | 12,667 | 1,583 | [just+shut+up+and+do+it](https://oceanofpdf.com/?s=just+shut+up+and+do+it) |
| 成功人士一定會做的9件事情 | `personal/mindset/growth/nine-things-successful-people-do-differently` | 12,724 | 1,272 | [nine+things+successful+people+do+differently](https://oceanofpdf.com/?s=nine+things+successful+people+do+differently) |
| 📄 杜拉克談自我管理 | `professional/career/skill-building/managing-oneself` | 12,822 | 1,603 | [managing+oneself](https://oceanofpdf.com/?s=managing+oneself) |
| 這一生，你想留下什麼 | `professional/communication/storytelling/leading-matters` | 12,864 | 1,072 | [leading+matters](https://oceanofpdf.com/?s=leading+matters) |
| 📄 人之廢 | `wisdom/philosophy/ethics/abolition-of-man` | 12,939 | 3,235 | [the+abolition+of+man](https://oceanofpdf.com/?s=the+abolition+of+man) |
| 📄 愛勝過恐懼 | `faith/theology/systematic/lifesigns` | 12,945 | 925 | 📄 已在 repo｜[lifesigns nouwen](https://oceanofpdf.com/?s=lifesigns+nouwen) |
| 哈佛教你做好自我管理 | `personal/habit/productivity/hbr-10-must-reads-on-managing-yourself` | 12,973 | 1,081 | [hbr+10+must+reads+on+managing+yourself](https://oceanofpdf.com/?s=hbr+10+must+reads+on+managing+yourself) |
| 📄 愛的藝術 | `wisdom/philosophy/ethics/art-of-loving` | 13,110 | 3,278 | 📄 已在 repo｜[the art of loving fromm](https://oceanofpdf.com/?s=the+art+of+loving+fromm) |
| 平等 | `wisdom/philosophy/political-philosophy/equality` | 13,116 | 1,457 | [Readmoo](https://readmoo.com/book/210123416000101) ·原 |
| 異類僑居者：基督徒的倫理與政治 | `faith/theology/systematic/resident-aliens-life-in-the-christian-colony` | 13,247 | 828 | [resident+aliens](https://oceanofpdf.com/?s=resident+aliens) |
| 財務自由實踐版 | `professional/career/skill-building/work-optional` | 13,588 | 647 | [work+optional](https://oceanofpdf.com/?s=work+optional) |
| 世界在等待的門徒 | `faith/theology/systematic/radical-disciple` | 13,601 | 1,511 | [the+radical+disciple](https://oceanofpdf.com/?s=the+radical+disciple) |
| 有錢人想的和你不一樣 | `personal/mindset/growth/secrets-of-the-millionaire-mind` | 14,123 | 673 | [OceanofPDF](https://oceanofpdf.com/?s=secrets+of+the+millionaire+mind) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E6%9C%89%E9%8C%A2%E4%BA%BA%E6%83%B3%E7%9A%84%E5%92%8C%E4%BD%A0%E4%B8%8D%E4%B8%80%E6%A8%A3) ·英 |
| HBR 經理人財務基礎指南 | `professional/business/management/hbr-guide-to-finance-basics-for-managers` | 14,153 | 4,718 | [hbr+guide+to+finance+basics+for+managers](https://oceanofpdf.com/?s=hbr+guide+to+finance+basics+for+managers) |
| 📄 The Prodigal Prophet: Jonah and the Mystery of God's Mercy | `faith/theology/systematic/prodigal-prophet` | 14,214 | 1,093 | [the+prodigal+prophet](https://oceanofpdf.com/?s=the+prodigal+prophet) |
| 工作與生活的技術 | `personal/habit/productivity/art-of-work-and-life` | 14,224 | 459 | [Readmoo](https://readmoo.com/search/keyword?q=%E5%B7%A5%E4%BD%9C%E8%88%87%E7%94%9F%E6%B4%BB%E7%9A%84%E6%8A%80%E8%A1%93) ·中 |
| 📄 記憶的治療者 | `faith/theology/pastoral/living-reminder` | 14,230 | 2,846 | [the+living+reminder](https://oceanofpdf.com/?s=the+living+reminder) |
| 📄 蒙格之道：關於投資、閱讀、工作與幸福的普通常識 | `wisdom/philosophy/ethics/way-of-munger` | 14,405 | 1,601 | [Readmoo](https://readmoo.com/book/210287274000101) ·中 |
| 探索人格潛能，看見更真實的自己 | `personal/mindset/growth/me-myself-and-us` | 14,550 | 1,213 | [OceanofPDF](https://oceanofpdf.com/?s=me+myself+and+us+brian+little) ·英 |
| 你要如何衡量你的人生 | `personal/mindset/resilience/how-will-you-measure-your-life` | 14,630 | 975 | [OceanofPDF](https://oceanofpdf.com/?s=how+will+you+measure+your+life) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E4%BD%A0%E8%A6%81%E5%A6%82%E4%BD%95%E8%A1%A1%E9%87%8F%E4%BD%A0%E7%9A%84%E4%BA%BA%E7%94%9F) ·英 |
| 你的身體，正在洩漏你的秘密 | `professional/communication/persuasion/ohne-worte` | 14,694 | 816 | [OceanofPDF](https://oceanofpdf.com/?s=ohne+worte+koerpersprache)（德文原著）｜ [Readmoo](https://readmoo.com/search/keyword?q=%E4%BD%A0%E7%9A%84%E8%BA%AB%E9%AB%94%E6%AD%A3%E5%9C%A8%E6%B4%A9%E6%BC%8F%E4%BD%A0%E7%9A%84%E7%A7%98%E5%AF%86) ·德 |
| 石版上的聖言 🆕 | `faith/theology/systematic/written-in-stone` | 14,782 | 1,056 | ‼️ 待補找書連結 |
| 📄 深度學習力 | `wisdom/education/self-learning/how-to-be-a-high-school-superstar` | 14,794 | 548 | [OceanofPDF](https://oceanofpdf.com/?s=how+to+be+a+high+school+superstar) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E6%B7%B1%E5%BA%A6%E5%AD%B8%E7%BF%92%E5%8A%9B) ·英 |
| SCRUM：用一半的時間做兩倍的事 | `craft/engineering/agile/scrum-the-art-of-doing-twice-the-work-in-half-the-time` | 14,969 | 1,663 | [OceanofPDF](https://oceanofpdf.com/?s=scrum+the+art+of+doing+twice+the+work) ｜ [Readmoo](https://readmoo.com/search/keyword?q=SCRUM%20%E7%94%A8%E4%B8%80%E5%8D%8A%E7%9A%84%E6%99%82%E9%96%93%E5%81%9A%E5%85%A9%E5%80%8D%E7%9A%84%E4%BA%8B) ·英 |
| 擁抱B選項 | `personal/mindset/resilience/option-b` | 14,987 | 1,249 | [OceanofPDF](https://oceanofpdf.com/?s=option+b+sandberg) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E6%93%81%E6%8A%B1B%E9%81%B8%E9%A0%85) ·英 |

---

## 🗑️ 刪除／換源候選（Readmoo 無電子書、來源難取得）

> 查無電子書、擴充只能另尋來源。日系 McKinsey／速讀類與前一輪已刪的一批同型，**營養價值低、建議刪**；標註「保留」者為前版 NOTE 指定的中文價值書，留待換源。總字元為 2026-08-17 實測值。

| 書名 | slug | 總字元 | 語言 | 處置建議 |
|---|---|---:|:--:|---|
| 沒人敢告訴你的MBA大揭密 | `mba-confidential` | 4,992 | 原 | 🗑️ 刪除候選 |
| 哈佛教你打造健康人生 | `harvard-guide-to-a-healthy-life` | 6,203 | H | 🗑️ 刪除候選 |
| 比翼雙飛 | `flying-together-a-christian-marriage-guide` | 8,024 | 中 | 🗑️ 刪除候選 |
| 煩惱都是自己想出來的 | `worries-are-all-in-your-head` | 8,031 | 日 | 🗑️ 刪除候選 |
| 寫給每個人的社會學讀本 | `sociology-for-everyone` | 8,515 | 日 | 🗑️ 刪除候選 |
| McKinsey Zero-Second Logical Thinking | `mckinsey-zero-second-logical-thinking` | 8,673 | 日 | 🗑️ 刪除候選 |
| 読書力 | `power-of-reading` | 9,362 | 日 | 🗑️ 刪除候選 |
| 凝視生命--奇士勞斯基《十誡》的神學美學 | `gazing-at-life-theological-aesthetics-of-the-decalogue` | 9,475 | 中 | ⚠️ 保留（NOTE 指定） |
| McKinsey Note-Taking Method | `mckinsey-note-taking-method` | 10,362 | 日 | 🗑️ 刪除候選 |
| 順服的主 | `obedient-master` | 11,250 | 中 | ⚠️ 保留（NOTE 指定） |
| 在咖啡廳遇見彼得．杜拉克 | `peter-drucker-cafe` | 11,292 | 日 | 🗑️ 刪除候選 |

## ✅ 已通過門檻、移出清單（累計 44 本）

> 已重做/擴充到 total > 15,000，下輪掃描直接跳過。本輪新增：`attitude-of-the-rich`、`bible-suspense-investigation`、`elephant-and-the-flea`、`financial-boundaries-with-family`、`great-work-great-career`、`life-is-a-long-term-accumulation`、`live-your-best-life`、`reinforcements-how-to-get-people-to-help-you`、`rich-dads-conspiracy-of-the-rich`、`winning`、`world-only-readers-can-reach`。

`29-pawn-tickets`, `adversity-quotient`, `aging`, `attitude-of-the-rich`, `bible-suspense-investigation`, `bonhoeffer-spiritual-care-in-a-religionless-age`, `charisma-myth`, `course-of-love`, `deep-work`, `do-over`, `eat-this-book`, `elephant-and-the-flea`, `end-of-average`, `financial-boundaries-with-family`, `god-is-closer-than-you-think`, `great-work-great-career`, `grid-notebook`, `home-tonight`, `how-to-make-anyone-fall-in-love-with-you`, `how-to-win-in-a-winner-take-all-world`, `humble-inquiry`, `if-you-want-to-walk-on-water-you-have-got-to-get-out-of-the`, `intuition-pumps`, `keep-your-love-on`, `life-is-a-long-term-accumulation`, `live-your-best-life`, `living-loving-and-learning`, `mckinsey-writing-technique`, `myself-and-other-more-important-matters`, `orthodoxy`, `own-your-tech-career`, `power-of-the-other`, `qbq-question-behind-question`, `reinforcements-how-to-get-people-to-help-you`, `rich-dads-conspiracy-of-the-rich`, `so-good-they-cant-ignore-you`, `speed-of-trust`, `steal-like-an-artist`, `thats-not-what-i-meant`, `theory-and-practice-of-counseling-and-psychotherapy`, `what-every-body-is-saying`, `when-breath-becomes-air`, `winning`, `world-only-readers-can-reach`

---

## 🗑️ 已刪 repo 紀錄（2026-08-18，remote `nplus-father` ＋ local 一併刪，不重做）

| 書名 | slug | 總字元 | 每頁均 | 刪除原因 |
|---|---|---:|---:|---|
| 沒了名片，你還剩下什麼 | `whats-left-without-your-business-card` | 3,284 | 657 | 內容重複、已被其他書涵蓋 |
| 哈佛教你高EQ管理術 | `hbrs-guide-to-emotional-intelligence` | 13,706 | 1,054 | 內容重複——同一本書已有另一個更厚的 repo |

> 原分類 `professional/career/skill-building/`。張國洋、姚詩豪（大人學）的職場實戰指南——
> 自我定位、職場規則、累積籌碼、正向心態、轉職勝率五章。刪除理由不是找不到來源
> （[Readmoo](https://readmoo.com/book/210068763000101) 有電子書），是**同樣的題材已被庫裡更厚的書蓋過**：
> 同作者的 `art-of-adult-decision-making`（大人學選擇，9,671 字）講決策框架、
> `so-good-they-cant-ignore-you`（30,003 字）與 `managing-oneself`（12,822 字）講職涯資本與自我定位。
> 重做這本只會產出第四份同義筆記，因此連同 remote 一併刪除，不留備份。

> 原分類 `professional/leadership/team-building/`。這個 repo 與 `hbr-s-10-must-reads-on-emotional-intelligence`
> （19,835 字，2026-04 建）是**同一本書**——兩邊 `_index.md` 書名都是《哈佛教你高EQ管理術》
> （HBR's 10 Must Reads on Emotional Intelligence 繁中版），12 章逐一相同：成為全方位領導人／好情緒領導力／
> 公平為什麼這麼難／萬一好領袖作出壞決斷／推升高EQ團隊／有禮才有利／打造復原力／作自我情緒的領導人／
> 誰怕回饋意見／力阻少年得志＋兩篇導讀。刪除理由不是找不到來源，是重複建檔；留下的是較厚、較新的那份。
> repo 名與 GitHub description 標成 "HBR's Guide to Emotional Intelligence" 是誤標——真正的
> HBR Guide to Emotional Intelligence（2017 工作手冊，8 部）是 `hbr-guide-to-emotional-intelligence`（64,241 字），內容不同、保留。
> 刪除前已 `git bundle` 全歷史留底於 `books-management/archive/_deleted/hbrs-guide-to-emotional-intelligence.bundle`。

## 🗑️ 已刪 repo 紀錄（2026-08-17，remote `nplus-father` ＋ local 一併刪，不重做）

| 書名 | slug | 總字元 | 每頁均 | 刪除原因 |
|---|---|---:|---:|---|
| Kiss Your BUT Good-Bye | `kiss-your-but-good-bye` | 6,521 | 116 | 找不到資料、無法重做 |

> 原分類 `professional/career/skill-building/`。內容並非空殼——首頁有完整「改善行動十步驟」，
> 內文為 47 個職場隱形缺點條目（分 admit／discover／fight／observe／persist 五章，每條 130–500 字元）。
> 每頁均 116 偏低是**書本形式本來就是短條目**所致，不是骨架化。刪除前已備份內文與封面。

## 🗑️ 已刪 repo 紀錄（2026-07-11，remote `nplus-father` ＋ local 一併刪，不重做）

> 保留歷史紀錄。共 39 本（含 23 本第一批 ＋ 12 本華人中文書偏淺 ＋ 4 本後續）。詳見 git 歷史 `e0f326e` 之前版本。

> [!NOTE]
>
> 前版判定**有保留價值、不刪**的中文原著：秦嗣林《29張當票》系列＋《學上當》、彭明輝《活出生命最好的可能》《生命是長期而持續的累積》、大人學系列、王永福《上台的技術》、林明樟《五大關鍵數字力》、《與家人的財務界線》、《蒙格之道》、周家瑜《平等》、龔立人《是與非以外》、胡維華《胡爾摩斯品聖經懸疑》、《凝視生命（十誡神學）》、《和艦長玩轉 GitLab》。
