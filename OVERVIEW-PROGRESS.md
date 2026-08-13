# 深度概覽改寫進度

> 衍生檔，勿手改。重新產生：`python3 scripts/audit-overview.py --report`

最後更新：（未提供）

## 總覽

| | 數量 | 佔比 |
|---|---:|---:|
| 全庫 | 1637 | 100% |
| **已改寫並通過品檢** | **123** | **7.5%** |
| 仍是舊三段格式 | 1512 | 92.4% |

```
[███···············································] 7.5%
```

## 各項未通過

| 檢查 | 未通過 | 佔比 |
|---|---:|---:|
| 四段齊全 | 1512 | 92% |
| 作者的位置 長度 | 1512 | 92% |
| 定位 長度 | 1512 | 92% |
| 這本書的限制 長度 | 1512 | 92% |
| 限制段有實質內容 | 1512 | 92% |
| 引用年份 | 1349 | 82% |
| 點名可查證的作品／人名 | 1003 | 61% |
| 完整摘要 長度 | 502 | 31% |
| 粗體都收得起來 | 48 | 3% |
| 無空洞讚美 | 45 | 3% |

## 待改寫（依 slug 排序）

刻意不按筆記量或未通過項數排序——挑書的順序由人決定，這裡只負責把還沒做的列全。

| # | 書 | 未通過 | 概覽字數 | 筆記 |
|---:|---|---:|---:|---:|
| 1 | `craft/engineering/coding-practice/programmer-self-cultivation` | 6 | 663 | 145k |
| 2 | `craft/engineering/coding-practice/programming-pearls` | 7 | 812 | 50k |
| 3 | `craft/engineering/coding-practice/refactoring` | 8 | 409 | 86k |
| 4 | `craft/engineering/coding-practice/refactoring-for-software-design-smells` | 7 | 743 | 164k |
| 5 | `craft/engineering/coding-practice/refactoring-to-patterns` | 7 | 1238 | 496k |
| 6 | `craft/engineering/coding-practice/rules-of-programming` | 7 | 848 | 139k |
| 7 | `craft/engineering/coding-practice/running-on-empty` | 6 | 1290 | 77k |
| 8 | `craft/engineering/coding-practice/seriously-good-software` | 8 | 453 | 76k |
| 9 | `craft/engineering/coding-practice/smalltalk-best-practice-patterns` | 9 | 2137 | 288k |
| 10 | `craft/engineering/coding-practice/software-architect-12-disciplines` | 6 | 745 | 188k |
| 11 | `craft/engineering/coding-practice/software-architect-elevator` | 6 | 926 | 340k |
| 12 | `craft/engineering/coding-practice/software-engineering-at-google` | 7 | 427 | 632k |
| 13 | `craft/engineering/coding-practice/specification-by-example` | 6 | 786 | 177k |
| 14 | `craft/engineering/coding-practice/test-driven-development` | 8 | 403 | 180k |
| 15 | `craft/engineering/coding-practice/thinking-in-programming-paradigms-and-oop` | 8 | 375 | 42k |
| 16 | `craft/engineering/coding-practice/tidy-first` | 7 | 1497 | 109k |
| 17 | `craft/engineering/coding-practice/working-effectively-with-legacy-code` | 7 | 468 | 176k |
| 18 | `craft/engineering/coding-practice/wujun-beauty-of-math` | 8 | 516 | 119k |
| 19 | `craft/engineering/coding-practice/wujun-soul-of-computing` | 7 | 773 | 111k |
| 20 | `craft/engineering/coding-practice/zen-programmer` | 8 | 559 | 8k |
| 21 | `craft/engineering/databases/art-of-postgresql` | 6 | 977 | 433k |
| 22 | `craft/engineering/databases/cqrs-command-query-responsibility-segregation` | 7 | 922 | 94k |
| 23 | `craft/engineering/databases/data-warehouse-toolkit` | 7 | 934 | 679k |
| 24 | `craft/engineering/databases/database-internals` | 7 | 900 | 239k |
| 25 | `craft/engineering/databases/high-performance-mysql` | 7 | 523 | 348k |
| 26 | `craft/engineering/databases/nosql-distilled` | 7 | 1022 | 261k |
| 27 | `craft/engineering/databases/postgresql-14-internals` | 7 | 1308 | 600k |
| 28 | `craft/engineering/databases/sql-performance-explained` | 7 | 1370 | 228k |
| 29 | `craft/engineering/databases/sql-server-2025-query-performance-tuning` | 7 | 769 | 223k |
| 30 | `craft/engineering/databases/sql-server-2025-unveiled` | 5 | 1096 | 143k |
| 31 | `craft/engineering/devops/30-days-of-gitlab` | 8 | 490 | 20k |
| 32 | `craft/engineering/devops/accelerate` | 7 | 1095 | 235k |
| 33 | `craft/engineering/devops/devops-handbook` | 6 | 984 | 178k |
| 34 | `craft/engineering/devops/kubernetes-in-action` | 8 | 3971 | 683k |
| 35 | `craft/engineering/devops/kubernetes-patterns` | 6 | 1442 | 313k |
| 36 | `craft/engineering/devops/phoenix-project` | 7 | 1119 | 425k |
| 37 | `craft/engineering/devops/seeking-sre` | 7 | 972 | 942k |
| 38 | `craft/engineering/devops/site-reliability-engineering` | 6 | 871 | 227k |
| 39 | `craft/engineering/devops/site-reliability-engineering-handbook` | 6 | 1251 | 170k |
| 40 | `craft/engineering/devops/site-reliability-workbook` | 7 | 443 | 247k |
| 41 | `craft/engineering/devops/vbirds-linux-basic` | 6 | 991 | 116k |
| 42 | `craft/engineering/devops/vbirds-linux-server` | 7 | 948 | 69k |
| 43 | `craft/engineering/engineering-management/become-an-effective-software-engineering-manager` | 7 | 758 | 138k |
| 44 | `craft/engineering/engineering-management/effective-engineer` | 7 | 407 | 89k |
| 45 | `craft/engineering/engineering-management/peopleware` | 7 | 695 | 182k |
| 46 | `craft/engineering/engineering-management/staff-engineers-path` | 6 | 907 | 128k |
| 47 | `craft/engineering/engineering-management/team-topologies` | 7 | 1191 | 308k |
| 48 | `craft/engineering/language-rust/team-geek` | 7 | 819 | 54k |
| 49 | `craft/engineering/security/attacking-network-protocols` | 7 | 1011 | 422k |
| 50 | `craft/engineering/security/browser-hackers-handbook` | 6 | 1265 | 244k |
| 51 | `craft/engineering/security/building-secure-and-reliable-systems` | 7 | 1140 | 862k |
| 52 | `craft/engineering/security/hacking-art-of-exploitation` | 7 | 1151 | 297k |
| 53 | `craft/engineering/security/real-world-bug-hunting` | 6 | 675 | 167k |
| 54 | `craft/engineering/security/security-engineering` | 7 | 1264 | 1719k |
| 55 | `craft/engineering/security/serious-cryptography` | 6 | 1218 | 528k |
| 56 | `craft/engineering/security/web-security-for-developers` | 6 | 1134 | 155k |
| 57 | `craft/engineering/systems-design/analysis-patterns` | 7 | 1011 | 547k |
| 58 | `craft/engineering/systems-design/beautiful-architecture` | 7 | 1154 | 145k |
| 59 | `craft/engineering/systems-design/building-microservices` | 5 | 1477 | 197k |
| 60 | `craft/engineering/systems-design/clean-architecture` | 7 | 494 | 113k |
| 61 | `craft/engineering/systems-design/clean-code` | 8 | 419 | 79k |
| 62 | `craft/engineering/systems-design/designing-data-intensive-applications` | 6 | 819 | 292k |
| 63 | `craft/engineering/systems-design/designing-distributed-systems` | 7 | 1183 | 151k |
| 64 | `craft/engineering/systems-design/distributed-systems-principles-and-paradigms` | 7 | 1602 | 870k |
| 65 | `craft/engineering/systems-design/enterprise-integration-patterns` | 8 | 1783 | 813k |
| 66 | `craft/engineering/systems-design/fundamentals-of-software-architecture` | 7 | 571 | 193k |
| 67 | `craft/engineering/systems-design/get-your-hands-dirty-clean-architecture` | 7 | 728 | 153k |
| 68 | `craft/engineering/systems-design/grokking-advanced-system-design-interview` | 6 | 602 | 307k |
| 69 | `craft/engineering/systems-design/how-linux-works` | 6 | 1183 | 242k |
| 70 | `craft/engineering/systems-design/implementation-patterns` | 7 | 364 | 195k |
| 71 | `craft/engineering/systems-design/microservices-patterns` | 7 | 1039 | 177k |
| 72 | `craft/engineering/systems-design/patterns-of-enterprise-application-architecture` | 7 | 451 | 169k |
| 73 | `craft/engineering/systems-design/philosophy-of-software-design` | 6 | 1210 | 211k |
| 74 | `craft/engineering/systems-design/release-it` | 7 | 877 | 539k |
| 75 | `craft/engineering/systems-design/software-architecture-for-developers-vol1` | 8 | 426 | 76k |
| 76 | `craft/engineering/systems-design/software-architecture-for-developers-vol2` | 7 | 490 | 92k |
| 77 | `craft/engineering/systems-design/software-architecture-in-practice` | 7 | 402 | 365k |
| 78 | `craft/engineering/systems-design/software-architecture-the-hard-parts` | 7 | 1138 | 166k |
| 79 | `craft/engineering/systems-design/system-architecture-design` | 6 | 663 | 135k |
| 80 | `craft/engineering/systems-design/system-design-interview` | 8 | 431 | 784k |
| 81 | `craft/engineering/systems-design/uml-distilled` | 8 | 2033 | 208k |
| 82 | `craft/engineering/systems-design/understanding-distributed-systems` | 7 | 1167 | 261k |
| 83 | `craft/tools/build-systems/manage-your-day-to-day` | 6 | 1045 | 50k |
| 84 | `craft/tools/coffee/uncommon-grounds` | 8 | 590 | 282k |
| 85 | `craft/tools/coffee/world-atlas-of-coffee` | 8 | 573 | 138k |
| 86 | `craft/tools/version-control/building-a-second-brain` | 6 | 1380 | 152k |
| 87 | `craft/tools/version-control/continuous-delivery` | 6 | 718 | 287k |
| 88 | `craft/tools/version-control/deep-work` | 8 | 351 | 54k |
| 89 | `craft/tools/version-control/digital-minimalism` | 6 | 1064 | 47k |
| 90 | `craft/tools/version-control/make-time` | 6 | 776 | 185k |
| 91 | `craft/writing/fiction/bird-by-bird` | 6 | 1279 | 140k |
| 92 | `craft/writing/fiction/brothers-karamazov` | 8 | 512 | 86k |
| 93 | `craft/writing/fiction/chronicles-of-narnia` | 6 | 1075 | 1605k |
| 94 | `craft/writing/fiction/crime-and-punishment` | 7 | 644 | 65k |
| 95 | `craft/writing/fiction/dialogue-the-art-of-verbal-action` | 7 | 529 | 124k |
| 96 | `craft/writing/fiction/on-writing-well` | 8 | 520 | 175k |
| 97 | `craft/writing/fiction/steering-the-craft` | 7 | 596 | 42k |
| 98 | `craft/writing/non-fiction/anatomy-of-story` | 7 | 984 | 120k |
| 99 | `craft/writing/non-fiction/art-of-listening` | 7 | 661 | 39k |
| 100 | `craft/writing/non-fiction/brysons-dictionary-of-troublesome-words` | 7 | 615 | 177k |
| 101 | `craft/writing/non-fiction/copywriters-handbook` | 6 | 778 | 230k |
| 102 | `craft/writing/non-fiction/elements-of-style` | 8 | 263 | 62k |
| 103 | `craft/writing/non-fiction/how-to-read-a-book` | 7 | 949 | 212k |
| 104 | `craft/writing/non-fiction/if-you-want-to-write` | 7 | 639 | 43k |
| 105 | `craft/writing/non-fiction/little-history-of-philosophy` | 6 | 662 | 187k |
| 106 | `craft/writing/non-fiction/on-writing` | 7 | 743 | 61k |
| 107 | `craft/writing/non-fiction/reading-like-a-writer` | 7 | 730 | 44k |
| 108 | `craft/writing/non-fiction/sense-of-style` | 8 | 507 | 134k |
| 109 | `craft/writing/non-fiction/show-your-work` | 8 | 535 | 78k |
| 110 | `craft/writing/non-fiction/storynomics` | 7 | 859 | 133k |
| 111 | `craft/writing/non-fiction/style-lessons-in-clarity-and-grace` | 6 | 621 | 60k |
| 112 | `craft/writing/non-fiction/talking-to-strangers` | 6 | 1077 | 120k |
| 113 | `craft/writing/non-fiction/wild-at-heart` | 7 | 549 | 197k |
| 114 | `craft/writing/non-fiction/working-poor` | 8 | 548 | 67k |
| 115 | `craft/writing/non-fiction/writing-down-the-bones` | 7 | 623 | 178k |
| 116 | `craft/writing/non-fiction/writing-life` | 8 | 573 | 43k |
| 117 | `craft/writing/screenwriting/action-mckee` | 6 | 944 | 143k |
| 118 | `craft/writing/screenwriting/story-mckee` | 6 | 814 | 292k |
| 119 | `faith/spiritual-formation/contemplative/50-spiritual-classics` | 7 | 584 | 408k |
| 120 | `faith/spiritual-formation/contemplative/augustine-confessions` | 7 | 811 | 609k |
| 121 | `faith/spiritual-formation/contemplative/genesee-diary` | 7 | 1398 | 275k |
| 122 | `faith/spiritual-formation/contemplative/inner-voice-of-love` | 6 | 1056 | 111k |
| 123 | `faith/spiritual-formation/contemplative/reaching-out` | 8 | 4130 | 221k |
| 124 | `faith/spiritual-formation/contemplative/road-to-daybreak` | 6 | 2081 | 371k |
| 125 | `faith/spiritual-formation/devotional/adam-gods-beloved` | 7 | 717 | 146k |
| 126 | `faith/spiritual-formation/devotional/can-you-drink-the-cup` | 6 | 1302 | 97k |
| 127 | `faith/spiritual-formation/devotional/four-loves` | 7 | 1453 | 172k |
| 128 | `faith/spiritual-formation/devotional/friendly-snowflake` | 7 | 451 | 31k |
| 129 | `faith/spiritual-formation/devotional/great-divorce` | 7 | 569 | 128k |
| 130 | `faith/spiritual-formation/devotional/grief-observed` | 6 | 916 | 90k |
| 131 | `faith/spiritual-formation/devotional/imitation-of-christ` | 7 | 621 | 337k |
| 132 | `faith/spiritual-formation/devotional/life-of-the-beloved` | 7 | 1368 | 130k |
| 133 | `faith/spiritual-formation/devotional/surprised-by-joy` | 5 | 1181 | 250k |
| 134 | `faith/spiritual-formation/devotional/till-we-have-faces` | 8 | 2792 | 386k |
| 135 | `faith/spiritual-formation/devotional/weight-of-glory` | 5 | 766 | 76k |
| 136 | `faith/spiritual-formation/discipleship/after-you-believe` | 7 | 829 | 469k |
| 137 | `faith/spiritual-formation/discipleship/attached-to-god` | 8 | 1660 | 308k |
| 138 | `faith/spiritual-formation/discipleship/cost-of-discipleship` | 7 | 903 | 161k |
| 139 | `faith/spiritual-formation/discipleship/effective-bible-teaching` | 6 | 820 | 260k |
| 140 | `faith/spiritual-formation/discipleship/great-omission` | 6 | 1253 | 331k |
| 141 | `faith/spiritual-formation/discipleship/life-without-lack` | 9 | 2011 | 382k |
| 142 | `faith/spiritual-formation/discipleship/long-obedience-in-the-same-direction` | 7 | 1150 | 175k |
| 143 | `faith/spiritual-formation/discipleship/pilgrims-progress` | 7 | 575 | 181k |
| 144 | `faith/spiritual-formation/discipleship/renovation-of-the-heart` | 7 | 1132 | 566k |
| 145 | `faith/spiritual-formation/discipleship/ruthless-elimination-of-hurry` | 7 | 3903 | 281k |
| 146 | `faith/spiritual-formation/discipleship/soul-of-shame` | 9 | 2586 | 406k |
| 147 | `faith/spiritual-formation/discipleship/spirit-of-the-disciplines` | 7 | 1115 | 500k |
| 148 | `faith/spiritual-formation/prayer/letters-to-malcolm` | 8 | 428 | 188k |
| 149 | `faith/spiritual-formation/prayer/life-together-prayerbook-of-the-bible` | 6 | 789 | 106k |
| 150 | `faith/theology/apologetics/everlasting-man` | 7 | 811 | 539k |
| 151 | `faith/theology/apologetics/god-in-the-dock` | 8 | 2692 | 637k |
| 152 | `faith/theology/apologetics/mere-christianity` | 6 | 1138 | 251k |
| 153 | `faith/theology/apologetics/miracles` | 7 | 1294 | 352k |
| 154 | `faith/theology/apologetics/problem-of-pain` | 7 | 731 | 137k |
| 155 | `faith/theology/apologetics/screwtape-letters` | 7 | 648 | 56k |
| 156 | `faith/theology/apologetics/simply-christian` | 7 | 546 | 329k |
| 157 | `faith/theology/apologetics/where-the-conflict-really-lies` | 7 | 1028 | 172k |
| 158 | `faith/theology/biblical-studies/21st-century-biblical-homiletics` | 6 | 659 | 267k |
| 159 | `faith/theology/biblical-studies/a-new-testament-biblical-theology` | 7 | 623 | 316k |
| 160 | `faith/theology/biblical-studies/according-to-plan` | 8 | 523 | 323k |
| 161 | `faith/theology/biblical-studies/an-introduction-to-the-new-testament` | 6 | 613 | 1419k |
| 162 | `faith/theology/biblical-studies/an-introduction-to-the-old-testament` | 6 | 602 | 253k |
| 163 | `faith/theology/biblical-studies/ancient-near-eastern-texts` | 6 | 1036 | 164k |
| 164 | `faith/theology/biblical-studies/ancient-near-eastern-thought` | 8 | 574 | 138k |
| 165 | `faith/theology/biblical-studies/basic-christianity` | 6 | 787 | 41k |
| 166 | `faith/theology/biblical-studies/biblical-critical-theory` | 8 | 469 | 195k |
| 167 | `faith/theology/biblical-studies/biblical-theology-goldingay` | 7 | 636 | 874k |
| 168 | `faith/theology/biblical-studies/biblical-theology-vos` | 7 | 1002 | 834k |
| 169 | `faith/theology/biblical-studies/books-of-the-pentateuch` | 8 | 592 | 94k |
| 170 | `faith/theology/biblical-studies/carson-intro-new-testament` | 7 | 606 | 238k |
| 171 | `faith/theology/biblical-studies/casket-empty-old-testament-study-guide` | 5 | 665 | 227k |
| 172 | `faith/theology/biblical-studies/cross-and-the-prodigal` | 7 | 771 | 115k |
| 173 | `faith/theology/biblical-studies/dictionary-of-the-later-new-testament` | 8 | 375 | 360k |
| 174 | `faith/theology/biblical-studies/eat-this-book` | 7 | 399 | 194k |
| 175 | `faith/theology/biblical-studies/every-good-endeavor` | 8 | 424 | 53k |
| 176 | `faith/theology/biblical-studies/evil-and-the-justice-of-god` | 7 | 886 | 90k |
| 177 | `faith/theology/biblical-studies/exegetical-fallacies` | 7 | 3997 | 218k |
| 178 | `faith/theology/biblical-studies/ezra-nehemiah-esther-for-everyone` | 5 | 630 | 275k |
| 179 | `faith/theology/biblical-studies/forgive` | 8 | 414 | 46k |
| 180 | `faith/theology/biblical-studies/from-eden-to-the-new-jerusalem` | 8 | 479 | 89k |
| 181 | `faith/theology/biblical-studies/genesis-waltke` | 7 | 609 | 120k |
| 182 | `faith/theology/biblical-studies/gods-wisdom-for-navigating-life` | 8 | 338 | 402k |
| 183 | `faith/theology/biblical-studies/hard-sayings-of-the-bible` | 7 | 615 | 146k |
| 184 | `faith/theology/biblical-studies/hermeneutical-spiral` | 7 | 942 | 637k |
| 185 | `faith/theology/biblical-studies/hidden-christmas` | 7 | 4252 | 175k |
| 186 | `faith/theology/biblical-studies/how-to-read-the-bible-book-by-book` | 7 | 738 | 285k |
| 187 | `faith/theology/biblical-studies/how-to-read-the-bible-for-all-its-worth` | 8 | 328 | 63k |
| 188 | `faith/theology/biblical-studies/is-there-a-meaning-in-this-text` | 6 | 1161 | 154k |
| 189 | `faith/theology/biblical-studies/issues-facing-christians-today` | 7 | 564 | 116k |
| 190 | `faith/theology/biblical-studies/ivp-bible-background-commentary-nt` | 8 | 533 | 182k |
| 191 | `faith/theology/biblical-studies/ivp-bible-background-commentary-ot` | 6 | 785 | 141k |
| 192 | `faith/theology/biblical-studies/jesus-and-the-victory-of-god` | 7 | 615 | 129k |
| 193 | `faith/theology/biblical-studies/jesus-through-middle-eastern-eyes` | 7 | 644 | 312k |
| 194 | `faith/theology/biblical-studies/lost-world-of-adam-and-eve` | 7 | 731 | 184k |
| 195 | `faith/theology/biblical-studies/lost-world-of-genesis-one` | 7 | 600 | 170k |
| 196 | `faith/theology/biblical-studies/meaning-of-marriage` | 8 | 498 | 46k |
| 197 | `faith/theology/biblical-studies/message-of-1-2-thessalonians` | 8 | 553 | 260k |
| 198 | `faith/theology/biblical-studies/message-of-1-corinthians` | 8 | 458 | 124k |
| 199 | `faith/theology/biblical-studies/message-of-1-peter` | 8 | 439 | 153k |
| 200 | `faith/theology/biblical-studies/message-of-1-timothy-titus` | 8 | 457 | 109k |

_（只列前 200 本，另有 1305 本未列出）_

## 筆記是空的，寫不了（7 本）

docs/ 底下只有章節 frontmatter、沒有內文（< 8000 bytes）。「完整摘要」規定只能取材自筆記，硬寫等於編造——先補筆記再回來。

- `personal/mindset/growth/29-pawn-tickets-3` — 筆記 3070 bytes
- `personal/relationships/community/life-is-a-long-term-accumulation` — 筆記 1461 bytes
- `personal/relationships/parenting/financial-boundaries-with-family` — 筆記 1509 bytes
- `professional/career/skill-building/whats-left-without-your-business-card` — 筆記 7688 bytes
- `professional/communication/persuasion/reinforcements-how-to-get-people-to-help-you` — 筆記 819 bytes
- `professional/finance/investing/attitude-of-the-rich` — 筆記 400 bytes
- `wisdom/philosophy/ethics/live-your-best-life` — 筆記 1100 bytes

## 已寫過、但有項目未過（2 本）

四段都在，缺的是個別項目——補那一項就好，不要當成沒寫過整段重寫。`--todo` 不會挑到這些書。

- `faith/theology/systematic/allure-of-gentleness` — 引用年份
- `personal/mindset/growth/29-pawn-tickets-2` — 引用年份

## 已完成

- `craft/design/architecture/timeless-way-of-building` — 2909 字
- `craft/design/ux/design-of-everyday-things` — 3048 字
- `craft/design/visual/architecture-of-happiness` — 3158 字
- `craft/design/visual/back-of-the-napkin` — 3137 字
- `craft/design/visual/brutal-simplicity-of-thought` — 3022 字
- `craft/design/visual/change-by-design` — 3184 字
- `craft/design/visual/judge-this` — 3191 字
- `craft/design/visual/laws-of-simplicity` — 3160 字
- `craft/design/visual/non-designers-design-book` — 3054 字
- `craft/design/visual/non-designers-presentation` — 2905 字
- `craft/design/visual/only-sales-guide` — 3188 字
- `craft/design/visual/refactoring-ui` — 3200 字
- `craft/design/visual/say-it-with-charts` — 3271 字
- `craft/design/visual/simplicity-cycle` — 3219 字
- `craft/design/visual/steal-like-an-artist` — 2803 字
- `craft/design/visual/web-designers-idea-book` — 2830 字
- `craft/design/visual/wtf-what-is-the-future` — 3092 字
- `craft/engineering/agile/97-things-every-project-manager-should-know` — 3000 字
- `craft/engineering/agile/agile-product-management-with-scrum` — 3041 字
- `craft/engineering/agile/essential-scrum` — 3134 字
- `craft/engineering/agile/extreme-programming-explained` — 3208 字
- `craft/engineering/agile/impact-mapping` — 3984 字
- `craft/engineering/agile/kanban` — 3204 字
- `craft/engineering/agile/kanban-in-action` — 2879 字
- `craft/engineering/agile/planning-extreme-programming` — 3603 字
- `craft/engineering/agile/scrum-the-art-of-doing-twice-the-work-in-half-the-time` — 2858 字
- `craft/engineering/agile/sprint` — 2871 字
- `craft/engineering/agile/succeeding-with-agile` — 3230 字
- `craft/engineering/agile/user-stories-applied` — 2979 字
- `craft/engineering/ai-ml/genai-system-design-interview` — 2967 字
- `craft/engineering/ai-ml/machine-learning-system-design-interview` — 2686 字
- `craft/engineering/ai-ml/mastering-ai-survival-guide` — 3142 字
- `craft/engineering/ai-ml/wujun-age-of-intelligence` — 2893 字
- `craft/engineering/coding-practice/97-things-every-programmer-should-know` — 2989 字
- `craft/engineering/coding-practice/97-things-every-software-architect-should-know` — 2977 字
- `craft/engineering/coding-practice/agile-principles-patterns-practices-csharp` — 3099 字
- `craft/engineering/coding-practice/agile-retrospectives` — 2782 字
- `craft/engineering/coding-practice/algorithms-to-live-by` — 2810 字
- `craft/engineering/coding-practice/apprenticeship-patterns` — 2960 字
- `craft/engineering/coding-practice/are-your-lights-on` — 2654 字
- `craft/engineering/coding-practice/art-of-clean-code` — 2783 字
- `craft/engineering/coding-practice/art-of-doing-science-and-engineering` — 2978 字
- `craft/engineering/coding-practice/art-of-readable-code` — 3901 字
- `craft/engineering/coding-practice/art-of-unit-testing` — 2907 字
- `craft/engineering/coding-practice/balancing-coupling` — 3162 字
- `craft/engineering/coding-practice/bdd-in-action` — 2849 字
- `craft/engineering/coding-practice/big-refactoring` — 2863 字
- `craft/engineering/coding-practice/big-talk-design-patterns` — 2583 字
- `craft/engineering/coding-practice/clean-agile` — 2730 字
- `craft/engineering/coding-practice/clean-code-principles-and-patterns` — 2980 字
- `craft/engineering/coding-practice/clean-coder` — 2948 字
- `craft/engineering/coding-practice/clean-craftsmanship` — 2892 字
- `craft/engineering/coding-practice/code-complete` — 2894 字
- `craft/engineering/coding-practice/code-that-fits-in-your-head` — 3057 字
- `craft/engineering/coding-practice/coders-at-work` — 2945 字
- `craft/engineering/coding-practice/coding-interview-patterns` — 2793 字
- `craft/engineering/coding-practice/cracking-the-coding-interview` — 2769 字
- `craft/engineering/coding-practice/dependency-injection` — 2925 字
- `craft/engineering/coding-practice/design-patterns` — 2873 字
- `craft/engineering/coding-practice/design-patterns-explained` — 2934 字
- `craft/engineering/coding-practice/designing-web-apis` — 2455 字
- `craft/engineering/coding-practice/domain-driven-design` — 2977 字
- `craft/engineering/coding-practice/effective-debugging` — 2784 字
- `craft/engineering/coding-practice/exploring-requirements` — 2936 字
- `craft/engineering/coding-practice/five-lines-of-code` — 3061 字
- `craft/engineering/coding-practice/functional-design-principles-patterns-practices` — 2767 字
- `craft/engineering/coding-practice/good-code-bad-code` — 2808 字
- `craft/engineering/coding-practice/grokking-system-design-interview` — 2439 字
- `craft/engineering/coding-practice/hackers-and-painters` — 2753 字
- `craft/engineering/coding-practice/high-performance-java-persistence` — 2661 字
- `craft/engineering/coding-practice/joel-on-software` — 2669 字
- `craft/engineering/coding-practice/living-documentation` — 2790 字
- `craft/engineering/coding-practice/mis-network-82` — 2551 字
- `craft/engineering/coding-practice/mobile-system-design-interview` — 2560 字
- `craft/engineering/coding-practice/more-joel-on-software` — 2741 字
- `craft/engineering/coding-practice/mythical-man-month` — 2879 字
- `craft/engineering/coding-practice/nine-algorithms-that-changed-the-future` — 2563 字
- `craft/engineering/coding-practice/object-oriented-design-interview` — 2431 字
- `craft/engineering/coding-practice/observability-beginners-guide` — 2902 字
- `craft/engineering/coding-practice/perfect-software` — 2758 字
- `craft/engineering/coding-practice/pragmatic-programmer` — 2896 字
- `craft/engineering/coding-practice/prefactoring` — 3027 字
- `craft/engineering/coding-practice/software-developers-career-guide` — 2637 字
- `craft/engineering/coding-practice/wujun-math-general-course` — 3433 字
- `craft/engineering/systems-design/acing-the-system-design-interview` — 3897 字
- `craft/engineering/systems-design/api-design-patterns` — 3750 字
- `craft/engineering/systems-design/art-of-scalability` — 3796 字
- `craft/writing/fiction/2nd-iteration` — 4030 字
- `faith/spiritual-formation/devotional/lament-for-a-son` — 4177 字
- `faith/theology/apologetics/shadowlands` — 3228 字
- `faith/theology/biblical-studies/sherlock-who-3-new-light` — 2314 字
- `personal/mindset/growth/artists-way` — 3785 字
- `personal/mindset/growth/tuesdays-with-morrie` — 2847 字
- `personal/mindset/self-awareness/scout-mindset` — 2833 字
- `personal/relationships/community/bowling-alone` — 4150 字
- `personal/relationships/dating/home-tonight` — 2588 字
- `personal/relationships/dating/how-to-make-anyone-fall-in-love-with-you` — 2453 字
- `professional/career/skill-building/so-good-they-cant-ignore-you` — 2782 字
- `professional/communication/negotiation/bargaining-for-advantage` — 4012 字
- `professional/communication/persuasion/becoming-a-person-of-influence` — 3844 字
- `professional/communication/persuasion/mba-confidential` — 3199 字
- `professional/communication/persuasion/three-minutes-to-doomsday` — 3815 字
- `professional/communication/public-speaking/slideology` — 2678 字
- `professional/communication/workplace-relations/fierce-conversations` — 3808 字
- `professional/finance/economics/freakonomics` — 4028 字
- `professional/finance/investing/investment-philosophies` — 3996 字
- `professional/leadership/team-building/17-indisputable-laws-of-teamwork` — 4186 字
- `professional/leadership/team-building/hbr-guide-to-leading-through-change` — 2592 字
- `professional/leadership/vision/innovators-prescription` — 3668 字
- `professional/leadership/vision/its-your-ship` — 2649 字
- `wisdom/education/pedagogy/why-dont-students-like-school` — 2958 字
- `wisdom/education/self-learning/online-teaching-technique` — 2477 字
- `wisdom/history/military/art-of-war` — 3485 字
- `wisdom/philosophy/eastern/how-the-world-thinks` — 2529 字
- `wisdom/philosophy/ethics/12-rules-for-life` — 2637 字
- `wisdom/philosophy/ethics/art-as-therapy` — 4104 字
- `wisdom/philosophy/ethics/intuition-pumps` — 3021 字
- `wisdom/philosophy/ethics/reading-as-a-wilderness` — 2414 字
- `wisdom/philosophy/political-philosophy/theory-of-justice` — 4170 字
- `wisdom/science/cognitive/forgotten-language` — 3931 字
- `wisdom/science/pharmacology/applied-therapeutics` — 3426 字
- `wisdom/science/pharmacology/basic-clinical-pharmacology` — 3426 字
- `wisdom/science/pharmacology/pharmacotherapy-principles-and-practice` — 3600 字
