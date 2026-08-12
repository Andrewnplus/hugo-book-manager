# 深度概覽改寫進度

> 衍生檔，勿手改。重新產生：`python3 scripts/audit-overview.py --report`

最後更新：2026-08-13

## 總覽

| | 數量 | 佔比 |
|---|---:|---:|
| 全庫 | 1618 | 100% |
| **已改寫並通過品檢** | **16** | **1.0%** |
| 仍是舊三段格式 | 1602 | 99.0% |

```
[··················································] 1.0%
```

## 各項未通過

| 檢查 | 未通過 | 佔比 |
|---|---:|---:|
| 四段齊全 | 1602 | 99% |
| 作者的位置 長度 | 1602 | 99% |
| 定位 長度 | 1602 | 99% |
| 這本書的限制 長度 | 1602 | 99% |
| 限制段有實質內容 | 1602 | 99% |
| 引用年份 | 1429 | 88% |
| 英文原名 | 1158 | 72% |
| 完整摘要 長度 | 545 | 34% |
| 無空洞讚美 | 55 | 3% |

## 待改寫（依未通過項數 → 筆記量排序）

筆記量是排序依據：筆記厚而概覽薄，代表素材就在那裡沒被用上，重做的投報率最高。

| # | 書 | 未通過 | 概覽字數 | 筆記 |
|---:|---|---:|---:|---:|
| 1 | `professional/communication/public-speaking/slideology` | 9 | 561 | 97k |
| 2 | `wisdom/education/self-learning/online-teaching-technique` | 9 | 581 | 65k |
| 3 | `craft/engineering/coding-practice/are-your-lights-on` | 9 | 497 | 30k |
| 4 | `personal/mindset/growth/29-pawn-tickets-2` | 9 | 544 | 20k |
| 5 | `wisdom/philosophy/ethics/reading-as-a-wilderness` | 9 | 322 | 16k |
| 6 | `professional/communication/persuasion/mba-confidential` | 9 | 414 | 11k |
| 7 | `faith/theology/apologetics/shadowlands` | 9 | 519 | 11k |
| 8 | `personal/relationships/parenting/financial-boundaries-with-family` | 9 | 409 | 1k |
| 9 | `personal/relationships/community/life-is-a-long-term-accumulation` | 9 | 590 | 1k |
| 10 | `wisdom/science/pharmacology/pharmacotherapy-principles-and-practice` | 8 | 538 | 1247k |
| 11 | `wisdom/philosophy/ethics/beautiful-thoughts-from-emerson` | 8 | 536 | 1061k |
| 12 | `wisdom/science/pharmacology/basic-clinical-pharmacology` | 8 | 555 | 900k |
| 13 | `wisdom/science/pharmacology/applied-therapeutics` | 8 | 518 | 832k |
| 14 | `craft/engineering/systems-design/enterprise-integration-patterns` | 8 | 1783 | 813k |
| 15 | `craft/engineering/systems-design/system-design-interview` | 8 | 431 | 784k |
| 16 | `wisdom/science/evolution/origin-of-species` | 8 | 1454 | 778k |
| 17 | `faith/theology/biblical-studies/paul-a-biography` | 8 | 2610 | 762k |
| 18 | `craft/engineering/devops/kubernetes-in-action` | 8 | 3971 | 683k |
| 19 | `faith/theology/apologetics/god-in-the-dock` | 8 | 2692 | 637k |
| 20 | `professional/finance/investing/dynamic-hedging` | 8 | 4167 | 601k |
| 21 | `faith/theology/systematic/systematic-theology` | 8 | 355 | 578k |
| 22 | `faith/theology/biblical-studies/message-of-psalms-73-150` | 8 | 581 | 575k |
| 23 | `professional/finance/real-estate/real-book-of-real-estate` | 8 | 552 | 563k |
| 24 | `personal/relationships/community/emily-posts-etiquette-19th-edition` | 8 | 516 | 532k |
| 25 | `faith/theology/biblical-studies/prophets-heschel` | 8 | 538 | 509k |
| 26 | `professional/finance/economics/wealth-of-nations` | 8 | 3910 | 508k |
| 27 | `professional/communication/persuasion/48-laws-of-power` | 8 | 431 | 504k |
| 28 | `professional/business/management/high-growth-handbook` | 8 | 1930 | 503k |
| 29 | `wisdom/science/statistics/statistical-consequences-of-fat-tails` | 8 | 4535 | 489k |
| 30 | `wisdom/education/self-learning/teaching-technique` | 8 | 415 | 481k |
| 31 | `faith/theology/systematic/moses-in-the-clinic` | 8 | 595 | 471k |
| 32 | `faith/theology/pastoral/seeing-with-new-eyes` | 8 | 2754 | 462k |
| 33 | `professional/finance/economics/general-theory-of-employment-interest-and-money` | 8 | 4277 | 448k |
| 34 | `professional/career/job-search/what-color-is-your-parachute` | 8 | 1168 | 447k |
| 35 | `faith/theology/biblical-studies/message-of-mark` | 8 | 567 | 417k |
| 36 | `professional/finance/personal-finance/retire-young-retire-rich` | 8 | 583 | 411k |
| 37 | `faith/spiritual-formation/contemplative/50-spiritual-classics` | 8 | 584 | 408k |
| 38 | `faith/spiritual-formation/discipleship/soul-of-shame` | 8 | 2586 | 406k |
| 39 | `faith/theology/biblical-studies/gods-wisdom-for-navigating-life` | 8 | 338 | 402k |
| 40 | `faith/theology/pastoral/story-of-christian-theology` | 8 | 536 | 393k |
| 41 | `faith/theology/biblical-studies/message-of-ezekiel` | 8 | 602 | 390k |
| 42 | `professional/finance/personal-finance/fake-money-fake-teachers-fake-assets` | 8 | 550 | 383k |
| 43 | `faith/spiritual-formation/discipleship/life-without-lack` | 8 | 2011 | 382k |
| 44 | `faith/theology/systematic/we-who-wrestle-with-god` | 8 | 599 | 367k |
| 45 | `faith/theology/systematic/along-with-moses` | 8 | 560 | 366k |
| 46 | `professional/leadership/team-building/extreme-ownership` | 8 | 2006 | 365k |
| 47 | `personal/lifestyle/style/how-to-be-a-man` | 8 | 408 | 361k |
| 48 | `faith/theology/biblical-studies/dictionary-of-the-later-new-testament` | 8 | 375 | 360k |
| 49 | `personal/mindset/growth/change-your-thinking-change-your-life-tracy` | 8 | 4109 | 359k |
| 50 | `personal/relationships/marriage/boundaries-in-marriage` | 8 | 1896 | 357k |
| 51 | `personal/mindset/growth/wan-weigang-master` | 8 | 488 | 357k |
| 52 | `craft/engineering/coding-practice/code-complete` | 8 | 467 | 350k |
| 53 | `craft/engineering/databases/high-performance-mysql` | 8 | 523 | 348k |
| 54 | `professional/finance/investing/50-questions-retail-investors` | 8 | 487 | 348k |
| 55 | `faith/theology/biblical-studies/message-of-samuel` | 8 | 519 | 347k |
| 56 | `professional/leadership/decision-making/breaking-roberts-rules` | 8 | 504 | 341k |
| 57 | `professional/career/job-search/cracking-the-tech-career` | 8 | 486 | 339k |
| 58 | `professional/career/skill-building/myself-and-other-more-important-matters` | 8 | 375 | 336k |
| 59 | `professional/career/side-hustle/100-startup` | 8 | 1803 | 335k |
| 60 | `professional/leadership/vision/managers-path` | 8 | 387 | 335k |
| 61 | `faith/theology/pastoral/when-people-are-big-and-god-is-small` | 8 | 2078 | 333k |
| 62 | `personal/mindset/growth/wujun-abundance` | 8 | 455 | 332k |
| 63 | `faith/theology/biblical-studies/according-to-plan` | 8 | 523 | 323k |
| 64 | `professional/communication/persuasion/5-min-mba-personal` | 8 | 476 | 320k |
| 65 | `professional/leadership/culture/dare-to-lead` | 8 | 5757 | 319k |
| 66 | `faith/theology/biblical-studies/mission-of-god` | 8 | 538 | 312k |
| 67 | `faith/spiritual-formation/discipleship/attached-to-god` | 8 | 1660 | 308k |
| 68 | `faith/theology/biblical-studies/message-of-genesis-bst` | 8 | 575 | 290k |
| 69 | `professional/finance/economics/economics-in-one-lesson` | 8 | 3864 | 290k |
| 70 | `professional/business/management/management-challenges-for-21st-century` | 8 | 1758 | 290k |
| 71 | `faith/theology/biblical-studies/message-of-job` | 8 | 577 | 289k |
| 72 | `craft/engineering/coding-practice/smalltalk-best-practice-patterns` | 8 | 2137 | 288k |
| 73 | `wisdom/philosophy/ethics/revolution-of-hope` | 8 | 1773 | 287k |
| 74 | `personal/relationships/community/wujun-attitude` | 8 | 393 | 286k |
| 75 | `craft/tools/coffee/uncommon-grounds` | 8 | 590 | 282k |
| 76 | `wisdom/philosophy/ethics/wujun-realm` | 8 | 481 | 277k |
| 77 | `faith/theology/systematic/hearing-god` | 8 | 551 | 276k |
| 78 | `professional/leadership/team-building/leading-change` | 8 | 3635 | 274k |
| 79 | `professional/career/job-search/60-seconds-and-youre-hired` | 8 | 1952 | 269k |
| 80 | `professional/communication/storytelling/resonate-visual-stories-transform-audiences` | 8 | 575 | 267k |
| 81 | `faith/theology/systematic/twentieth-century-theologians` | 8 | 775 | 264k |
| 82 | `faith/theology/biblical-studies/message-of-joshua` | 8 | 595 | 264k |
| 83 | `personal/relationships/parenting/rules-of-life` | 8 | 449 | 263k |
| 84 | `faith/theology/biblical-studies/old-testament-theology-waltke` | 8 | 593 | 263k |
| 85 | `professional/leadership/vision/360-degree-leader` | 8 | 472 | 263k |
| 86 | `faith/theology/biblical-studies/message-of-1-2-thessalonians` | 8 | 553 | 260k |
| 87 | `professional/finance/personal-finance/cashflow-quadrant` | 8 | 541 | 260k |
| 88 | `wisdom/history/civilization/wujun-context` | 8 | 404 | 252k |
| 89 | `faith/theology/biblical-studies/new-testament-in-its-world` | 8 | 473 | 252k |
| 90 | `faith/theology/systematic/if-you-want-to-walk-on-water-you-have-got-to-get-out-of-the` | 8 | 562 | 244k |
| 91 | `craft/engineering/coding-practice/clean-code-principles-and-patterns` | 8 | 525 | 243k |
| 92 | `craft/engineering/agile/97-things-every-project-manager-should-know` | 8 | 384 | 243k |
| 93 | `wisdom/science/cognitive/synthesizing-mind` | 8 | 583 | 242k |
| 94 | `personal/relationships/marriage/secret-of-loving` | 8 | 301 | 241k |
| 95 | `professional/communication/storytelling/33-strategies-of-war` | 8 | 422 | 239k |
| 96 | `personal/habit/discipline/power-of-action` | 8 | 588 | 239k |
| 97 | `craft/engineering/coding-practice/coders-at-work` | 8 | 523 | 238k |
| 98 | `faith/theology/biblical-studies/old-testament-ethics-for-the-people-of-god` | 8 | 448 | 237k |
| 99 | `personal/relationships/community/reading-people` | 8 | 535 | 236k |
| 100 | `professional/communication/persuasion/winning` | 8 | 533 | 234k |
| 101 | `personal/wellness/fitness/4-hour-body` | 8 | 521 | 234k |
| 102 | `faith/theology/systematic/uncommon-ground` | 8 | 706 | 233k |
| 103 | `professional/finance/economics/rich-dads-conspiracy-of-the-rich` | 8 | 499 | 228k |
| 104 | `craft/engineering/coding-practice/designing-web-apis` | 8 | 471 | 228k |
| 105 | `professional/career/job-search/first-break-all-the-rules` | 8 | 575 | 226k |
| 106 | `personal/wellness/mental-health/fengtang-have-capability` | 8 | 503 | 225k |
| 107 | `craft/engineering/coding-practice/pragmatic-programmer` | 8 | 361 | 223k |
| 108 | `faith/spiritual-formation/contemplative/reaching-out` | 8 | 4130 | 221k |
| 109 | `craft/engineering/coding-practice/97-things-every-programmer-should-know` | 8 | 336 | 220k |
| 110 | `wisdom/philosophy/ethics/fallen-leaves` | 8 | 501 | 215k |
| 111 | `professional/business/startup/your-next-five-moves` | 8 | 426 | 215k |
| 112 | `professional/business/management/managing-for-results` | 8 | 587 | 214k |
| 113 | `wisdom/philosophy/ethics/wujun-meta-wisdom` | 8 | 587 | 213k |
| 114 | `professional/career/job-search/decode-and-conquer` | 8 | 1990 | 211k |
| 115 | `professional/finance/investing/essays-of-warren-buffett` | 8 | 526 | 210k |
| 116 | `craft/engineering/systems-design/uml-distilled` | 8 | 2033 | 208k |
| 117 | `faith/theology/biblical-studies/message-of-romans` | 8 | 469 | 206k |
| 118 | `professional/finance/investing/disciplined-trader` | 8 | 593 | 206k |
| 119 | `professional/finance/investing/trend-following-masters-volume-2` | 8 | 598 | 206k |
| 120 | `professional/finance/investing/debunkery` | 8 | 554 | 202k |
| 121 | `personal/mindset/growth/wan-weigang-intellectuals` | 8 | 478 | 202k |
| 122 | `personal/habit/productivity/mindset-for-wealth` | 8 | 495 | 201k |
| 123 | `wisdom/education/self-learning/4-hour-chef` | 8 | 587 | 201k |
| 124 | `personal/relationships/community/different-drum` | 8 | 499 | 200k |
| 125 | `personal/mindset/growth/welcome-to-your-brain` | 8 | 458 | 198k |
| 126 | `craft/engineering/coding-practice/97-things-every-software-architect-should-know` | 8 | 358 | 197k |
| 127 | `craft/writing/non-fiction/wild-at-heart` | 8 | 549 | 197k |
| 128 | `personal/mindset/growth/self-esteem-a-proven-program-of-cognitive-techniques-for` | 8 | 435 | 196k |
| 129 | `faith/theology/biblical-studies/biblical-critical-theory` | 8 | 469 | 195k |
| 130 | `professional/leadership/team-building/8-lessons-in-military-leadership` | 8 | 495 | 195k |
| 131 | `faith/theology/historical/overcoming-sin-and-temptation` | 8 | 415 | 195k |
| 132 | `craft/engineering/coding-practice/exploring-requirements` | 8 | 835 | 195k |
| 133 | `professional/business/marketing/30-day-mba` | 8 | 392 | 193k |
| 134 | `professional/business/sales/extreme-sales` | 8 | 553 | 192k |
| 135 | `professional/communication/persuasion/thats-not-what-i-meant` | 8 | 671 | 192k |
| 136 | `faith/spiritual-formation/prayer/letters-to-malcolm` | 8 | 428 | 188k |
| 137 | `professional/business/startup/knack` | 8 | 549 | 187k |
| 138 | `faith/theology/biblical-studies/message-of-exodus` | 8 | 439 | 186k |
| 139 | `wisdom/history/cultural/sapiens` | 8 | 587 | 186k |
| 140 | `faith/theology/biblical-studies/new-testament-and-the-people-of-god` | 8 | 475 | 185k |
| 141 | `craft/engineering/coding-practice/effective-debugging` | 8 | 499 | 185k |
| 142 | `personal/relationships/community/how-to-say-it` | 8 | 387 | 185k |
| 143 | `personal/habit/productivity/why-elites-are-checklist-masters` | 8 | 539 | 185k |
| 144 | `faith/theology/biblical-studies/ivp-bible-background-commentary-nt` | 8 | 533 | 182k |
| 145 | `faith/theology/systematic/jesus-the-king` | 8 | 284 | 181k |
| 146 | `wisdom/science/cognitive/righteous-mind` | 8 | 511 | 181k |
| 147 | `professional/business/startup/entrepreneur-mind` | 8 | 352 | 181k |
| 148 | `personal/habit/productivity/laws-of-winners` | 8 | 461 | 181k |
| 149 | `professional/finance/investing/first-book-for-retail-investors` | 8 | 408 | 181k |
| 150 | `professional/communication/persuasion/communication-methods` | 8 | 597 | 181k |
| 151 | `craft/engineering/coding-practice/test-driven-development` | 8 | 403 | 180k |
| 152 | `craft/engineering/coding-practice/big-talk-design-patterns` | 8 | 522 | 179k |
| 153 | `professional/finance/investing/complete-guide-to-futures-markets` | 8 | 586 | 179k |
| 154 | `professional/communication/persuasion/laws-of-human-nature` | 8 | 470 | 179k |
| 155 | `professional/business/strategy/hbr-guide-to-thinking-strategically` | 8 | 405 | 177k |
| 156 | `professional/finance/investing/most-important-thing` | 8 | 367 | 177k |
| 157 | `professional/finance/economics/basic-economics` | 8 | 484 | 176k |
| 158 | `craft/engineering/coding-practice/joel-on-software` | 8 | 402 | 175k |
| 159 | `craft/writing/fiction/on-writing-well` | 8 | 520 | 175k |
| 160 | `professional/business/startup/art-of-the-start` | 8 | 310 | 174k |
| 161 | `craft/engineering/agile/planning-extreme-programming` | 8 | 2317 | 174k |
| 162 | `professional/communication/persuasion/16-undeniable-laws-of-communication` | 8 | 479 | 173k |
| 163 | `professional/communication/negotiation/how-to-win-friends-and-influence-people` | 8 | 391 | 173k |
| 164 | `craft/engineering/coding-practice/good-code-bad-code` | 8 | 569 | 173k |
| 165 | `professional/business/startup/innovation-and-entrepreneurship` | 8 | 407 | 172k |
| 166 | `personal/relationships/community/25-ways-to-win-with-people` | 8 | 550 | 171k |
| 167 | `professional/finance/personal-finance/playing-with-fire` | 8 | 552 | 170k |
| 168 | `faith/theology/pastoral/bonhoeffer-spiritual-care-in-a-religionless-age` | 8 | 364 | 170k |
| 169 | `faith/theology/biblical-studies/sherlock-who-2-biblical-world` | 8 | 549 | 169k |
| 170 | `professional/career/skill-building/hbr-guide-to-remote-work` | 8 | 571 | 169k |
| 171 | `professional/career/skill-building/how-to-win-in-a-winner-take-all-world` | 8 | 348 | 168k |
| 172 | `personal/mindset/growth/be-obsessed-or-be-average` | 8 | 524 | 168k |
| 173 | `professional/finance/investing/money-rob-moore` | 8 | 562 | 166k |
| 174 | `craft/engineering/coding-practice/art-of-doing-science-and-engineering` | 8 | 808 | 166k |
| 175 | `professional/finance/economics/mastering-the-market-cycle` | 8 | 446 | 165k |
| 176 | `faith/theology/biblical-studies/sherlock-who-1-bible-suspense` | 8 | 315 | 164k |
| 177 | `personal/mindset/growth/maps-of-meaning` | 8 | 876 | 164k |
| 178 | `wisdom/philosophy/ethics/anatomy-of-human-destructiveness` | 8 | 412 | 164k |
| 179 | `personal/mindset/growth/grey-thinking` | 8 | 561 | 161k |
| 180 | `professional/finance/personal-finance/rich-dad-poor-dad` | 8 | 500 | 160k |
| 181 | `professional/communication/storytelling/story-factor` | 8 | 486 | 156k |
| 182 | `professional/career/problem-solving/strategic-thinking` | 8 | 488 | 155k |
| 183 | `professional/career/skill-building/effective-executive` | 8 | 591 | 155k |
| 184 | `professional/career/skill-building/great-work-great-career` | 8 | 368 | 155k |
| 185 | `personal/mindset/resilience/becoming-bulletproof` | 8 | 521 | 154k |
| 186 | `wisdom/philosophy/ethics/essays-of-francis-bacon` | 8 | 312 | 154k |
| 187 | `faith/theology/systematic/introduction-to-nt-research` | 8 | 447 | 153k |
| 188 | `faith/theology/biblical-studies/message-of-1-peter` | 8 | 439 | 153k |
| 189 | `personal/mindset/growth/bottom-logic` | 8 | 450 | 151k |
| 190 | `personal/mindset/resilience/rules-of-thinking` | 8 | 554 | 149k |
| 191 | `professional/communication/persuasion/how-to-use-power-phrases` | 8 | 553 | 148k |
| 192 | `professional/finance/investing/im-worth-more` | 8 | 541 | 148k |
| 193 | `professional/business/strategy/hbr-guide-to-managing-strategic-initiatives` | 8 | 564 | 148k |
| 194 | `professional/finance/economics/how-economy-grows` | 8 | 391 | 147k |
| 195 | `professional/communication/persuasion/ask-more-the-power-of-questions` | 8 | 538 | 146k |
| 196 | `professional/business/startup/built-to-sell` | 8 | 2125 | 144k |
| 197 | `professional/leadership/team-building/boundaries-for-leaders` | 8 | 587 | 144k |
| 198 | `personal/mindset/growth/rich-brother-rich-sister` | 8 | 561 | 143k |
| 199 | `personal/habit/productivity/world-only-readers-can-reach` | 8 | 752 | 143k |
| 200 | `faith/theology/biblical-studies/message-of-hosea` | 8 | 546 | 143k |

_（只列前 200 本，另有 1402 本未列出）_

## 已完成

- `craft/engineering/coding-practice/software-developers-career-guide` — 2632 字
- `craft/engineering/coding-practice/wujun-math-general-course` — 3433 字
- `faith/theology/biblical-studies/sherlock-who-3-new-light` — 2314 字
- `faith/theology/systematic/allure-of-gentleness` — 2790 字
- `personal/mindset/growth/tuesdays-with-morrie` — 2847 字
- `personal/mindset/self-awareness/scout-mindset` — 2833 字
- `personal/relationships/dating/home-tonight` — 2588 字
- `personal/relationships/dating/how-to-make-anyone-fall-in-love-with-you` — 2453 字
- `professional/career/skill-building/so-good-they-cant-ignore-you` — 2778 字
- `professional/leadership/team-building/hbr-guide-to-leading-through-change` — 2592 字
- `professional/leadership/vision/innovators-prescription` — 3668 字
- `professional/leadership/vision/its-your-ship` — 2649 字
- `wisdom/education/pedagogy/why-dont-students-like-school` — 2958 字
- `wisdom/philosophy/eastern/how-the-world-thinks` — 2529 字
- `wisdom/philosophy/ethics/12-rules-for-life` — 2628 字
- `wisdom/philosophy/ethics/intuition-pumps` — 3021 字
