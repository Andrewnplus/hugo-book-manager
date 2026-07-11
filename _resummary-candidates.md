# Bookstown 摘要重做候選清單

> 產生日期：2026-07-05 ・ 掃描：`~/workspace/books` 下 1,404 個書本 repo（排除 4 個含 PDF 者後為 1,400）

## 我們要找的目標

**沒有用 PDF 元素、以舊摘要產生內容的 repo**——這些內容偏薄，需重新找完整來源重做摘要。

### 判斷方法

- **PDF 排除**：repo 內若含 PDF，視為已用完整文件整理，直接跳過（本次共排除 4 個）。
- **內容密度**：其餘 repo 量兩個指標——**總字元**（整本 Markdown 內容量）與**每頁均**（總字元÷頁數，反映每章密度）。

> 📌 觀察：**內容豐富的 repo 幾乎都沒有把來源 PDF 留在 repo 裡**（前 8 大內容 repo 全部無 PDF），全庫僅 4 個 repo 有 PDF。
> 因此「有沒有 PDF」無法當主要判準，實務上以**內容密度**為主，PDF 僅用於剔除少數例外。

全庫分布供參：總字元中位數 **53,631**（p10=13,235）；每頁均中位數 **2,835**（p10=960）。

---

## 第一優先｜近乎空殼（每頁均 < 250，形同只有標題骨架）

幾乎沒有正文，多半只有章節標題與 front matter，**務必重做**。

| 書名 | Repo 路徑 | 總字元 | 頁數 | 每頁均 |
|---|---|---:|---:|---:|
| 諮商與心理治療：理論與實務 | `wisdom/philosophy/theory-and-practice-of-counseling-and-psychotherapy` | 147 | 4 | 36 |
| 不說謊，我們活不下去 | `wisdom/philosophy/born-liars` | 1634 | 8 | 204 |
| 世界菁英為什麼相信失敗，質疑成功？ | `professional/career/why-global-elites-believe-in-failure` | 2180 | 15 | 145 |
| 你拿什麼定義自己 | `professional/career/myself-and-other-more-important-matters` | 2730 | 13 | 210 |
| 直覺幫浦與其他思考工具 | `wisdom/philosophy/intuition-pumps` | 3293 | 90 | 36 |
| 你有你的計劃，世界另有計劃 | `personal/mindset/wan-weigang-your-plan-worlds-plan` | 3461 | 65 | 53 |
| God Works the Night Shift | `faith/theology/god-works-the-night-shift` | 3462 | 21 | 164 |
| 走過失業，我喜歡現在的人生 | `professional/finance/after-unemployment-i-love-my-life-now` | 6843 | 28 | 244 |
| 讓人變有錢的36個微習慣 | `personal/habit/36-micro-habits-to-become-rich` | 8221 | 42 | 195 |
| Kiss Your BUT Good-Bye | `professional/career/kiss-your-but-good-bye` | 8523 | 56 | 152 |

## 第二優先｜內容稀薄（總字元 < 8,000，舊摘要風格）

有正文但整本偏薄，符合「舊摘要」特徵，建議重做。

| 書名 | Repo 路徑 | 總字元 | 頁數 | 每頁均 |
|---|---|---:|---:|---:|
| 29張當票3：門簾外的人生鑑定 | `personal/mindset/29-pawn-tickets-3` | 1356 | 3 | 452 |
| 蔡康永的說話之道 | `personal/relationships/cai-kangyongs-way-of-speaking` | 1434 | 1 | 1434 |
| 請問呂律師：關於愛和婚姻的練習題 | `personal/relationships/ask-lawyer-lu` | 1626 | 5 | 325 |
| 蔡康永的說話之道2 | `personal/relationships/cai-kangyongs-way-of-speaking-2` | 2297 | 1 | 2297 |
| 魅力學 | `professional/communication/charisma-myth` | 2648 | 3 | 882 |
| 只有讀書能抵達的境界 | `personal/habit/world-only-readers-can-reach` | 2744 | 8 | 343 |
| 讓好工作找上你 | `professional/career/great-work-great-career` | 2752 | 10 | 275 |
| Steal Like an Artist | `craft/design/steal-like-an-artist` | 2811 | 10 | 281 |
| 複製成功腦 | `professional/career/copy-the-successful-brain` | 2902 | 4 | 725 |
| 工作最重要的投資 | `professional/career/do-over` | 3208 | 5 | 641 |
| 決斷思考就是你的武器 | `professional/communication/decisive-thinking-weapon` | 3220 | 8 | 402 |
| 讀書筆記模版 | `wisdom/education/grid-notebook` | 3281 | 6 | 546 |
| 深度職場力 | `professional/career/so-good-they-cant-ignore-you` | 3323 | 5 | 664 |
| 不讓自己成為箭靶 | `professional/business/do-not-make-yourself-a-target` | 3447 | 8 | 430 |
| 沒了名片，你還剩下什麼 | `professional/career/whats-left-without-your-business-card` | 3454 | 5 | 690 |
| 致富強心臟 | `professional/finance/wealthy-strong-heart` | 3505 | 6 | 584 |
| 程式設計師之禪 | `craft/tools/zen-programmer` | 3747 | 8 | 468 |
| 用「空服員說話法」輕鬆搞定各種人 | `professional/communication/flight-attendant-communication-skills` | 3790 | 3 | 1263 |
| 40歲一定要養成的關鍵習慣 | `professional/communication/key-habits-to-build-by-40` | 3825 | 6 | 637 |
| 麥肯錫教我的思考武器：從邏輯思考到真正解決問題 | `craft/tools/issue-driven` | 3921 | 6 | 653 |
| 人才，自造者 | `wisdom/education/self-made-talent` | 3931 | 4 | 982 |
| QBQ!問題背後的問題 | `personal/habit/qbq-question-behind-question` | 3952 | 6 | 658 |
| 投資判斷一點都不難 | `professional/finance/investment-judgment-made-easy` | 3986 | 5 | 797 |
| 愛情非童話：給妳的床邊故事 | `personal/relationships/love-is-not-fairytale` | 4096 | 3 | 1365 |
| 陪你飛一程 | `professional/career/flying-with-you-a-while` | 4100 | 8 | 512 |
| How Capable People Organize Their Desks | `craft/tools/how-capable-people-organize-their-desks` | 4135 | 6 | 689 |
| Investment Banking Excel Skills | `craft/tools/investment-bank-excel` | 4222 | 4 | 1055 |
| 致勝的答案 | `professional/communication/winning` | 4320 | 17 | 254 |
| 學上當 | `personal/habit/learning-to-be-deceived` | 4401 | 6 | 733 |
| 麥肯錫寫作技術與邏輯思考 | `professional/communication/mckinsey-writing-technique` | 4451 | 7 | 635 |
| 突破性思考 | `craft/design/harvard-business-review-on-breakthrough-thinking` | 4473 | 7 | 639 |
| 與歲月和好 | `wisdom/philosophy/aging` | 4473 | 8 | 559 |
| The Friendly Snowflake | `faith/spiritual-formation/friendly-snowflake` | 4553 | 5 | 910 |
| 我們為何工作 | `wisdom/philosophy/why-we-work` | 4642 | 6 | 773 |
| 好好拜託 | `professional/communication/reinforcements-how-to-get-people-to-help-you` | 4734 | 17 | 278 |
| 活出生命最好的可能 | `wisdom/philosophy/live-your-best-life` | 4779 | 4 | 1194 |
| 33 Things to Do Before 35 | `professional/career/33-things-to-do-before-35` | 4980 | 3 | 1660 |
| 幽谷之旅 | `faith/theology/shadowlands` | 4998 | 13 | 384 |
| 向下的移動 | `faith/theology/selfless-way-of-christ` | 5005 | 5 | 1001 |
| 少犯錯，一生都是投資贏家 | `professional/finance/winning-the-losers-game` | 5042 | 3 | 1680 |
| 直說無妨：非常關係2 | `personal/relationships/extraordinary-relationship-2` | 5043 | 4 | 1260 |
| Where Will You Be in the Next Decade | `professional/career/where-will-you-be-in-the-next-decade` | 5051 | 6 | 841 |
| 富者的態度 | `professional/finance/attitude-of-the-rich` | 5197 | 8 | 649 |
| 向史丹佛、麥肯錫菁英學做永不後悔的決定 | `personal/mindset/stanford-mckinsey-deliberate-decision-making` | 5230 | 9 | 581 |
| 謙遜提問 | `professional/communication/humble-inquiry` | 5277 | 8 | 659 |
| 他人的力量 | `professional/communication/power-of-the-other` | 5396 | 13 | 415 |
| 讓你在乎的人都喜歡你 | `professional/communication/conversation-the-first-four-minutes` | 5482 | 5 | 1096 |
| 教出孩子的生存力 | `wisdom/education/raising-children-with-survival-skills` | 5525 | 7 | 789 |
| 約櫃流浪記 | `faith/theology/ichabod-toward-home` | 5546 | 7 | 792 |
| 沒人敢告訴你的MBA大揭密 | `professional/communication/mba-confidential` | 5618 | 12 | 468 |
| 神啊！說好的那個人呢？ | `faith/theology/god-where-is-the-one-you-promised` | 5827 | 12 | 485 |
| 愛的進化論 | `personal/relationships/course-of-love` | 5866 | 5 | 1173 |
| FBI教你讀心術:看穿肢體動作的真實訊息 | `wisdom/science/what-every-body-is-saying` | 5936 | 10 | 593 |
| How World-Class Professionals Practice Fundamentals | `professional/career/how-world-class-professionals-practice-fundamentals` | 6074 | 8 | 759 |
| 跟任何人都可以聊得來 | `professional/communication/how-to-talk-to-anyone` | 6074 | 9 | 674 |
| Home Tonight | `personal/relationships/home-tonight` | 6168 | 14 | 440 |
| 29張當票：典當不到的人生啟發 | `wisdom/philosophy/29-pawn-tickets` | 6248 | 5 | 1249 |
| McKinsey and Stanford Thinking Notes | `craft/tools/mckinsey-and-stanford-thinking-notes` | 6381 | 7 | 911 |
| 有異象的人 | `faith/theology/people-with-vision` | 6463 | 6 | 1077 |
| 善用你的談話風格 | `professional/communication/thats-not-what-i-meant` | 6590 | 16 | 411 |
| 哈佛教你打造健康人生 | `personal/wellness/harvard-guide-to-a-healthy-life` | 6623 | 12 | 551 |
| 一路愛到底 | `personal/relationships/keep-your-love-on` | 6772 | 14 | 483 |
| When God Interrupts | `faith/theology/when-god-interrupts` | 6791 | 9 | 754 |
| 非常關係 | `personal/relationships/extraordinary-relationship` | 6826 | 4 | 1706 |
| 戀愛課：戀人的五十道習題 | `personal/relationships/lessons-in-love` | 6834 | 4 | 1708 |
| 富爸爸-有錢人的大陰謀 | `professional/finance/rich-dads-conspiracy-of-the-rich` | 6949 | 16 | 434 |
| 跟任何人都可以聊得來3 | `personal/relationships/how-to-make-anyone-fall-in-love-with-you` | 6949 | 7 | 992 |
| 生命是長期而持續的累積 | `personal/relationships/life-is-a-long-term-accumulation` | 6964 | 5 | 1392 |
| 你的不安，是因為太習慣受傷害 | `personal/relationships/your-anxiety-comes-from-being-too-used-to-getting-hurt` | 7071 | 7 | 1010 |
| 大象與跳蚤 | `wisdom/history/elephant-and-the-flea` | 7264 | 14 | 518 |
| 與家人的財務界線 | `personal/relationships/financial-boundaries-with-family` | 7352 | 20 | 367 |
| 如何在贏者全拿的職場中生存 | `professional/career/how-to-win-in-a-winner-take-all-world` | 7534 | 11 | 684 |
| 牧養是場冒險：靈性關顧12講 | `faith/theology/bonhoeffer-spiritual-care-in-a-religionless-age` | 7553 | 21 | 359 |
| 29張當票2：當舖裡特有的人生風景 | `personal/mindset/29-pawn-tickets-2` | 7574 | 3 | 2524 |
| 哈佛菁英課 | `professional/communication/harvard-elite-course` | 7592 | 20 | 379 |
| 讀書這個荒野 | `wisdom/philosophy/reading-as-a-wilderness` | 7610 | 8 | 951 |
| 精準撩動人心的戀愛人類學 | `personal/relationships/love-anthropology` | 7634 | 5 | 1526 |
| 砍掉重練 | `professional/career/reset-and-rebuild` | 7977 | 7 | 1139 |

## 觀察名單｜邊緣偏薄（總字元 8,000–15,000）

低於全庫 p10，可能是舊摘要，也可能本來就是短的小書，**建議人工快速確認**。

| 書名 | Repo 路徑 | 總字元 | 頁數 | 每頁均 |
|---|---|---:|---:|---:|
| 高效信任力 | `professional/communication/speed-of-trust` | 8058 | 30 | 268 |
| 說話的品格 | `professional/communication/dignity-of-speaking` | 8102 | 29 | 279 |
| 未來最需要的新人才 | `professional/career/new-talent-for-the-future` | 8351 | 8 | 1043 |
| Rich Kids: How to Raise Our Children to Be Happy and Successful in Life | `personal/habit/rich-kids` | 8406 | 7 | 1200 |
| 煩惱都是自己想出來的 | `personal/habit/worries-are-all-in-your-head` | 8410 | 9 | 934 |
| 比翼雙飛 | `faith/theology/flying-together-a-christian-marriage-guide` | 8443 | 12 | 703 |
| God Is Closer Than You Think | `faith/theology/god-is-closer-than-you-think` | 8800 | 10 | 880 |
| Culture Making | `faith/theology/culture-making` | 8864 | 24 | 369 |
| McKinsey Zero-Second Logical Thinking | `craft/tools/mckinsey-zero-second-logical-thinking` | 8893 | 5 | 1778 |
| 終結平庸 | `professional/career/end-of-average` | 8914 | 13 | 685 |
| 掌握你的科技職涯 | `professional/career/own-your-tech-career` | 8927 | 9 | 991 |
| 寫給每個人的社會學讀本 | `wisdom/education/sociology-for-everyone` | 9074 | 14 | 648 |
| When Breath Becomes Air | `wisdom/philosophy/when-breath-becomes-air` | 9418 | 4 | 2354 |
| 読書力 | `wisdom/education/power-of-reading` | 9510 | 5 | 1902 |
| 蒙格之道：關於投資、閱讀、工作與幸福的普通常識 | `wisdom/philosophy/way-of-munger` | 9681 | 9 | 1075 |
| 社會的趨勢 | `wisdom/history/new-realities` | 9808 | 14 | 700 |
| 就業的終結 | `professional/career/end-of-jobs` | 9870 | 18 | 548 |
| 大人學選擇 | `personal/mindset/art-of-adult-decision-making` | 9920 | 7 | 1417 |
| 凝視生命--奇士勞斯基《十誡》的神學美學 | `wisdom/philosophy/gazing-at-life-theological-aesthetics-of-the-decalogue` | 10153 | 16 | 634 |
| 好好說話 | `professional/communication/art-of-speaking-well` | 10246 | 8 | 1280 |
| 愛，生活與學習 | `wisdom/education/living-loving-and-learning` | 10252 | 15 | 683 |
| 上台的技術 | `professional/communication/stage-presentation-skills` | 10279 | 9 | 1142 |
| Where Do Top Performers Draw the Line When Reading | `craft/tools/where-do-top-performers-draw-the-line-when-reading` | 10525 | 10 | 1052 |
| The Freedom of Self-Forgetfulness | `faith/theology/freedom-of-self-forgetfulness` | 10564 | 5 | 2112 |
| 是與非以外：基督教的倫理想像 | `faith/theology/toward-a-christian-moral-imagination` | 10594 | 20 | 529 |
| McKinsey Note-Taking Method | `craft/tools/mckinsey-note-taking-method` | 10630 | 6 | 1771 |
| The Art of Loving | `wisdom/philosophy/art-of-loving` | 10817 | 4 | 2704 |
| 行在水面上 | `faith/theology/if-you-want-to-walk-on-water-you-have-got-to-get-out-of-the` | 10891 | 12 | 907 |
| World-Class Work Methods Compiled | `professional/career/world-class-work-methods-compiled` | 10900 | 11 | 990 |
| 愛勝過恐懼 | `faith/theology/lifesigns` | 10979 | 14 | 784 |
| 胡爾摩斯品聖經懸疑 | `faith/theology/bible-suspense-investigation` | 11019 | 13 | 847 |
| 聖經好好吃 | `faith/theology/eat-this-book` | 11169 | 12 | 930 |
| 順服的主 | `faith/theology/obedient-master` | 11316 | 3 | 3772 |
| 心靈療癒自助手冊 | `personal/mindset/your-mind-an-owners-manual-for-a-better-life` | 11515 | 11 | 1046 |
| Orthodoxy | `faith/theology/orthodoxy` | 11543 | 12 | 961 |
| Five Key Numbers to Pick Profitable Stocks | `professional/finance/five-key-numbers` | 11549 | 9 | 1283 |
| 在咖啡廳遇見彼得．杜拉克 | `craft/tools/peter-drucker-cafe` | 11588 | 7 | 1655 |
| AQ逆境商數 | `professional/leadership/adversity-quotient` | 11749 | 14 | 839 |
| Deep Work 深度工作力 | `craft/tools/deep-work` | 11773 | 12 | 981 |
| 30 Days of GitLab with the Captain | `craft/engineering/30-days-of-gitlab` | 11876 | 6 | 1979 |
| If You're Not First, You're Last | `professional/business/if-youre-not-first-youre-last` | 12033 | 9 | 1337 |
| 父母會傷人 | `personal/relationships/toxic-parents` | 12110 | 19 | 637 |
| 喧囂中的寧靜 | `faith/theology/way-of-the-heart` | 12340 | 5 | 2468 |
| 親密關係 - 通往靈魂之橋 | `personal/relationships/relationship-bridge-to-the-soul` | 12709 | 7 | 1815 |
| Just Shut Up and Do It | `craft/tools/just-shut-up-and-do-it` | 12968 | 8 | 1621 |
| 康乃爾最經典的思考邏輯課 | `personal/mindset/how-we-know-what-isnt-so` | 13015 | 15 | 867 |
| 人之廢 | `wisdom/philosophy/abolition-of-man` | 13021 | 4 | 3255 |
| 選民進化論 | `professional/leadership/wont-get-fooled-again` | 13087 | 13 | 1006 |
| Preaching and Preachers | `faith/theology/preaching-and-preachers` | 13103 | 17 | 770 |
| 這一生，你想留下什麼 | `professional/communication/leading-matters` | 13188 | 12 | 1099 |
| Managing Oneself | `professional/career/managing-oneself` | 13235 | 9 | 1470 |
| 成功人士一定會做的9件事情 | `personal/mindset/nine-things-successful-people-do-differently` | 13270 | 10 | 1327 |
| HBR 10 Must Reads on Managing Yourself | `craft/tools/hbr-10-must-reads-on-managing-yourself` | 13391 | 12 | 1115 |
| 平等 | `wisdom/philosophy/equality` | 13592 | 9 | 1510 |
| 世界在等待的門徒 | `faith/theology/radical-disciple` | 13926 | 9 | 1547 |
| 異類僑居者：基督徒的倫理與政治 | `faith/theology/resident-aliens-life-in-the-christian-colony` | 14064 | 16 | 879 |
| 哈佛教你高EQ管理術 | `professional/leadership/hbrs-guide-to-emotional-intelligence` | 14314 | 13 | 1101 |
| HBR Guide to Finance Basics for Managers | `professional/business/hbr-guide-to-finance-basics-for-managers` | 14355 | 3 | 4785 |
| Work Optional | `professional/career/work-optional` | 14367 | 21 | 684 |
| 記憶的治療者 | `faith/theology/living-reminder` | 14412 | 5 | 2882 |
| The Prodigal Prophet: Jonah and the Mystery of God's Mercy | `faith/theology/prodigal-prophet` | 14664 | 13 | 1128 |

---

## ⚠️ 異常：有原始 PDF 但內容仍偏薄

依規則已從上方排除，但實際上內容很薄——原始 PDF 就在 repo 內，**最容易重做**，建議優先處理：

| 書名 | Repo 路徑 | 總字元 | PDF |
|---|---|---:|---|
| Further Along the Road Less Traveled | `personal/mindset/further-along-the-road-less-traveled` | 1,920 | 有原始書 PDF |

## 統計摘要

| 分層 | repo 數 |
|---|---:|
| 近乎空殼（avg<250） | 10 |
| 內容稀薄（total<8000） | 78 |
| 邊緣偏薄（8000–15000） | 61 |
| **合計建議處理** | **149** |
| 含 PDF 已排除 | 4 |
| 全庫總數 | 1,404 |
