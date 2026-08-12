# 深度概覽改寫進度

> 衍生檔，勿手改。重新產生：`python3 scripts/audit-overview.py --report`

最後更新：2026-08-13

## 總覽

| | 數量 | 佔比 |
|---|---:|---:|
| 全庫 | 1618 | 100% |
| **已改寫並通過品檢** | **26** | **1.6%** |
| 仍是舊三段格式 | 1592 | 98.4% |

```
[··················································] 1.6%
```

## 各項未通過

| 檢查 | 未通過 | 佔比 |
|---|---:|---:|
| 四段齊全 | 1592 | 98% |
| 作者的位置 長度 | 1592 | 98% |
| 定位 長度 | 1592 | 98% |
| 這本書的限制 長度 | 1592 | 98% |
| 限制段有實質內容 | 1592 | 98% |
| 引用年份 | 1419 | 88% |
| 點名可查證的作品／人名 | 1055 | 65% |
| 完整摘要 長度 | 535 | 33% |
| 無空洞讚美 | 48 | 3% |

## 待改寫（依 slug 排序）

刻意不按筆記量或未通過項數排序——挑書的順序由人決定，這裡只負責把還沒做的列全。

| # | 書 | 未通過 | 概覽字數 | 筆記 |
|---:|---|---:|---:|---:|
| 1 | `craft/design/architecture/timeless-way-of-building` | 7 | 662 | 176k |
| 2 | `craft/design/ux/design-of-everyday-things` | 7 | 1337 | 517k |
| 3 | `craft/design/visual/architecture-of-happiness` | 8 | 545 | 109k |
| 4 | `craft/design/visual/back-of-the-napkin` | 7 | 964 | 258k |
| 5 | `craft/design/visual/brutal-simplicity-of-thought` | 8 | 625 | 44k |
| 6 | `craft/design/visual/change-by-design` | 6 | 759 | 122k |
| 7 | `craft/design/visual/judge-this` | 6 | 663 | 83k |
| 8 | `craft/design/visual/laws-of-simplicity` | 7 | 1018 | 69k |
| 9 | `craft/design/visual/non-designers-design-book` | 7 | 953 | 134k |
| 10 | `craft/design/visual/non-designers-presentation` | 6 | 764 | 41k |
| 11 | `craft/design/visual/only-sales-guide` | 6 | 842 | 107k |
| 12 | `craft/design/visual/refactoring-ui` | 7 | 725 | 125k |
| 13 | `craft/design/visual/say-it-with-charts` | 7 | 1640 | 126k |
| 14 | `craft/design/visual/simplicity-cycle` | 7 | 966 | 102k |
| 15 | `craft/design/visual/steal-like-an-artist` | 8 | 319 | 47k |
| 16 | `craft/design/visual/web-designers-idea-book` | 7 | 500 | 36k |
| 17 | `craft/design/visual/wtf-what-is-the-future` | 8 | 399 | 39k |
| 18 | `craft/engineering/agile/97-things-every-project-manager-should-know` | 8 | 384 | 243k |
| 19 | `craft/engineering/agile/agile-product-management-with-scrum` | 6 | 930 | 51k |
| 20 | `craft/engineering/agile/essential-scrum` | 7 | 532 | 290k |
| 21 | `craft/engineering/agile/extreme-programming-explained` | 7 | 1309 | 258k |
| 22 | `craft/engineering/agile/kanban` | 7 | 666 | 381k |
| 23 | `craft/engineering/agile/kanban-in-action` | 7 | 371 | 309k |
| 24 | `craft/engineering/agile/planning-extreme-programming` | 8 | 2317 | 174k |
| 25 | `craft/engineering/agile/scrum-the-art-of-doing-twice-the-work-in-half-the-time` | 8 | 410 | 33k |
| 26 | `craft/engineering/agile/sprint` | 7 | 819 | 63k |
| 27 | `craft/engineering/agile/succeeding-with-agile` | 7 | 492 | 250k |
| 28 | `craft/engineering/agile/user-stories-applied` | 6 | 1012 | 60k |
| 29 | `craft/engineering/ai-ml/genai-system-design-interview` | 7 | 782 | 305k |
| 30 | `craft/engineering/ai-ml/machine-learning-system-design-interview` | 7 | 797 | 250k |
| 31 | `craft/engineering/ai-ml/mastering-ai-survival-guide` | 7 | 766 | 196k |
| 32 | `craft/engineering/ai-ml/wujun-age-of-intelligence` | 7 | 670 | 390k |
| 33 | `craft/engineering/coding-practice/97-things-every-programmer-should-know` | 8 | 336 | 220k |
| 34 | `craft/engineering/coding-practice/97-things-every-software-architect-should-know` | 8 | 358 | 197k |
| 35 | `craft/engineering/coding-practice/agile-principles-patterns-practices-csharp` | 6 | 1064 | 222k |
| 36 | `craft/engineering/coding-practice/agile-retrospectives` | 7 | 699 | 103k |
| 37 | `craft/engineering/coding-practice/algorithms-to-live-by` | 6 | 764 | 133k |
| 38 | `craft/engineering/coding-practice/apprenticeship-patterns` | 8 | 488 | 112k |
| 39 | `craft/engineering/coding-practice/art-of-clean-code` | 6 | 709 | 70k |
| 40 | `craft/engineering/coding-practice/art-of-doing-science-and-engineering` | 8 | 808 | 166k |
| 41 | `craft/engineering/coding-practice/art-of-unit-testing` | 7 | 418 | 174k |
| 42 | `craft/engineering/coding-practice/balancing-coupling` | 6 | 819 | 172k |
| 43 | `craft/engineering/coding-practice/balancing-coupling-in-software-design` | 6 | 1230 | 143k |
| 44 | `craft/engineering/coding-practice/bdd-in-action` | 7 | 994 | 98k |
| 45 | `craft/engineering/coding-practice/big-refactoring` | 8 | 534 | 131k |
| 46 | `craft/engineering/coding-practice/big-talk-design-patterns` | 8 | 522 | 179k |
| 47 | `craft/engineering/coding-practice/clean-agile` | 7 | 432 | 93k |
| 48 | `craft/engineering/coding-practice/clean-code-principles-and-patterns` | 8 | 525 | 243k |
| 49 | `craft/engineering/coding-practice/clean-coder` | 8 | 462 | 58k |
| 50 | `craft/engineering/coding-practice/clean-craftsmanship` | 7 | 478 | 172k |
| 51 | `craft/engineering/coding-practice/code-complete` | 7 | 467 | 350k |
| 52 | `craft/engineering/coding-practice/code-that-fits-in-your-head` | 7 | 462 | 108k |
| 53 | `craft/engineering/coding-practice/coders-at-work` | 8 | 523 | 238k |
| 54 | `craft/engineering/coding-practice/coding-interview-patterns` | 7 | 709 | 812k |
| 55 | `craft/engineering/coding-practice/cracking-the-coding-interview` | 6 | 948 | 235k |
| 56 | `craft/engineering/coding-practice/dependency-injection` | 6 | 1176 | 87k |
| 57 | `craft/engineering/coding-practice/design-patterns` | 6 | 998 | 182k |
| 58 | `craft/engineering/coding-practice/design-patterns-explained` | 6 | 946 | 140k |
| 59 | `craft/engineering/coding-practice/designing-web-apis` | 8 | 471 | 228k |
| 60 | `craft/engineering/coding-practice/domain-driven-design` | 7 | 1445 | 275k |
| 61 | `craft/engineering/coding-practice/effective-debugging` | 8 | 499 | 185k |
| 62 | `craft/engineering/coding-practice/exploring-requirements` | 8 | 835 | 195k |
| 63 | `craft/engineering/coding-practice/five-lines-of-code` | 7 | 550 | 116k |
| 64 | `craft/engineering/coding-practice/functional-design-principles-patterns-practices` | 7 | 770 | 176k |
| 65 | `craft/engineering/coding-practice/good-code-bad-code` | 7 | 569 | 173k |
| 66 | `craft/engineering/coding-practice/grokking-system-design-interview` | 6 | 665 | 181k |
| 67 | `craft/engineering/coding-practice/hackers-and-painters` | 7 | 499 | 103k |
| 68 | `craft/engineering/coding-practice/high-performance-java-persistence` | 6 | 1280 | 176k |
| 69 | `craft/engineering/coding-practice/joel-on-software` | 8 | 402 | 175k |
| 70 | `craft/engineering/coding-practice/living-documentation` | 6 | 940 | 160k |
| 71 | `craft/engineering/coding-practice/mis-network-82` | 8 | 288 | 127k |
| 72 | `craft/engineering/coding-practice/mobile-system-design-interview` | 7 | 621 | 355k |
| 73 | `craft/engineering/coding-practice/more-joel-on-software` | 8 | 385 | 114k |
| 74 | `craft/engineering/coding-practice/mythical-man-month` | 8 | 408 | 105k |
| 75 | `craft/engineering/coding-practice/nine-algorithms-that-changed-the-future` | 7 | 778 | 104k |
| 76 | `craft/engineering/coding-practice/object-oriented-design-interview` | 7 | 718 | 285k |
| 77 | `craft/engineering/coding-practice/observability-beginners-guide` | 7 | 1041 | 187k |
| 78 | `craft/engineering/coding-practice/perfect-software` | 7 | 848 | 140k |
| 79 | `craft/engineering/coding-practice/pragmatic-programmer` | 8 | 361 | 223k |
| 80 | `craft/engineering/coding-practice/prefactoring` | 6 | 725 | 92k |
| 81 | `craft/engineering/coding-practice/programmer-self-cultivation` | 6 | 663 | 145k |
| 82 | `craft/engineering/coding-practice/programming-pearls` | 7 | 812 | 50k |
| 83 | `craft/engineering/coding-practice/refactoring` | 8 | 409 | 86k |
| 84 | `craft/engineering/coding-practice/refactoring-for-software-design-smells` | 7 | 743 | 164k |
| 85 | `craft/engineering/coding-practice/refactoring-to-patterns` | 7 | 1238 | 496k |
| 86 | `craft/engineering/coding-practice/rules-of-programming` | 7 | 848 | 139k |
| 87 | `craft/engineering/coding-practice/running-on-empty` | 6 | 1290 | 77k |
| 88 | `craft/engineering/coding-practice/seriously-good-software` | 8 | 453 | 76k |
| 89 | `craft/engineering/coding-practice/smalltalk-best-practice-patterns` | 8 | 2137 | 288k |
| 90 | `craft/engineering/coding-practice/software-architect-12-disciplines` | 6 | 745 | 188k |
| 91 | `craft/engineering/coding-practice/software-architect-elevator` | 6 | 926 | 340k |
| 92 | `craft/engineering/coding-practice/software-engineering-at-google` | 7 | 427 | 632k |
| 93 | `craft/engineering/coding-practice/specification-by-example` | 6 | 786 | 177k |
| 94 | `craft/engineering/coding-practice/test-driven-development` | 8 | 403 | 180k |
| 95 | `craft/engineering/coding-practice/thinking-in-programming-paradigms-and-oop` | 8 | 375 | 42k |
| 96 | `craft/engineering/coding-practice/tidy-first` | 7 | 1497 | 109k |
| 97 | `craft/engineering/coding-practice/working-effectively-with-legacy-code` | 7 | 468 | 176k |
| 98 | `craft/engineering/coding-practice/wujun-beauty-of-math` | 8 | 516 | 119k |
| 99 | `craft/engineering/coding-practice/wujun-soul-of-computing` | 7 | 773 | 111k |
| 100 | `craft/engineering/coding-practice/zen-programmer` | 8 | 559 | 8k |
| 101 | `craft/engineering/databases/art-of-postgresql` | 6 | 977 | 433k |
| 102 | `craft/engineering/databases/cqrs-command-query-responsibility-segregation` | 7 | 922 | 94k |
| 103 | `craft/engineering/databases/data-warehouse-toolkit` | 7 | 934 | 679k |
| 104 | `craft/engineering/databases/database-internals` | 7 | 900 | 239k |
| 105 | `craft/engineering/databases/high-performance-mysql` | 7 | 523 | 348k |
| 106 | `craft/engineering/databases/nosql-distilled` | 7 | 1022 | 261k |
| 107 | `craft/engineering/databases/postgresql-14-internals` | 7 | 1308 | 600k |
| 108 | `craft/engineering/databases/sql-performance-explained` | 7 | 1370 | 228k |
| 109 | `craft/engineering/databases/sql-server-2025-query-performance-tuning` | 7 | 769 | 223k |
| 110 | `craft/engineering/databases/sql-server-2025-unveiled` | 5 | 1096 | 143k |
| 111 | `craft/engineering/devops/30-days-of-gitlab` | 8 | 490 | 20k |
| 112 | `craft/engineering/devops/accelerate` | 7 | 1095 | 235k |
| 113 | `craft/engineering/devops/devops-handbook` | 6 | 984 | 178k |
| 114 | `craft/engineering/devops/kubernetes-in-action` | 8 | 3971 | 683k |
| 115 | `craft/engineering/devops/kubernetes-patterns` | 6 | 1442 | 313k |
| 116 | `craft/engineering/devops/phoenix-project` | 7 | 1119 | 425k |
| 117 | `craft/engineering/devops/seeking-sre` | 7 | 972 | 942k |
| 118 | `craft/engineering/devops/site-reliability-engineering` | 6 | 871 | 227k |
| 119 | `craft/engineering/devops/site-reliability-engineering-handbook` | 6 | 1251 | 170k |
| 120 | `craft/engineering/devops/site-reliability-workbook` | 7 | 443 | 247k |
| 121 | `craft/engineering/devops/vbirds-linux-basic` | 6 | 991 | 116k |
| 122 | `craft/engineering/devops/vbirds-linux-server` | 7 | 948 | 69k |
| 123 | `craft/engineering/engineering-management/become-an-effective-software-engineering-manager` | 7 | 758 | 138k |
| 124 | `craft/engineering/engineering-management/effective-engineer` | 7 | 407 | 89k |
| 125 | `craft/engineering/engineering-management/peopleware` | 7 | 695 | 182k |
| 126 | `craft/engineering/engineering-management/staff-engineers-path` | 6 | 907 | 128k |
| 127 | `craft/engineering/engineering-management/team-topologies` | 7 | 1191 | 308k |
| 128 | `craft/engineering/language-rust/team-geek` | 7 | 819 | 54k |
| 129 | `craft/engineering/security/attacking-network-protocols` | 7 | 1011 | 422k |
| 130 | `craft/engineering/security/browser-hackers-handbook` | 6 | 1265 | 244k |
| 131 | `craft/engineering/security/building-secure-and-reliable-systems` | 7 | 1140 | 862k |
| 132 | `craft/engineering/security/hacking-art-of-exploitation` | 7 | 1151 | 297k |
| 133 | `craft/engineering/security/real-world-bug-hunting` | 6 | 675 | 167k |
| 134 | `craft/engineering/security/security-engineering` | 6 | 1264 | 1719k |
| 135 | `craft/engineering/security/serious-cryptography` | 6 | 1218 | 528k |
| 136 | `craft/engineering/security/web-security-for-developers` | 6 | 1134 | 155k |
| 137 | `craft/engineering/systems-design/analysis-patterns` | 7 | 1011 | 547k |
| 138 | `craft/engineering/systems-design/beautiful-architecture` | 7 | 1154 | 145k |
| 139 | `craft/engineering/systems-design/building-microservices` | 5 | 1477 | 197k |
| 140 | `craft/engineering/systems-design/clean-architecture` | 7 | 494 | 113k |
| 141 | `craft/engineering/systems-design/clean-code` | 8 | 419 | 79k |
| 142 | `craft/engineering/systems-design/designing-data-intensive-applications` | 6 | 819 | 292k |
| 143 | `craft/engineering/systems-design/designing-distributed-systems` | 7 | 1183 | 151k |
| 144 | `craft/engineering/systems-design/distributed-systems-principles-and-paradigms` | 7 | 1602 | 870k |
| 145 | `craft/engineering/systems-design/enterprise-integration-patterns` | 8 | 1783 | 813k |
| 146 | `craft/engineering/systems-design/fundamentals-of-software-architecture` | 7 | 571 | 193k |
| 147 | `craft/engineering/systems-design/get-your-hands-dirty-clean-architecture` | 7 | 728 | 153k |
| 148 | `craft/engineering/systems-design/grokking-advanced-system-design-interview` | 6 | 602 | 307k |
| 149 | `craft/engineering/systems-design/how-linux-works` | 6 | 1183 | 242k |
| 150 | `craft/engineering/systems-design/implementation-patterns` | 7 | 364 | 195k |
| 151 | `craft/engineering/systems-design/microservices-patterns` | 7 | 1039 | 177k |
| 152 | `craft/engineering/systems-design/patterns-of-enterprise-application-architecture` | 7 | 451 | 169k |
| 153 | `craft/engineering/systems-design/philosophy-of-software-design` | 6 | 1210 | 211k |
| 154 | `craft/engineering/systems-design/release-it` | 7 | 877 | 539k |
| 155 | `craft/engineering/systems-design/software-architecture-for-developers-vol1` | 8 | 426 | 76k |
| 156 | `craft/engineering/systems-design/software-architecture-for-developers-vol2` | 7 | 490 | 92k |
| 157 | `craft/engineering/systems-design/software-architecture-in-practice` | 7 | 402 | 365k |
| 158 | `craft/engineering/systems-design/software-architecture-the-hard-parts` | 7 | 1138 | 166k |
| 159 | `craft/engineering/systems-design/system-architecture-design` | 6 | 663 | 135k |
| 160 | `craft/engineering/systems-design/system-design-interview` | 8 | 431 | 784k |
| 161 | `craft/engineering/systems-design/uml-distilled` | 8 | 2033 | 208k |
| 162 | `craft/engineering/systems-design/understanding-distributed-systems` | 7 | 1167 | 261k |
| 163 | `craft/tools/build-systems/manage-your-day-to-day` | 6 | 1045 | 50k |
| 164 | `craft/tools/coffee/uncommon-grounds` | 8 | 590 | 282k |
| 165 | `craft/tools/coffee/world-atlas-of-coffee` | 8 | 573 | 138k |
| 166 | `craft/tools/version-control/building-a-second-brain` | 6 | 1380 | 152k |
| 167 | `craft/tools/version-control/continuous-delivery` | 6 | 718 | 287k |
| 168 | `craft/tools/version-control/deep-work` | 8 | 351 | 54k |
| 169 | `craft/tools/version-control/digital-minimalism` | 6 | 1064 | 47k |
| 170 | `craft/tools/version-control/make-time` | 6 | 776 | 185k |
| 171 | `craft/writing/fiction/bird-by-bird` | 6 | 1279 | 140k |
| 172 | `craft/writing/fiction/brothers-karamazov` | 8 | 512 | 86k |
| 173 | `craft/writing/fiction/chronicles-of-narnia` | 6 | 1075 | 1605k |
| 174 | `craft/writing/fiction/crime-and-punishment` | 7 | 644 | 65k |
| 175 | `craft/writing/fiction/dialogue-the-art-of-verbal-action` | 7 | 529 | 124k |
| 176 | `craft/writing/fiction/on-writing-well` | 8 | 520 | 175k |
| 177 | `craft/writing/fiction/steering-the-craft` | 7 | 596 | 42k |
| 178 | `craft/writing/non-fiction/anatomy-of-story` | 7 | 984 | 120k |
| 179 | `craft/writing/non-fiction/art-of-listening` | 7 | 661 | 39k |
| 180 | `craft/writing/non-fiction/brysons-dictionary-of-troublesome-words` | 7 | 615 | 177k |
| 181 | `craft/writing/non-fiction/copywriters-handbook` | 6 | 778 | 230k |
| 182 | `craft/writing/non-fiction/elements-of-style` | 8 | 263 | 62k |
| 183 | `craft/writing/non-fiction/how-to-read-a-book` | 7 | 949 | 212k |
| 184 | `craft/writing/non-fiction/if-you-want-to-write` | 7 | 639 | 43k |
| 185 | `craft/writing/non-fiction/little-history-of-philosophy` | 6 | 662 | 187k |
| 186 | `craft/writing/non-fiction/on-writing` | 7 | 743 | 61k |
| 187 | `craft/writing/non-fiction/reading-like-a-writer` | 7 | 730 | 44k |
| 188 | `craft/writing/non-fiction/sense-of-style` | 8 | 507 | 134k |
| 189 | `craft/writing/non-fiction/show-your-work` | 8 | 535 | 78k |
| 190 | `craft/writing/non-fiction/storynomics` | 7 | 859 | 133k |
| 191 | `craft/writing/non-fiction/style-lessons-in-clarity-and-grace` | 6 | 621 | 60k |
| 192 | `craft/writing/non-fiction/talking-to-strangers` | 6 | 1077 | 120k |
| 193 | `craft/writing/non-fiction/wild-at-heart` | 7 | 549 | 197k |
| 194 | `craft/writing/non-fiction/working-poor` | 8 | 548 | 67k |
| 195 | `craft/writing/non-fiction/writing-down-the-bones` | 7 | 623 | 178k |
| 196 | `craft/writing/non-fiction/writing-life` | 8 | 573 | 43k |
| 197 | `craft/writing/screenwriting/action-mckee` | 6 | 944 | 143k |
| 198 | `craft/writing/screenwriting/story-mckee` | 6 | 814 | 292k |
| 199 | `faith/spiritual-formation/contemplative/50-spiritual-classics` | 7 | 584 | 408k |
| 200 | `faith/spiritual-formation/contemplative/augustine-confessions` | 7 | 811 | 609k |

_（只列前 200 本，另有 1385 本未列出）_

## 筆記是空的，寫不了（7 本）

docs/ 底下只有章節 frontmatter、沒有內文（< 8000 bytes）。「完整摘要」規定只能取材自筆記，硬寫等於編造——先補筆記再回來。

- `personal/mindset/growth/29-pawn-tickets-3` — 筆記 3070 bytes
- `personal/relationships/community/life-is-a-long-term-accumulation` — 筆記 1461 bytes
- `personal/relationships/parenting/financial-boundaries-with-family` — 筆記 1509 bytes
- `professional/career/skill-building/whats-left-without-your-business-card` — 筆記 7688 bytes
- `professional/communication/persuasion/reinforcements-how-to-get-people-to-help-you` — 筆記 819 bytes
- `professional/finance/investing/attitude-of-the-rich` — 筆記 400 bytes
- `wisdom/philosophy/ethics/live-your-best-life` — 筆記 1100 bytes

## 已完成

- `craft/engineering/coding-practice/are-your-lights-on` — 2654 字
- `craft/engineering/coding-practice/software-developers-career-guide` — 2632 字
- `craft/engineering/coding-practice/wujun-math-general-course` — 3433 字
- `faith/theology/apologetics/shadowlands` — 3228 字
- `faith/theology/biblical-studies/sherlock-who-3-new-light` — 2314 字
- `faith/theology/systematic/allure-of-gentleness` — 2790 字
- `personal/mindset/growth/29-pawn-tickets-2` — 2494 字
- `personal/mindset/growth/tuesdays-with-morrie` — 2847 字
- `personal/mindset/self-awareness/scout-mindset` — 2833 字
- `personal/relationships/dating/home-tonight` — 2588 字
- `personal/relationships/dating/how-to-make-anyone-fall-in-love-with-you` — 2453 字
- `professional/career/skill-building/so-good-they-cant-ignore-you` — 2778 字
- `professional/communication/persuasion/mba-confidential` — 3196 字
- `professional/communication/public-speaking/slideology` — 2678 字
- `professional/leadership/team-building/hbr-guide-to-leading-through-change` — 2592 字
- `professional/leadership/vision/innovators-prescription` — 3668 字
- `professional/leadership/vision/its-your-ship` — 2649 字
- `wisdom/education/pedagogy/why-dont-students-like-school` — 2958 字
- `wisdom/education/self-learning/online-teaching-technique` — 2477 字
- `wisdom/philosophy/eastern/how-the-world-thinks` — 2529 字
- `wisdom/philosophy/ethics/12-rules-for-life` — 2628 字
- `wisdom/philosophy/ethics/intuition-pumps` — 3021 字
- `wisdom/philosophy/ethics/reading-as-a-wilderness` — 2414 字
- `wisdom/science/pharmacology/applied-therapeutics` — 3425 字
- `wisdom/science/pharmacology/basic-clinical-pharmacology` — 3422 字
- `wisdom/science/pharmacology/pharmacotherapy-principles-and-practice` — 3599 字
