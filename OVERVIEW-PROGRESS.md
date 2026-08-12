# 深度概覽改寫進度

> 衍生檔，勿手改。重新產生：`python3 scripts/audit-overview.py --report`

最後更新：2026-08-13

## 總覽

| | 數量 | 佔比 |
|---|---:|---:|
| 全庫 | 1618 | 100% |
| **已改寫並通過品檢** | **20** | **1.2%** |
| 仍是舊三段格式 | 1598 | 98.8% |

```
[··················································] 1.2%
```

## 各項未通過

| 檢查 | 未通過 | 佔比 |
|---|---:|---:|
| 四段齊全 | 1598 | 99% |
| 作者的位置 長度 | 1598 | 99% |
| 定位 長度 | 1598 | 99% |
| 這本書的限制 長度 | 1598 | 99% |
| 限制段有實質內容 | 1598 | 99% |
| 引用年份 | 1425 | 88% |
| 點名可查證的作品／人名 | 1061 | 66% |
| 完整摘要 長度 | 541 | 33% |
| 無空洞讚美 | 51 | 3% |

## 待改寫（依未通過項數 → 筆記量排序）

筆記量是排序依據：筆記厚而概覽薄，代表素材就在那裡沒被用上，重做的投報率最高。

| # | 書 | 未通過 | 概覽字數 | 筆記 |
|---:|---|---:|---:|---:|
| 1 | `wisdom/philosophy/ethics/reading-as-a-wilderness` | 9 | 322 | 16k |
| 2 | `professional/communication/persuasion/mba-confidential` | 9 | 414 | 11k |
| 3 | `faith/theology/apologetics/shadowlands` | 9 | 519 | 11k |
| 4 | `personal/relationships/parenting/financial-boundaries-with-family` | 9 | 409 | 1k |
| 5 | `personal/relationships/community/life-is-a-long-term-accumulation` | 9 | 590 | 1k |
| 6 | `wisdom/science/pharmacology/pharmacotherapy-principles-and-practice` | 8 | 538 | 1247k |
| 7 | `wisdom/science/pharmacology/basic-clinical-pharmacology` | 8 | 555 | 900k |
| 8 | `wisdom/science/pharmacology/applied-therapeutics` | 8 | 518 | 832k |
| 9 | `craft/engineering/systems-design/enterprise-integration-patterns` | 8 | 1783 | 813k |
| 10 | `craft/engineering/systems-design/system-design-interview` | 8 | 431 | 784k |
| 11 | `wisdom/science/evolution/origin-of-species` | 8 | 1454 | 778k |
| 12 | `craft/engineering/devops/kubernetes-in-action` | 8 | 3971 | 683k |
| 13 | `faith/theology/apologetics/god-in-the-dock` | 8 | 2692 | 637k |
| 14 | `professional/finance/investing/dynamic-hedging` | 8 | 4167 | 601k |
| 15 | `faith/theology/systematic/systematic-theology` | 8 | 355 | 578k |
| 16 | `faith/theology/biblical-studies/message-of-psalms-73-150` | 8 | 581 | 575k |
| 17 | `professional/finance/real-estate/real-book-of-real-estate` | 8 | 552 | 563k |
| 18 | `personal/relationships/community/emily-posts-etiquette-19th-edition` | 8 | 516 | 532k |
| 19 | `faith/theology/biblical-studies/prophets-heschel` | 8 | 538 | 509k |
| 20 | `professional/finance/economics/wealth-of-nations` | 8 | 3910 | 508k |
| 21 | `professional/communication/persuasion/48-laws-of-power` | 8 | 431 | 504k |
| 22 | `professional/business/management/high-growth-handbook` | 8 | 1930 | 503k |
| 23 | `wisdom/science/statistics/statistical-consequences-of-fat-tails` | 8 | 4535 | 489k |
| 24 | `wisdom/education/self-learning/teaching-technique` | 8 | 415 | 481k |
| 25 | `faith/theology/systematic/moses-in-the-clinic` | 8 | 595 | 471k |
| 26 | `professional/finance/economics/general-theory-of-employment-interest-and-money` | 8 | 4277 | 448k |
| 27 | `professional/career/job-search/what-color-is-your-parachute` | 8 | 1168 | 447k |
| 28 | `faith/theology/biblical-studies/message-of-mark` | 8 | 567 | 417k |
| 29 | `professional/finance/personal-finance/retire-young-retire-rich` | 8 | 583 | 411k |
| 30 | `faith/spiritual-formation/discipleship/soul-of-shame` | 8 | 2586 | 406k |
| 31 | `faith/theology/biblical-studies/gods-wisdom-for-navigating-life` | 8 | 338 | 402k |
| 32 | `faith/theology/pastoral/story-of-christian-theology` | 8 | 536 | 393k |
| 33 | `faith/theology/biblical-studies/message-of-ezekiel` | 8 | 602 | 390k |
| 34 | `professional/finance/personal-finance/fake-money-fake-teachers-fake-assets` | 8 | 550 | 383k |
| 35 | `faith/spiritual-formation/discipleship/life-without-lack` | 8 | 2011 | 382k |
| 36 | `faith/theology/systematic/we-who-wrestle-with-god` | 8 | 599 | 367k |
| 37 | `faith/theology/systematic/along-with-moses` | 8 | 560 | 366k |
| 38 | `professional/leadership/team-building/extreme-ownership` | 8 | 2006 | 365k |
| 39 | `personal/lifestyle/style/how-to-be-a-man` | 8 | 408 | 361k |
| 40 | `faith/theology/biblical-studies/dictionary-of-the-later-new-testament` | 8 | 375 | 360k |
| 41 | `personal/mindset/growth/change-your-thinking-change-your-life-tracy` | 8 | 4109 | 359k |
| 42 | `personal/relationships/marriage/boundaries-in-marriage` | 8 | 1896 | 357k |
| 43 | `professional/finance/investing/50-questions-retail-investors` | 8 | 487 | 348k |
| 44 | `faith/theology/biblical-studies/message-of-samuel` | 8 | 519 | 347k |
| 45 | `professional/leadership/decision-making/breaking-roberts-rules` | 8 | 504 | 341k |
| 46 | `professional/career/job-search/cracking-the-tech-career` | 8 | 486 | 339k |
| 47 | `professional/career/skill-building/myself-and-other-more-important-matters` | 8 | 375 | 336k |
| 48 | `professional/career/side-hustle/100-startup` | 8 | 1803 | 335k |
| 49 | `professional/leadership/vision/managers-path` | 8 | 387 | 335k |
| 50 | `faith/theology/pastoral/when-people-are-big-and-god-is-small` | 8 | 2078 | 333k |
| 51 | `faith/theology/biblical-studies/according-to-plan` | 8 | 523 | 323k |
| 52 | `professional/communication/persuasion/5-min-mba-personal` | 8 | 476 | 320k |
| 53 | `professional/leadership/culture/dare-to-lead` | 8 | 5757 | 319k |
| 54 | `faith/theology/biblical-studies/mission-of-god` | 8 | 538 | 312k |
| 55 | `faith/spiritual-formation/discipleship/attached-to-god` | 8 | 1660 | 308k |
| 56 | `faith/theology/biblical-studies/message-of-genesis-bst` | 8 | 575 | 290k |
| 57 | `professional/finance/economics/economics-in-one-lesson` | 8 | 3864 | 290k |
| 58 | `professional/business/management/management-challenges-for-21st-century` | 8 | 1758 | 290k |
| 59 | `faith/theology/biblical-studies/message-of-job` | 8 | 577 | 289k |
| 60 | `craft/engineering/coding-practice/smalltalk-best-practice-patterns` | 8 | 2137 | 288k |
| 61 | `wisdom/philosophy/ethics/revolution-of-hope` | 8 | 1773 | 287k |
| 62 | `personal/relationships/community/wujun-attitude` | 8 | 393 | 286k |
| 63 | `craft/tools/coffee/uncommon-grounds` | 8 | 590 | 282k |
| 64 | `wisdom/philosophy/ethics/wujun-realm` | 8 | 481 | 277k |
| 65 | `faith/theology/systematic/hearing-god` | 8 | 551 | 276k |
| 66 | `professional/leadership/team-building/leading-change` | 8 | 3635 | 274k |
| 67 | `professional/career/job-search/60-seconds-and-youre-hired` | 8 | 1952 | 269k |
| 68 | `professional/communication/storytelling/resonate-visual-stories-transform-audiences` | 8 | 575 | 267k |
| 69 | `faith/theology/systematic/twentieth-century-theologians` | 8 | 775 | 264k |
| 70 | `faith/theology/biblical-studies/message-of-joshua` | 8 | 595 | 264k |
| 71 | `personal/relationships/parenting/rules-of-life` | 8 | 449 | 263k |
| 72 | `faith/theology/biblical-studies/old-testament-theology-waltke` | 8 | 593 | 263k |
| 73 | `professional/leadership/vision/360-degree-leader` | 8 | 472 | 263k |
| 74 | `faith/theology/biblical-studies/message-of-1-2-thessalonians` | 8 | 553 | 260k |
| 75 | `professional/finance/personal-finance/cashflow-quadrant` | 8 | 541 | 260k |
| 76 | `wisdom/history/civilization/wujun-context` | 8 | 404 | 252k |
| 77 | `faith/theology/biblical-studies/new-testament-in-its-world` | 8 | 473 | 252k |
| 78 | `faith/theology/systematic/if-you-want-to-walk-on-water-you-have-got-to-get-out-of-the` | 8 | 562 | 244k |
| 79 | `craft/engineering/coding-practice/clean-code-principles-and-patterns` | 8 | 525 | 243k |
| 80 | `craft/engineering/agile/97-things-every-project-manager-should-know` | 8 | 384 | 243k |
| 81 | `wisdom/science/cognitive/synthesizing-mind` | 8 | 583 | 242k |
| 82 | `personal/relationships/marriage/secret-of-loving` | 8 | 301 | 241k |
| 83 | `professional/communication/storytelling/33-strategies-of-war` | 8 | 422 | 239k |
| 84 | `personal/habit/discipline/power-of-action` | 8 | 588 | 239k |
| 85 | `craft/engineering/coding-practice/coders-at-work` | 8 | 523 | 238k |
| 86 | `faith/theology/biblical-studies/old-testament-ethics-for-the-people-of-god` | 8 | 448 | 237k |
| 87 | `personal/relationships/community/reading-people` | 8 | 535 | 236k |
| 88 | `professional/communication/persuasion/winning` | 8 | 533 | 234k |
| 89 | `personal/wellness/fitness/4-hour-body` | 8 | 521 | 234k |
| 90 | `faith/theology/systematic/uncommon-ground` | 8 | 706 | 233k |
| 91 | `professional/finance/economics/rich-dads-conspiracy-of-the-rich` | 8 | 499 | 228k |
| 92 | `craft/engineering/coding-practice/designing-web-apis` | 8 | 471 | 228k |
| 93 | `professional/career/job-search/first-break-all-the-rules` | 8 | 575 | 226k |
| 94 | `personal/wellness/mental-health/fengtang-have-capability` | 8 | 503 | 225k |
| 95 | `craft/engineering/coding-practice/pragmatic-programmer` | 8 | 361 | 223k |
| 96 | `faith/spiritual-formation/contemplative/reaching-out` | 8 | 4130 | 221k |
| 97 | `craft/engineering/coding-practice/97-things-every-programmer-should-know` | 8 | 336 | 220k |
| 98 | `wisdom/philosophy/ethics/fallen-leaves` | 8 | 501 | 215k |
| 99 | `professional/business/startup/your-next-five-moves` | 8 | 426 | 215k |
| 100 | `professional/business/management/managing-for-results` | 8 | 587 | 214k |
| 101 | `wisdom/philosophy/ethics/wujun-meta-wisdom` | 8 | 587 | 213k |
| 102 | `professional/career/job-search/decode-and-conquer` | 8 | 1990 | 211k |
| 103 | `professional/finance/investing/essays-of-warren-buffett` | 8 | 526 | 210k |
| 104 | `craft/engineering/systems-design/uml-distilled` | 8 | 2033 | 208k |
| 105 | `faith/theology/biblical-studies/message-of-romans` | 8 | 469 | 206k |
| 106 | `professional/finance/investing/disciplined-trader` | 8 | 593 | 206k |
| 107 | `professional/finance/investing/trend-following-masters-volume-2` | 8 | 598 | 206k |
| 108 | `professional/finance/investing/debunkery` | 8 | 554 | 202k |
| 109 | `personal/mindset/growth/wan-weigang-intellectuals` | 8 | 478 | 202k |
| 110 | `personal/habit/productivity/mindset-for-wealth` | 8 | 495 | 201k |
| 111 | `wisdom/education/self-learning/4-hour-chef` | 8 | 587 | 201k |
| 112 | `personal/relationships/community/different-drum` | 8 | 499 | 200k |
| 113 | `personal/mindset/growth/welcome-to-your-brain` | 8 | 458 | 198k |
| 114 | `craft/engineering/coding-practice/97-things-every-software-architect-should-know` | 8 | 358 | 197k |
| 115 | `personal/mindset/growth/self-esteem-a-proven-program-of-cognitive-techniques-for` | 8 | 435 | 196k |
| 116 | `faith/theology/biblical-studies/biblical-critical-theory` | 8 | 469 | 195k |
| 117 | `professional/leadership/team-building/8-lessons-in-military-leadership` | 8 | 495 | 195k |
| 118 | `craft/engineering/coding-practice/exploring-requirements` | 8 | 835 | 195k |
| 119 | `professional/business/marketing/30-day-mba` | 8 | 392 | 193k |
| 120 | `professional/business/sales/extreme-sales` | 8 | 553 | 192k |
| 121 | `professional/communication/persuasion/thats-not-what-i-meant` | 8 | 671 | 192k |
| 122 | `faith/spiritual-formation/prayer/letters-to-malcolm` | 8 | 428 | 188k |
| 123 | `professional/business/startup/knack` | 8 | 549 | 187k |
| 124 | `faith/theology/biblical-studies/message-of-exodus` | 8 | 439 | 186k |
| 125 | `wisdom/history/cultural/sapiens` | 8 | 587 | 186k |
| 126 | `faith/theology/biblical-studies/new-testament-and-the-people-of-god` | 8 | 475 | 185k |
| 127 | `craft/engineering/coding-practice/effective-debugging` | 8 | 499 | 185k |
| 128 | `personal/relationships/community/how-to-say-it` | 8 | 387 | 185k |
| 129 | `personal/habit/productivity/why-elites-are-checklist-masters` | 8 | 539 | 185k |
| 130 | `faith/theology/biblical-studies/ivp-bible-background-commentary-nt` | 8 | 533 | 182k |
| 131 | `faith/theology/systematic/jesus-the-king` | 8 | 284 | 181k |
| 132 | `wisdom/science/cognitive/righteous-mind` | 8 | 511 | 181k |
| 133 | `professional/business/startup/entrepreneur-mind` | 8 | 352 | 181k |
| 134 | `personal/habit/productivity/laws-of-winners` | 8 | 461 | 181k |
| 135 | `professional/finance/investing/first-book-for-retail-investors` | 8 | 408 | 181k |
| 136 | `professional/communication/persuasion/communication-methods` | 8 | 597 | 181k |
| 137 | `craft/engineering/coding-practice/test-driven-development` | 8 | 403 | 180k |
| 138 | `craft/engineering/coding-practice/big-talk-design-patterns` | 8 | 522 | 179k |
| 139 | `professional/finance/investing/complete-guide-to-futures-markets` | 8 | 586 | 179k |
| 140 | `professional/communication/persuasion/laws-of-human-nature` | 8 | 470 | 179k |
| 141 | `professional/business/strategy/hbr-guide-to-thinking-strategically` | 8 | 405 | 177k |
| 142 | `professional/finance/investing/most-important-thing` | 8 | 367 | 177k |
| 143 | `craft/engineering/coding-practice/joel-on-software` | 8 | 402 | 175k |
| 144 | `craft/writing/fiction/on-writing-well` | 8 | 520 | 175k |
| 145 | `professional/business/startup/art-of-the-start` | 8 | 310 | 174k |
| 146 | `craft/engineering/agile/planning-extreme-programming` | 8 | 2317 | 174k |
| 147 | `professional/communication/persuasion/16-undeniable-laws-of-communication` | 8 | 479 | 173k |
| 148 | `professional/communication/negotiation/how-to-win-friends-and-influence-people` | 8 | 391 | 173k |
| 149 | `professional/business/startup/innovation-and-entrepreneurship` | 8 | 407 | 172k |
| 150 | `personal/relationships/community/25-ways-to-win-with-people` | 8 | 550 | 171k |
| 151 | `professional/finance/personal-finance/playing-with-fire` | 8 | 552 | 170k |
| 152 | `faith/theology/pastoral/bonhoeffer-spiritual-care-in-a-religionless-age` | 8 | 364 | 170k |
| 153 | `faith/theology/biblical-studies/sherlock-who-2-biblical-world` | 8 | 549 | 169k |
| 154 | `professional/career/skill-building/hbr-guide-to-remote-work` | 8 | 571 | 169k |
| 155 | `professional/career/skill-building/how-to-win-in-a-winner-take-all-world` | 8 | 348 | 168k |
| 156 | `personal/mindset/growth/be-obsessed-or-be-average` | 8 | 524 | 168k |
| 157 | `professional/finance/investing/money-rob-moore` | 8 | 562 | 166k |
| 158 | `craft/engineering/coding-practice/art-of-doing-science-and-engineering` | 8 | 808 | 166k |
| 159 | `professional/finance/economics/mastering-the-market-cycle` | 8 | 446 | 165k |
| 160 | `faith/theology/biblical-studies/sherlock-who-1-bible-suspense` | 8 | 315 | 164k |
| 161 | `personal/mindset/growth/maps-of-meaning` | 8 | 876 | 164k |
| 162 | `wisdom/philosophy/ethics/anatomy-of-human-destructiveness` | 8 | 412 | 164k |
| 163 | `personal/mindset/growth/grey-thinking` | 8 | 561 | 161k |
| 164 | `professional/finance/personal-finance/rich-dad-poor-dad` | 8 | 500 | 160k |
| 165 | `professional/communication/storytelling/story-factor` | 8 | 486 | 156k |
| 166 | `professional/career/problem-solving/strategic-thinking` | 8 | 488 | 155k |
| 167 | `professional/career/skill-building/effective-executive` | 8 | 591 | 155k |
| 168 | `professional/career/skill-building/great-work-great-career` | 8 | 368 | 155k |
| 169 | `personal/mindset/resilience/becoming-bulletproof` | 8 | 521 | 154k |
| 170 | `wisdom/philosophy/ethics/essays-of-francis-bacon` | 8 | 312 | 154k |
| 171 | `faith/theology/systematic/introduction-to-nt-research` | 8 | 447 | 153k |
| 172 | `faith/theology/biblical-studies/message-of-1-peter` | 8 | 439 | 153k |
| 173 | `personal/mindset/growth/bottom-logic` | 8 | 450 | 151k |
| 174 | `personal/mindset/resilience/rules-of-thinking` | 8 | 554 | 149k |
| 175 | `professional/communication/persuasion/how-to-use-power-phrases` | 8 | 553 | 148k |
| 176 | `professional/finance/investing/im-worth-more` | 8 | 541 | 148k |
| 177 | `professional/business/strategy/hbr-guide-to-managing-strategic-initiatives` | 8 | 564 | 148k |
| 178 | `professional/finance/economics/how-economy-grows` | 8 | 391 | 147k |
| 179 | `professional/communication/persuasion/ask-more-the-power-of-questions` | 8 | 538 | 146k |
| 180 | `professional/business/startup/built-to-sell` | 8 | 2125 | 144k |
| 181 | `professional/leadership/team-building/boundaries-for-leaders` | 8 | 587 | 144k |
| 182 | `personal/mindset/growth/rich-brother-rich-sister` | 8 | 561 | 143k |
| 183 | `personal/habit/productivity/world-only-readers-can-reach` | 8 | 752 | 143k |
| 184 | `faith/theology/biblical-studies/message-of-hosea` | 8 | 546 | 143k |
| 185 | `professional/finance/investing/market-wizards` | 8 | 502 | 140k |
| 186 | `professional/communication/persuasion/louder-than-words` | 8 | 597 | 140k |
| 187 | `professional/business/startup/hbr-on-entrepreneurship` | 8 | 404 | 139k |
| 188 | `personal/relationships/marriage/course-of-love` | 8 | 754 | 139k |
| 189 | `faith/theology/biblical-studies/ancient-near-eastern-thought` | 8 | 574 | 138k |
| 190 | `craft/tools/coffee/world-atlas-of-coffee` | 8 | 573 | 138k |
| 191 | `faith/theology/biblical-studies/message-of-2-corinthians` | 8 | 501 | 138k |
| 192 | `craft/writing/non-fiction/sense-of-style` | 8 | 507 | 134k |
| 193 | `wisdom/science/cognitive/beast-gentleman` | 8 | 542 | 133k |
| 194 | `wisdom/history/modern/escape-from-freedom` | 8 | 453 | 133k |
| 195 | `wisdom/education/self-learning/learning-how-to-learn` | 8 | 546 | 131k |
| 196 | `craft/engineering/coding-practice/big-refactoring` | 8 | 534 | 131k |
| 197 | `professional/career/skill-building/do-over` | 8 | 345 | 130k |
| 198 | `professional/leadership/team-building/being-the-boss` | 8 | 580 | 129k |
| 199 | `professional/communication/negotiation/rules-of-people` | 8 | 582 | 129k |
| 200 | `wisdom/philosophy/ethics/art-of-asking-life-questions` | 8 | 553 | 129k |

_（只列前 200 本，另有 1398 本未列出）_

## 已完成

- `craft/engineering/coding-practice/are-your-lights-on` — 2654 字
- `craft/engineering/coding-practice/software-developers-career-guide` — 2632 字
- `craft/engineering/coding-practice/wujun-math-general-course` — 3433 字
- `faith/theology/biblical-studies/sherlock-who-3-new-light` — 2314 字
- `faith/theology/systematic/allure-of-gentleness` — 2790 字
- `personal/mindset/growth/29-pawn-tickets-2` — 2494 字
- `personal/mindset/growth/tuesdays-with-morrie` — 2847 字
- `personal/mindset/self-awareness/scout-mindset` — 2833 字
- `personal/relationships/dating/home-tonight` — 2588 字
- `personal/relationships/dating/how-to-make-anyone-fall-in-love-with-you` — 2453 字
- `professional/career/skill-building/so-good-they-cant-ignore-you` — 2778 字
- `professional/communication/public-speaking/slideology` — 2678 字
- `professional/leadership/team-building/hbr-guide-to-leading-through-change` — 2592 字
- `professional/leadership/vision/innovators-prescription` — 3668 字
- `professional/leadership/vision/its-your-ship` — 2649 字
- `wisdom/education/pedagogy/why-dont-students-like-school` — 2958 字
- `wisdom/education/self-learning/online-teaching-technique` — 2477 字
- `wisdom/philosophy/eastern/how-the-world-thinks` — 2529 字
- `wisdom/philosophy/ethics/12-rules-for-life` — 2628 字
- `wisdom/philosophy/ethics/intuition-pumps` — 3021 字
