# Bookstown 摘要重做候選清單

> 重新盤點 2026-07-12・掃描 `books-done` 下 **1,360** 個 repo（已排除 build 產物、template、tmp）。
> 判準：**總字元**（整本 MD body 量）＋**每頁均**（總字元÷章節頁數）。全庫中位數：總字元 55,327、每頁均 2,987；p10：總字元 18,215、每頁均 1,101。
> 本輪 vs 2026-07-05：**33 本通過門檻移出**、**8 本新入榜**（🆕）、**6 本重做骨架空殼**（🚧 進行中）、**4 本開工後仍偏薄**（⏳ 重做未落地）。目前待處理 **79 本**。

## 圖例

- **📄** = 原始 PDF 已在 repo 根目錄，可直接開 IntelliJ 用 `/book-rewrite-content` 重做。
- **⏳** = 上一輪已標「開 IntelliJ 重做」，但至今仍偏薄（重做未落地，需回頭補完）。
- **🆕** = 本輪新入榜。
- 找書連結：英文原著→OceanofPDF；非英文（欄尾 ·日／·中／·韓／·德）→Readmoo。`Readmoo 無`＝查無電子書 → 見下方刪除／換源候選。

---

## 🚧 進行中｜重做骨架已建、正文未寫（勿重複開工）

> 這 6 本章節骨架（含新細分頁）已鋪好、body 仍空且未 commit。**是上一輪重做的在途工作，補完正文即可**，別當新案重開。

| 書名 | Repo 路徑 | 未追蹤變更 | 找書連結 |
|---|---|---:|---|
| 生命是長期而持續的累積 | `personal/relationships/community/life-is-a-long-term-accumulation` | 9 項 | [Readmoo](https://readmoo.com/book/210199970000101) ·中 |
| 與家人的財務界線 | `personal/relationships/parenting/financial-boundaries-with-family` | 28 項 | [Readmoo](https://readmoo.com/book/210133475000101) ·中 |
| 讓好工作找上你 | `professional/career/skill-building/great-work-great-career` | 20 項 | [OceanofPDF](https://oceanofpdf.com/?s=great+work+great+career) ｜ [Readmoo](https://readmoo.com/book/210025915000101) ·英 |
| 好好拜託 | `professional/communication/persuasion/reinforcements-how-to-get-people-to-help-you` | 26 項 | [OceanofPDF](https://oceanofpdf.com/?s=reinforcements+how+to+get+people+to+help+you) ｜ [Readmoo](https://readmoo.com/book/210105308000101) ·英 |
| 富者的態度 | `professional/finance/investing/attitude-of-the-rich` | 8 項 | [Readmoo](https://readmoo.com/book/210274862000101) ·韓 |
| 活出生命最好的可能 | `wisdom/philosophy/ethics/live-your-best-life` | 20 項 | [Readmoo](https://readmoo.com/book/210001283000101) ·中 |

## 待處理清單

### 第一優先｜近乎空殼（每頁均 < 250）

| 書名 | Repo 路徑 | 總字元 | 每頁均 | 找書連結 |
|---|---|---:|---:|---|
| Kiss Your BUT Good-Bye | `professional/career/skill-building/kiss-your-but-good-bye` | 6,521 | 116 | [kiss+your+but+good-bye](https://oceanofpdf.com/?s=kiss+your+but+good-bye) |
| 說話的品格 | `professional/communication/persuasion/dignity-of-speaking` | 6,648 | 229 | [Readmoo](https://readmoo.com/book/210330713000101) ·韓 |

### 第二優先｜內容稀薄（總字元 < 8,000）

| 書名 | Repo 路徑 | 總字元 | 每頁均 | 找書連結 |
|---|---|---:|---:|---|
| 29張當票3：門簾外的人生鑑定 | `personal/mindset/growth/29-pawn-tickets-3` | 1,250 | 417 | [Readmoo](https://readmoo.com/book/210019712000101) ·中 |
| 只有讀書能抵達的境界 | `personal/habit/productivity/world-only-readers-can-reach` | 2,436 | 304 | [Readmoo](https://readmoo.com/book/210148906000101) ·日 |
| 沒了名片，你還剩下什麼 | `professional/career/skill-building/whats-left-without-your-business-card` | 3,284 | 657 | [Readmoo](https://readmoo.com/book/210068763000101) ·日 |
| 程式設計師之禪 | `craft/tools/cli/zen-programmer` | 3,470 | 434 | [OceanofPDF](https://oceanofpdf.com/?s=the+zen+programmer) ｜ [Readmoo](https://readmoo.com/book/210119373000101) ·英 |
| 麥肯錫教我的思考武器：從邏輯思考到真正解決問題 | `craft/tools/cli/issue-driven` | 3,680 | 613 | [Readmoo](https://readmoo.com/book/210201484000101) ·日 |
| 學上當 | `personal/habit/productivity/learning-to-be-deceived` | 4,212 | 702 | [Readmoo](https://readmoo.com/book/210072176000101) ·中 |
| 📄 The Friendly Snowflake | `faith/spiritual-formation/devotional/friendly-snowflake` | 4,356 | 871 | [the+friendly+snowflake](https://oceanofpdf.com/?s=the+friendly+snowflake) |
| 幽谷之旅 | `faith/theology/apologetics/shadowlands` | 4,567 | 351 | [shadowlands](https://oceanofpdf.com/?s=shadowlands) (?) |
| 向下的移動 | `faith/theology/systematic/selfless-way-of-christ` | 4,822 | 964 | [the+selfless+way+of+christ](https://oceanofpdf.com/?s=the+selfless+way+of+christ) |
| 沒人敢告訴你的MBA大揭密 | `professional/communication/persuasion/mba-confidential` | 5,076 | 423 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E6%B2%92%E4%BA%BA%E6%95%A2%E5%91%8A%E8%A8%B4%E4%BD%A0%E7%9A%84MBA%E5%A4%A7%E6%8F%AD%E5%AF%86) ·原 |
| 約櫃流浪記 | `faith/theology/systematic/ichabod-toward-home` | 5,238 | 748 | [ichabod+toward+home](https://oceanofpdf.com/?s=ichabod+toward+home) |
| 教出孩子的生存力 | `wisdom/education/self-learning/raising-children-with-survival-skills` | 5,238 | 748 | [Readmoo](https://readmoo.com/book/210118288000101) ·日 |
| 跟任何人都可以聊得來 | `professional/communication/persuasion/how-to-talk-to-anyone` | 5,702 | 634 | [OceanofPDF](https://oceanofpdf.com/?s=how+to+talk+to+anyone) ｜ [Readmoo](https://share.readmoo.com/book/118779) ·英 |
| 哈佛教你打造健康人生 | `personal/wellness/mental-health/harvard-guide-to-a-healthy-life` | 6,203 | 517 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%93%88%E4%BD%9B%E6%95%99%E4%BD%A0%E6%89%93%E9%80%A0%E5%81%A5%E5%BA%B7%E4%BA%BA%E7%94%9F) ·H |
| 📄 富爸爸-有錢人的大陰謀 | `professional/finance/economics/rich-dads-conspiracy-of-the-rich` | 6,336 | 396 | [OceanofPDF](https://oceanofpdf.com/?s=rich+dad+conspiracy+of+the+rich) ｜ [Readmoo](https://readmoo.com/book/210086132000101) ·英 |
| When God Interrupts | `faith/theology/systematic/when-god-interrupts` | 6,488 | 721 | [when+god+interrupts](https://oceanofpdf.com/?s=when+god+interrupts) |
| 大象與跳蚤 | `wisdom/history/civilization/elephant-and-the-flea` | 6,752 | 482 | [OceanofPDF](https://oceanofpdf.com/?s=the+elephant+and+the+flea) ｜ [Readmoo](https://readmoo.com/book/210148867000101) ·英 |
| 你的不安，是因為太習慣受傷害 | `personal/relationships/community/your-anxiety-comes-from-being-too-used-to-getting-hurt` | 6,777 | 968 | [Readmoo](https://readmoo.com/book/210469737000101) ·韓 |
| 讀書這個荒野 | `wisdom/philosophy/ethics/reading-as-a-wilderness` | 7,277 | 910 | [Readmoo](https://readmoo.com/book/210114739000101) ·日 |
| 29張當票2：當舖裡特有的人生風景 | `personal/mindset/growth/29-pawn-tickets-2` | 7,468 | 2,489 | [Readmoo](https://readmoo.com/book/210003324000101) ·中 |

### 觀察名單｜邊緣偏薄（總字元 8,000–15,000）

| 書名 | Repo 路徑 | 總字元 | 每頁均 | 找書連結 |
|---|---|---:|---:|---|
| 未來最需要的新人才 | `professional/career/skill-building/new-talent-for-the-future` | 8,004 | 1,000 | [Readmoo](https://readmoo.com/book/210118641000101) ·日 |
| 比翼雙飛 | `faith/theology/systematic/flying-together-a-christian-marriage-guide` | 8,016 | 668 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E6%AF%94%E7%BF%BC%E9%9B%99%E9%A3%9B) ·中 |
| 煩惱都是自己想出來的 | `personal/habit/productivity/worries-are-all-in-your-head` | 8,031 | 892 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E7%85%A9%E6%83%B1%E9%83%BD%E6%98%AF%E8%87%AA%E5%B7%B1%E6%83%B3%E5%87%BA%E4%BE%86%E7%9A%84) ·日 |
| Culture Making | `faith/theology/systematic/culture-making` | 8,074 | 336 | [culture+making](https://oceanofpdf.com/?s=culture+making) |
| Rich Kids: How to Raise Our Children to Be Happy and Successful in Life | `personal/habit/productivity/rich-kids` | 8,185 | 1,169 | [rich+kids+thomas+corley](https://oceanofpdf.com/?s=rich+kids+thomas+corley) |
| 寫給每個人的社會學讀本 | `wisdom/education/self-learning/sociology-for-everyone` | 8,516 | 608 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%AF%AB%E7%B5%A6%E6%AF%8F%E5%80%8B%E4%BA%BA%E7%9A%84%E7%A4%BE%E6%9C%83%E5%AD%B8%E8%AE%80%E6%9C%AC) ·日 |
| McKinsey Zero-Second Logical Thinking | `craft/tools/cli/mckinsey-zero-second-logical-thinking` | 8,673 | 1,735 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=McKinsey%20Zero-Second%20Logical%20Thinking) ·日 |
| 就業的終結 | `professional/career/skill-building/end-of-jobs` | 8,995 | 500 | [the+end+of+jobs](https://oceanofpdf.com/?s=the+end+of+jobs) |
| 社會的趨勢 | `wisdom/history/civilization/new-realities` | 9,343 | 667 | [the+new+realities+drucker](https://oceanofpdf.com/?s=the+new+realities+drucker) |
| 読書力 | `wisdom/education/self-learning/power-of-reading` | 9,352 | 1,870 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E8%AA%AD%E6%9B%B8%E5%8A%9B) ·日 |
| 凝視生命--奇士勞斯基《十誡》的神學美學 | `wisdom/philosophy/ethics/gazing-at-life-theological-aesthetics-of-the-decalogue` | 9,475 | 592 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%87%9D%E8%A6%96%E7%94%9F%E5%91%BD) ·中 |
| 大人學選擇 | `personal/mindset/growth/art-of-adult-decision-making` | 9,671 | 1,382 | [Readmoo](https://readmoo.com/book/210254770000101) ·中 |
| 是與非以外：基督教的倫理想像 | `faith/theology/systematic/toward-a-christian-moral-imagination` | 9,792 | 490 | [Readmoo](https://readmoo.com/book/210190337000101) ·中 |
| 上台的技術 | `professional/communication/public-speaking/stage-presentation-skills` | 9,967 | 1,107 | [Readmoo](https://readmoo.com/book/240032622000101) ·中 |
| Where Do Top Performers Draw the Line When Reading | `craft/tools/cli/where-do-top-performers-draw-the-line-when-reading` | 10,130 | 1,013 | [Readmoo](https://readmoo.com/book/210185855000101) ·日 |
| 胡爾摩斯品聖經懸疑 | `faith/theology/biblical-studies/bible-suspense-investigation` | 10,337 | 795 | [Readmoo](https://readmoo.com/book/210304705000101) ·中 |
| McKinsey Note-Taking Method | `craft/tools/cli/mckinsey-note-taking-method` | 10,354 | 1,726 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=McKinsey%20Note-Taking%20Method) ·日 |
| 📄⏳ 愛勝過恐懼 | `faith/theology/systematic/lifesigns` | 10,440 | 746 | 📄 已在 repo｜[lifesigns nouwen](https://oceanofpdf.com/?s=lifesigns+nouwen) |
| World-Class Work Methods Compiled | `professional/career/skill-building/world-class-work-methods-compiled` | 10,448 | 950 | [Readmoo](https://readmoo.com/book/210333180000101) ·日 |
| 📄⏳ The Freedom of Self-Forgetfulness | `faith/theology/systematic/freedom-of-self-forgetfulness` | 10,881 | 2,176 | 📄 已在 repo｜[freedom of self forgetfulness](https://oceanofpdf.com/?s=freedom+of+self+forgetfulness) |
| 心靈療癒自助手冊 | `personal/mindset/emotion/your-mind-an-owners-manual-for-a-better-life` | 11,099 | 1,009 | [your+mind+an+owners+manual](https://oceanofpdf.com/?s=your+mind+an+owners+manual) |
| 📄 Five Key Numbers to Pick Profitable Stocks | `professional/finance/investing/five-key-numbers` | 11,198 | 1,244 | [Readmoo](https://readmoo.com/book/210067909000101) ·原 |
| 📄 順服的主 | `faith/theology/systematic/obedient-master` | 11,222 | 3,741 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E9%A0%86%E6%9C%8D%E7%9A%84%E4%B8%BB) ·中 |
| 在咖啡廳遇見彼得．杜拉克 | `craft/tools/cli/peter-drucker-cafe` | 11,328 | 1,618 | Readmoo 無｜[搜尋](https://readmoo.com/search/keyword?q=%E5%9C%A8%E5%92%96%E5%95%A1%E5%BB%B3%E9%81%87%E8%A6%8B%E5%BD%BC%E5%BE%97%EF%BC%8E%E6%9D%9C%E6%8B%89%E5%85%8B) ·日 |
| 父母會傷人 | `personal/relationships/parenting/toxic-parents` | 11,389 | 599 | [toxic+parents](https://oceanofpdf.com/?s=toxic+parents) |
| 30 Days of GitLab with the Captain | `craft/engineering/devops/30-days-of-gitlab` | 11,604 | 1,934 | [Readmoo](https://readmoo.com/book/210262575000101) ·中 |
| If You're Not First, You're Last | `professional/business/sales/if-youre-not-first-youre-last` | 11,729 | 1,303 | [if+youre+not+first+youre+last](https://oceanofpdf.com/?s=if+youre+not+first+youre+last) |
| Preaching and Preachers | `faith/theology/pastoral/preaching-and-preachers` | 12,130 | 714 | [preaching+and+preachers](https://oceanofpdf.com/?s=preaching+and+preachers) |
| 喧囂中的寧靜 | `faith/theology/systematic/way-of-the-heart` | 12,188 | 2,438 | [the+way+of+the+heart+nouwen](https://oceanofpdf.com/?s=the+way+of+the+heart+nouwen) |
| 康乃爾最經典的思考邏輯課 | `personal/mindset/growth/how-we-know-what-isnt-so` | 12,406 | 827 | [how+we+know+what+isnt+so](https://oceanofpdf.com/?s=how+we+know+what+isnt+so) |
| 親密關係 - 通往靈魂之橋 | `personal/relationships/dating/relationship-bridge-to-the-soul` | 12,410 | 1,773 | [relationship+bridge+to+the+soul](https://oceanofpdf.com/?s=relationship+bridge+to+the+soul) (?) |
| 選民進化論 | `professional/leadership/vision/wont-get-fooled-again` | 12,573 | 967 | [wont+get+fooled+again](https://oceanofpdf.com/?s=wont+get+fooled+again) (?) |
| Just Shut Up and Do It | `craft/tools/cli/just-shut-up-and-do-it` | 12,581 | 1,573 | [just+shut+up+and+do+it](https://oceanofpdf.com/?s=just+shut+up+and+do+it) |
| 📄 Managing Oneself | `professional/career/skill-building/managing-oneself` | 12,718 | 1,590 | [managing+oneself](https://oceanofpdf.com/?s=managing+oneself) |
| 成功人士一定會做的9件事情 | `personal/mindset/growth/nine-things-successful-people-do-differently` | 12,720 | 1,272 | [nine+things+successful+people+do+differently](https://oceanofpdf.com/?s=nine+things+successful+people+do+differently) |
| 這一生，你想留下什麼 | `professional/communication/storytelling/leading-matters` | 12,808 | 1,067 | [leading+matters](https://oceanofpdf.com/?s=leading+matters) |
| 📄 人之廢 | `wisdom/philosophy/ethics/abolition-of-man` | 12,891 | 3,223 | [the+abolition+of+man](https://oceanofpdf.com/?s=the+abolition+of+man) |
| HBR 10 Must Reads on Managing Yourself | `craft/tools/cli/hbr-10-must-reads-on-managing-yourself` | 12,969 | 1,081 | [hbr+10+must+reads+on+managing+yourself](https://oceanofpdf.com/?s=hbr+10+must+reads+on+managing+yourself) |
| 平等 | `wisdom/philosophy/political-philosophy/equality` | 13,044 | 1,449 | [Readmoo](https://readmoo.com/book/210123416000101) ·原 |
| 📄⏳ 致勝的答案 | `professional/communication/persuasion/winning` | 13,086 | 770 | 📄 已在 repo｜[winning jack welch](https://oceanofpdf.com/?s=winning+jack+welch) |
| 📄⏳ The Art of Loving | `wisdom/philosophy/ethics/art-of-loving` | 13,098 | 3,274 | 📄 已在 repo｜[the art of loving fromm](https://oceanofpdf.com/?s=the+art+of+loving+fromm) |
| 異類僑居者：基督徒的倫理與政治 | `faith/theology/systematic/resident-aliens-life-in-the-christian-colony` | 13,371 | 836 | [resident+aliens](https://oceanofpdf.com/?s=resident+aliens) |
| 世界在等待的門徒 | `faith/theology/systematic/radical-disciple` | 13,561 | 1,507 | [the+radical+disciple](https://oceanofpdf.com/?s=the+radical+disciple) |
| Work Optional | `professional/career/skill-building/work-optional` | 13,588 | 647 | [work+optional](https://oceanofpdf.com/?s=work+optional) |
| 哈佛教你高EQ管理術 | `professional/leadership/team-building/hbrs-guide-to-emotional-intelligence` | 13,830 | 1,064 | [hbr+guide+to+emotional+intelligence](https://oceanofpdf.com/?s=hbr+guide+to+emotional+intelligence) |
| 工作與生活的技術 🆕 | `craft/tools/cli/art-of-work-and-life` | 14,132 | 456 | [Readmoo](https://readmoo.com/search/keyword?q=%E5%B7%A5%E4%BD%9C%E8%88%87%E7%94%9F%E6%B4%BB%E7%9A%84%E6%8A%80%E8%A1%93) ·中 |
| 有錢人想的和你不一樣 🆕 | `personal/mindset/growth/secrets-of-the-millionaire-mind` | 14,139 | 673 | [OceanofPDF](https://oceanofpdf.com/?s=secrets+of+the+millionaire+mind) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E6%9C%89%E9%8C%A2%E4%BA%BA%E6%83%B3%E7%9A%84%E5%92%8C%E4%BD%A0%E4%B8%8D%E4%B8%80%E6%A8%A3) ·英 |
| HBR Guide to Finance Basics for Managers | `professional/business/management/hbr-guide-to-finance-basics-for-managers` | 14,153 | 4,718 | [hbr+guide+to+finance+basics+for+managers](https://oceanofpdf.com/?s=hbr+guide+to+finance+basics+for+managers) |
| 📄 The Prodigal Prophet: Jonah and the Mystery of God's Mercy | `faith/theology/systematic/prodigal-prophet` | 14,214 | 1,093 | [the+prodigal+prophet](https://oceanofpdf.com/?s=the+prodigal+prophet) |
| 📄 記憶的治療者 | `faith/theology/pastoral/living-reminder` | 14,226 | 2,845 | [the+living+reminder](https://oceanofpdf.com/?s=the+living+reminder) |
| 📄 蒙格之道：關於投資、閱讀、工作與幸福的普通常識 | `wisdom/philosophy/ethics/way-of-munger` | 14,365 | 1,596 | [Readmoo](https://readmoo.com/book/210287274000101) ·中 |
| 探索人格潛能，看見更真實的自己 (Me, Myself, and Us) 🆕 | `personal/mindset/growth/me-myself-and-us` | 14,534 | 1,211 | [OceanofPDF](https://oceanofpdf.com/?s=me+myself+and+us+brian+little) ·英 |
| 你要如何衡量你的人生 🆕 | `personal/mindset/resilience/how-will-you-measure-your-life` | 14,630 | 975 | [OceanofPDF](https://oceanofpdf.com/?s=how+will+you+measure+your+life) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E4%BD%A0%E8%A6%81%E5%A6%82%E4%BD%95%E8%A1%A1%E9%87%8F%E4%BD%A0%E7%9A%84%E4%BA%BA%E7%94%9F) ·英 |
| 你的身體，正在洩漏你的秘密 (Ohne Worte) 🆕 | `professional/communication/persuasion/ohne-worte` | 14,694 | 816 | [OceanofPDF](https://oceanofpdf.com/?s=ohne+worte+koerpersprache)（德文原著）｜ [Readmoo](https://readmoo.com/search/keyword?q=%E4%BD%A0%E7%9A%84%E8%BA%AB%E9%AB%94%E6%AD%A3%E5%9C%A8%E6%B4%A9%E6%BC%8F%E4%BD%A0%E7%9A%84%E7%A7%98%E5%AF%86) ·德 |
| 📄 深度學習力 (How to Be a High School Superstar) 🆕 | `wisdom/education/self-learning/how-to-be-a-high-school-superstar` | 14,786 | 548 | [OceanofPDF](https://oceanofpdf.com/?s=how+to+be+a+high+school+superstar) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E6%B7%B1%E5%BA%A6%E5%AD%B8%E7%BF%92%E5%8A%9B) ·英 |
| 擁抱B選項 🆕 | `personal/mindset/resilience/option-b` | 14,937 | 1,245 | [OceanofPDF](https://oceanofpdf.com/?s=option+b+sandberg) ｜ [Readmoo](https://readmoo.com/search/keyword?q=%E6%93%81%E6%8A%B1B%E9%81%B8%E9%A0%85) ·英 |
| SCRUM：用一半的時間做兩倍的事 🆕 | `craft/tools/cli/scrum-the-art-of-doing-twice-the-work-in-half-the-time` | 14,951 | 1,661 | [OceanofPDF](https://oceanofpdf.com/?s=scrum+the+art+of+doing+twice+the+work) ｜ [Readmoo](https://readmoo.com/search/keyword?q=SCRUM%20%E7%94%A8%E4%B8%80%E5%8D%8A%E7%9A%84%E6%99%82%E9%96%93%E5%81%9A%E5%85%A9%E5%80%8D%E7%9A%84%E4%BA%8B) ·英 |

---

## 🗑️ 刪除／換源候選（Readmoo 無電子書、來源難取得）

> 查無電子書、擴充只能另尋來源。日系 McKinsey／速讀類與前一輪已刪的一批同型，**營養價值低、建議刪**；標註「保留」者為前版 NOTE 指定的中文價值書，留待換源。

| 書名 | slug | 總字元 | 語言 | 處置建議 |
|---|---|---:|:--:|---|
| 沒人敢告訴你的MBA大揭密 | `mba-confidential` | 5,076 | 原 | 🗑️ 刪除候選 |
| 哈佛教你打造健康人生 | `harvard-guide-to-a-healthy-life` | 6,203 | H | 🗑️ 刪除候選 |
| 比翼雙飛 | `flying-together-a-christian-marriage-guide` | 8,016 | 中 | 🗑️ 刪除候選 |
| 煩惱都是自己想出來的 | `worries-are-all-in-your-head` | 8,031 | 日 | 🗑️ 刪除候選 |
| 寫給每個人的社會學讀本 | `sociology-for-everyone` | 8,516 | 日 | 🗑️ 刪除候選 |
| McKinsey Zero-Second Logical Thinking | `mckinsey-zero-second-logical-thinking` | 8,673 | 日 | 🗑️ 刪除候選 |
| 読書力 | `power-of-reading` | 9,352 | 日 | 🗑️ 刪除候選 |
| 凝視生命--奇士勞斯基《十誡》的神學美學 | `gazing-at-life-theological-aesthetics-of-the-decalogue` | 9,475 | 中 | ⚠️ 保留（NOTE 指定） |
| McKinsey Note-Taking Method | `mckinsey-note-taking-method` | 10,354 | 日 | 🗑️ 刪除候選 |
| 順服的主 | `obedient-master` | 11,222 | 中 | ⚠️ 保留（NOTE 指定） |
| 在咖啡廳遇見彼得．杜拉克 | `peter-drucker-cafe` | 11,328 | 日 | 🗑️ 刪除候選 |

## ✅ 本輪已通過門檻、移出清單（33 本）

> 已重做/擴充到 total > 15,000，下輪掃描直接跳過。

`qbq-question-behind-question`, `steal-like-an-artist`, `adversity-quotient`, `deep-work`, `when-breath-becomes-air`, `aging`, `charisma-myth`, `mckinsey-writing-technique`, `so-good-they-cant-ignore-you`, `theory-and-practice-of-counseling-and-psychotherapy`, `29-pawn-tickets`, `grid-notebook`, `humble-inquiry`, `end-of-average`, `do-over`, `how-to-make-anyone-fall-in-love-with-you`, `course-of-love`, `own-your-tech-career`, `keep-your-love-on`, `bonhoeffer-spiritual-care-in-a-religionless-age`, `what-every-body-is-saying`, `orthodoxy`, `how-to-win-in-a-winner-take-all-world`, `god-is-closer-than-you-think`, `eat-this-book`, `thats-not-what-i-meant`, `power-of-the-other`, `speed-of-trust`, `home-tonight`, `if-you-want-to-walk-on-water-you-have-got-to-get-out-of-the`, `living-loving-and-learning`, `myself-and-other-more-important-matters`, `intuition-pumps`

---

## 🗑️ 已刪 repo 紀錄（2026-07-11，remote `nplus-father` ＋ local 一併刪，不重做）

> 保留歷史紀錄。共 39 本（含 23 本第一批 ＋ 12 本華人中文書偏淺 ＋ 4 本後續）。詳見 git 歷史 `e0f326e` 之前版本。

> [!NOTE]
> 前版判定**有保留價值、不刪**的中文原著：秦嗣林《29張當票》系列＋《學上當》、彭明輝《活出生命最好的可能》《生命是長期而持續的累積》、大人學系列、王永福《上台的技術》、林明樟《五大關鍵數字力》、《與家人的財務界線》、《蒙格之道》、周家瑜《平等》、龔立人《是與非以外》、胡維華《胡爾摩斯品聖經懸疑》、《凝視生命（十誡神學）》、《和艦長玩轉 GitLab》。
