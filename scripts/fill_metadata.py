#!/usr/bin/env python3
"""Fill in book metadata for books-queue.yaml"""

# Existing repos on GitHub (Andrewnplus account)
EXISTING_REPOS = {
    "21-lessons-for-the-21st-century",
    "atomic-habits",
    "deep-work", "deepwork",
    "the-speed-of-trust",
    "the-richest-man-in-babylon",
    "friend-and-foe",
    "no-one-understands-you-and-what-to-do-about-it",
    "the-power-of-eye-contact",
    "living-loving-and-learning",
    "reinforcements-how-to-get-people-to-help-you",
    "thats-not-what-i-meant",
    "what-every-body-is-saying",
    "are-your-lights-on",
    "hard-thing-about-hard-things", "the-hard-thing-about-hard-things",
    "qbq-question-behind-question",
    "the-7-habits-of-highly-effective-people",
    "every-good-endeavor",
    "how-to-talk-to-anyone",
    "the-course-of-love",
    "extraordinary-relationship",
    "extraordinary-relationship-2",
    "harvard-business-review-on-breakthrough-thinking",
    "strategic-thinking",
    "decisive-thinking-weapon",
    "the-golden-rules",
    "failing-forward",
    "prayer-experiencing-awe-and-intimacy-with-god",
    "the-dignity-of-speaking",
    "love-anthropology",
    "love-is-not-fairytale",
    "lessons-in-love",
    "born-liars",
    "thinking-fast-and-slow",
    "how-to-win-friends-and-influence-people",
    "influence-the-psychology-of-persuasion",
    "give-and-take",
    "mans-search-for-meaning",
    "zero-to-one",
    "the-lean-startup",
    "start-with-why",
    "good-to-great",
    "you-just-dont-understand",
    "systematic-theology",
    "way-of-the-heart",
    "home-tonight",
    "our-greatest-gift",
    "selfless-way-of-christ",
    "eat-this-book",
    "the-call",
    "if-you-want-to-write",
    "how-to-read-the-bible-for-all-its-worth",
    "reading-like-a-writer",
    "the-power-of-reading",
    "everyone-communicates-few-connect",
    "kiss-your-but-good-bye",
    "wealthy-strong-heart",
    "attitude-of-the-rich",
}

# Book metadata: chinese_title -> (english_title, author, publication_date, id_override)
# id will be generated from english_title in kebab-case unless id_override is set
BOOKS = {
    # === 基督信仰 ===
    "上帝值夜班": {
        "english_title": "God on the Night Shift",
        "author": "Ron Mehl",
        "publication_date": "1999",
    },
    "上帝的混沌理論": {
        "english_title": "Chaos Theory of God",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "世界在等待的門徒": {
        "english_title": "The Radical Disciple",
        "author": "John Stott",
        "publication_date": "October 2010",
    },
    "全民神學家巴刻：從認識神到事奉神": {
        "english_title": "J I Packer: From Knowing God to Serving God",
        "author": "陳培德",
        "publication_date": "",
        "note": "Chinese original about J.I. Packer",
    },
    "工人之路": {
        "english_title": "The Way of a Worker",
        "author": "陳終道",
        "publication_date": "",
        "note": "Chinese original",
    },
    "幽谷之旅": {
        "english_title": "Turn My Mourning into Dancing",
        "author": "Henri Nouwen",
        "publication_date": "2001",
    },
    "暴力世界中的溫柔": {
        "english_title": "Peacework",
        "author": "Henri Nouwen",
        "publication_date": "2005",
    },
    "有異象的人": {
        "english_title": "The Man with a Vision",
        "author": "陶乃是",
        "publication_date": "",
        "note": "Chinese original",
    },
    "渴慕神": {
        "english_title": "The Pursuit of God",
        "author": "A.W. Tozer",
        "publication_date": "1948",
    },
    "禱告": {
        "english_title": "Prayer: Experiencing Awe and Intimacy with God",
        "author": "Timothy Keller",
        "publication_date": "November 2014",
        "id": "prayer-keller",
    },
    "約櫃流浪記": {
        "english_title": "The Ark of the Covenant Wandering",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "聖潔讓你想得不一樣": {
        "english_title": "The Hole in Our Holiness",
        "author": "Kevin DeYoung",
        "publication_date": "September 2012",
    },
    "行在水面上": {
        "english_title": "If You Want to Walk on Water You've Got to Get Out of the Boat",
        "author": "John Ortberg",
        "publication_date": "2001",
        "id": "walk-on-water",
    },
    "門徒好豐盛": {
        "english_title": "The Abundant Disciple",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    # === 基督信仰 (愛情再思 subfolder originally) ===
    "一個人的聖殿": {
        "english_title": "A Temple for One",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original - Christian singleness",
    },
    "一路愛到底": {
        "english_title": "Love All the Way",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "工作真重要": {
        "english_title": "Every Good Endeavor",
        "author": "Timothy Keller",
        "publication_date": "November 2012",
    },
    "成熟之愛": {
        "english_title": "Lessons in Love",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original - Christian love",
        "id": "lessons-in-love-liu",
    },
    "為約會立界線": {
        "english_title": "Boundaries in Dating",
        "author": "Henry Cloud, John Townsend",
        "publication_date": "2000",
    },
    "男人是蚌女人是鐵撬": {
        "english_title": "Men Are Clams Women Are Crowbars",
        "author": "David Clarke",
        "publication_date": "2002",
    },
    "神啊！說好的那個人呢？": {
        "english_title": "God Where Is the One You Promised",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    # === 基督信仰 (社會哲思 subfolder) ===
    "回到正統": {
        "english_title": "Orthodoxy",
        "author": "G.K. Chesterton",
        "publication_date": "1908",
    },
    "安息日的真諦": {
        "english_title": "The Sabbath",
        "author": "Abraham Joshua Heschel",
        "publication_date": "1951",
    },
    "福音作為悲劇、喜劇和童話": {
        "english_title": "Telling the Truth: The Gospel as Tragedy Comedy and Fairy Tale",
        "author": "Frederick Buechner",
        "publication_date": "1977",
    },
    "認識上帝與認識人的9個探險": {
        "english_title": "Nine Adventures in Knowing God and Knowing People",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "邪惡與上帝新世界 ": {
        "english_title": "Evil and the Justice of God",
        "author": "N.T. Wright",
        "publication_date": "2006",
    },

    # === 家庭教養 ===
    "我是好爸爸": {
        "english_title": "I Am a Good Dad",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "教出孩子的生存力": {
        "english_title": "Raising Kids with Survival Skills",
        "author": "大前研一",
        "publication_date": "",
        "note": "Japanese original by Kenichi Ohmae",
    },

    # === 專案管理 ===
    "Peopleware": {
        "english_title": "Peopleware: Productive Projects and Teams",
        "author": "Tom DeMarco, Timothy Lister",
        "publication_date": "1987",
        "id": "peopleware",
    },
    "Sprint衝刺計畫": {
        "english_title": "Sprint: How to Solve Big Problems and Test New Ideas in Just Five Days",
        "author": "Jake Knapp, John Zeratsky, Braden Kowitz",
        "publication_date": "March 2016",
        "id": "sprint",
    },
    "Team Geek": {
        "english_title": "Team Geek: A Software Developer's Guide to Working Well with Others",
        "author": "Brian W. Fitzpatrick, Ben Collins-Sussman",
        "publication_date": "July 2012",
        "id": "team-geek",
    },
    "顧問成功的祕密": {
        "english_title": "The Secrets of Consulting",
        "author": "Gerald M. Weinberg",
        "publication_date": "1985",
    },

    # === 心理建設 ===
    "一流的人如何保持顛峰": {
        "english_title": "Peak Performance",
        "author": "Brad Stulberg, Steve Magness",
        "publication_date": "June 2017",
    },
    "不讓自己成為箭靶": {
        "english_title": "Dont Let Others Get on Your Nerves",
        "author": "Albert Ellis",
        "publication_date": "",
        "id": "dont-be-a-target",
        "note": "Possibly How to Keep People from Pushing Your Buttons",
    },
    "你的不安，是因為太習慣受傷害": {
        "english_title": "Your Anxiety Is Because You Are Too Used to Being Hurt",
        "author": "根本裕幸",
        "publication_date": "",
        "note": "Japanese original",
        "id": "your-anxiety-from-being-hurt",
    },
    "哈佛教你打造健康人生": {
        "english_title": "The Good Life",
        "author": "Robert Waldinger, Marc Schulz",
        "publication_date": "January 2023",
    },
    "心態致勝": {
        "english_title": "Mindset: The New Psychology of Success",
        "author": "Carol S. Dweck",
        "publication_date": "February 2006",
        "id": "mindset",
    },
    "心靈療癒自助手冊": {
        "english_title": "Self-Therapy",
        "author": "Jay Earley",
        "publication_date": "2009",
    },
    "思考的藝術": {
        "english_title": "The Art of Thinking Clearly",
        "author": "Rolf Dobelli",
        "publication_date": "2011",
    },
    "怪咖心理學": {
        "english_title": "Quirkology",
        "author": "Richard Wiseman",
        "publication_date": "2007",
    },
    "情緒勒索": {
        "english_title": "Emotional Blackmail",
        "author": "Susan Forward",
        "publication_date": "1997",
    },
    "探索人格潛能，看見更真實的自己": {
        "english_title": "Me Myself and Us",
        "author": "Brian R. Little",
        "publication_date": "2014",
    },
    "最珍貴的教訓": {
        "english_title": "The Most Precious Lessons",
        "author": "劉墉",
        "publication_date": "",
        "note": "Chinese original by Liu Yong",
    },
    "柯維經典語錄": {
        "english_title": "The Stephen Covey Quotes Collection",
        "author": "Stephen R. Covey",
        "publication_date": "",
        "id": "covey-quotes",
    },
    "比賽，從心開始": {
        "english_title": "The Inner Game of Tennis",
        "author": "W. Timothy Gallwey",
        "publication_date": "1974",
    },
    "潛意識正在控制你的行為": {
        "english_title": "Subliminal: How Your Unconscious Mind Rules Your Behavior",
        "author": "Leonard Mlodinow",
        "publication_date": "April 2012",
        "id": "subliminal",
    },
    "煩惱都是自己想出來的": {
        "english_title": "All Worries Are Self-Created",
        "author": "松原正樹",
        "publication_date": "",
        "note": "Japanese original",
    },
    "真心接納自己，培養健全的自尊心": {
        "english_title": "Self-Esteem",
        "author": "Matthew McKay, Patrick Fanning",
        "publication_date": "1987",
        "id": "self-esteem",
    },
    "與自己對話": {
        "english_title": "Chatter: The Voice in Our Head",
        "author": "Ethan Kross",
        "publication_date": "January 2021",
        "id": "chatter",
    },
    "行為的藝術": {
        "english_title": "The Art of Thinking Clearly Part 2",
        "author": "Rolf Dobelli",
        "publication_date": "2012",
        "id": "the-art-of-action",
    },
    "被討厭的勇氣 二": {
        "english_title": "The Courage to Be Happy",
        "author": "Ichiro Kishimi, Fumitake Koga",
        "publication_date": "2016",
    },
    "被討厭的勇氣": {
        "english_title": "The Courage to Be Disliked",
        "author": "Ichiro Kishimi, Fumitake Koga",
        "publication_date": "2013",
    },
    "諮商與心理治療": {
        "english_title": "Theory and Practice of Counseling and Psychotherapy",
        "author": "Gerald Corey",
        "publication_date": "",
        "id": "counseling-and-psychotherapy",
    },
    "阿德勒心理學講義": {
        "english_title": "What Life Could Mean to You",
        "author": "Alfred Adler",
        "publication_date": "1931",
    },
    "陪你飛一程": {
        "english_title": "Accompany You for a While",
        "author": "吳若權",
        "publication_date": "",
        "note": "Chinese original",
    },

    # === 愛情再思 ===
    "愛情非童話": {
        "english_title": "Love Is Not a Fairytale",
        "author": "劉曉亭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "愛的進化論": {
        "english_title": "The Course of Love",
        "author": "Alain de Botton",
        "publication_date": "June 2016",
    },
    "我談的那場戀愛": {
        "english_title": "Essays in Love",
        "author": "Alain de Botton",
        "publication_date": "1993",
    },
    "男女親密對話": {
        "english_title": "Men and Women in Intimate Dialogue",
        "author": "黃維仁",
        "publication_date": "",
        "note": "Chinese original",
    },
    "精準撩動人心的戀愛人類學": {
        "english_title": "Love Anthropology",
        "author": "Leil Lowndes",
        "publication_date": "",
        "note": "Possibly How to Make Anyone Fall in Love with You",
        "id": "love-anthropology-book",
    },
    "給宅男的戀愛指導": {
        "english_title": "Dating Guide for Introverts",
        "author": "安達裕哉",
        "publication_date": "",
        "note": "Japanese original",
    },
    "跟任何人都可以聊得來三": {
        "english_title": "How to Talk to Anyone 3",
        "author": "Leil Lowndes",
        "publication_date": "",
        "id": "how-to-talk-to-anyone-3",
    },
    "非常關係": {
        "english_title": "Extraordinary Relationship",
        "author": "黃維仁",
        "publication_date": "",
        "note": "Chinese original",
    },
    "非常關係2": {
        "english_title": "Extraordinary Relationship 2",
        "author": "黃維仁",
        "publication_date": "",
        "note": "Chinese original",
    },

    # === 求職準備 ===
    "下個十年，你在哪": {
        "english_title": "Where Will You Be in Ten Years",
        "author": "洪雪珍",
        "publication_date": "",
        "note": "Chinese original",
    },
    "人才，自造者": {
        "english_title": "Self-Made Talent",
        "author": "謝文憲",
        "publication_date": "",
        "note": "Chinese original",
    },
    "人生的長尾效應": {
        "english_title": "The Long Game",
        "author": "Brian Fetherstonhaugh",
        "publication_date": "May 2016",
    },
    "工作最重要的投資": {
        "english_title": "The Most Important Investment at Work",
        "author": "李開復",
        "publication_date": "",
        "note": "Chinese original",
        "id": "most-important-investment-at-work",
    },
    "砍掉重練": {
        "english_title": "Start Over",
        "author": "劉奕酉",
        "publication_date": "",
        "note": "Chinese original",
    },

    # === 社會哲思 ===
    "21世紀的21堂課": {
        "english_title": "21 Lessons for the 21st Century",
        "author": "Yuval Noah Harari",
        "publication_date": "August 2018",
    },
    "29張當票2：當舖裡特有的人生風景": {
        "english_title": "29 Pawn Tickets 2",
        "author": "秦嗣林",
        "publication_date": "",
        "note": "Chinese original",
    },
    "29張當票3：門簾外的人生鑑定": {
        "english_title": "29 Pawn Tickets 3",
        "author": "秦嗣林",
        "publication_date": "",
        "note": "Chinese original",
    },
    "29張當票：典當不到的人生啟發": {
        "english_title": "29 Pawn Tickets",
        "author": "秦嗣林",
        "publication_date": "",
        "note": "Chinese original",
    },
    "你拿什麼定義自己": {
        "english_title": "Myself and Other More Important Matters",
        "author": "Charles Handy",
        "publication_date": "2006",
    },
    "動機背後的隱藏邏輯": {
        "english_title": "Payoff: The Hidden Logic That Shapes Our Motivations",
        "author": "Dan Ariely",
        "publication_date": "November 2016",
        "id": "payoff",
    },
    "只有讀書能抵達的境界": {
        "english_title": "The Realm Only Reading Can Reach",
        "author": "齋藤孝",
        "publication_date": "",
        "note": "Japanese original",
    },
    "品格": {
        "english_title": "The Road to Character",
        "author": "David Brooks",
        "publication_date": "April 2015",
    },
    "四騎士主宰的未來": {
        "english_title": "The Four: The Hidden DNA of Amazon Apple Facebook and Google",
        "author": "Scott Galloway",
        "publication_date": "October 2017",
        "id": "the-four",
    },
    "大象與跳蚤": {
        "english_title": "The Elephant and the Flea",
        "author": "Charles Handy",
        "publication_date": "2001",
    },
    "失控的正向思考": {
        "english_title": "Bright-Sided",
        "author": "Barbara Ehrenreich",
        "publication_date": "October 2009",
    },
    "如何與律師打交道": {
        "english_title": "How to Deal with Lawyers",
        "author": "呂秋遠",
        "publication_date": "",
        "note": "Chinese original",
    },
    "學上當": {
        "english_title": "Born Liars",
        "author": "Ian Leslie",
        "publication_date": "2011",
    },
    "宗教的慰藉": {
        "english_title": "Religion for Atheists",
        "author": "Alain de Botton",
        "publication_date": "January 2012",
    },
    "寫給每個人的社會學讀本": {
        "english_title": "Sociology for Everyone",
        "author": "顧忠華",
        "publication_date": "",
        "note": "Chinese original",
    },
    "工作的哲學": {
        "english_title": "Philosophy of Work",
        "author": "新井直之",
        "publication_date": "",
        "note": "Japanese original",
    },
    "心靈地圖": {
        "english_title": "The Road Less Traveled",
        "author": "M. Scott Peck",
        "publication_date": "1978",
    },
    "心靈地圖2": {
        "english_title": "The Road Less Traveled and Beyond",
        "author": "M. Scott Peck",
        "publication_date": "1997",
    },
    "我們為何工作": {
        "english_title": "Why We Work",
        "author": "Barry Schwartz",
        "publication_date": "September 2015",
    },
    "我愛身分地位": {
        "english_title": "Status Anxiety",
        "author": "Alain de Botton",
        "publication_date": "2004",
    },
    "擁抱B選項": {
        "english_title": "Option B",
        "author": "Sheryl Sandberg, Adam Grant",
        "publication_date": "April 2017",
    },
    "文明的代價": {
        "english_title": "The Price of Civilization",
        "author": "Jeffrey D. Sachs",
        "publication_date": "October 2011",
    },
    "未來地圖": {
        "english_title": "WTF: Whats the Future and Why Its Up to Us",
        "author": "Tim O'Reilly",
        "publication_date": "October 2017",
        "id": "wtf-whats-the-future",
    },
    "活出生命最好的可能": {
        "english_title": "Live the Best Possible Life",
        "author": "洪蘭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "生命是長期而持續的累積": {
        "english_title": "Life Is a Long-Term Continuous Accumulation",
        "author": "彭明輝",
        "publication_date": "",
        "note": "Chinese original",
    },
    "當呼吸化為空氣": {
        "english_title": "When Breath Becomes Air",
        "author": "Paul Kalanithi",
        "publication_date": "January 2016",
    },
    "社會的趨勢": {
        "english_title": "Social Trends",
        "author": "洪蘭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "窮忙：我們這樣的世代": {
        "english_title": "The Working Poor",
        "author": "David K. Shipler",
        "publication_date": "2004",
    },
    "第二曲線": {
        "english_title": "The Second Curve",
        "author": "Charles Handy",
        "publication_date": "2015",
    },
    "終結平庸": {
        "english_title": "The End of Average",
        "author": "Todd Rose",
        "publication_date": "January 2016",
    },
    "美國，再見？": {
        "english_title": "Goodbye America",
        "author": "范疇",
        "publication_date": "",
        "note": "Chinese original",
    },
    "記得你是誰": {
        "english_title": "Remember Who You Are",
        "author": "Daisy Wademan",
        "publication_date": "2004",
    },
    "請問呂律師": {
        "english_title": "Ask Lawyer Lu",
        "author": "呂秋遠",
        "publication_date": "",
        "note": "Chinese original",
    },
    "這一生，你想留下什麼": {
        "english_title": "What Legacy Will You Leave",
        "author": "John C. Maxwell",
        "publication_date": "",
        "note": "Possibly Intentional Living",
        "id": "intentional-living",
    },
    "選民進化論": {
        "english_title": "The Evolution of Voters",
        "author": "劉必榮",
        "publication_date": "",
        "note": "Chinese original",
    },
    "黑道商學院": {
        "english_title": "Mob Rules",
        "author": "Louis Ferrante",
        "publication_date": "2011",
    },

    # === 社會禮儀 ===
    "為什麼菁英都是細節控": {
        "english_title": "Why Elites Are Detail Oriented",
        "author": "戶塚隆將",
        "publication_date": "",
        "note": "Japanese original",
    },
    "用「空服員說話法」輕鬆搞定各種人": {
        "english_title": "Flight Attendant Communication Method",
        "author": "加藤昌也",
        "publication_date": "",
        "note": "Japanese original",
        "id": "flight-attendant-communication",
    },

    # === 程式設計 ===
    "改變世界的九大演算法": {
        "english_title": "Nine Algorithms That Changed the Future",
        "author": "John MacCormick",
        "publication_date": "2011",
    },
    "決斷的演算": {
        "english_title": "Algorithms to Live By",
        "author": "Brian Christian, Tom Griffiths",
        "publication_date": "April 2016",
    },
    "程式設計師之禪": {
        "english_title": "The Zen Programmer",
        "author": "Christian Grobmeier",
        "publication_date": "2013",
        "id": "the-zen-programmer",
    },

    # === 策略制定 ===
    "QBQ！": {
        "english_title": "QBQ! The Question Behind the Question",
        "author": "John G. Miller",
        "publication_date": "September 2004",
        "id": "qbq",
    },
    "什麼才是經營最難的事？": {
        "english_title": "The Hard Thing About Hard Things",
        "author": "Ben Horowitz",
        "publication_date": "March 2014",
    },
    "如何下決定": {
        "english_title": "Decisive: How to Make Better Choices in Life and Work",
        "author": "Chip Heath, Dan Heath",
        "publication_date": "March 2013",
        "id": "decisive",
    },
    "思辨賽局": {
        "english_title": "The Art of Strategy",
        "author": "Avinash K. Dixit, Barry J. Nalebuff",
        "publication_date": "2008",
    },
    "決斷思考就是你的武器": {
        "english_title": "Decisive Thinking Is Your Weapon",
        "author": "赤羽雄二",
        "publication_date": "",
        "note": "Japanese original",
    },
    "真正的問題你想通了嗎？": {
        "english_title": "Are Your Lights On",
        "author": "Donald C. Gause, Gerald M. Weinberg",
        "publication_date": "1982",
    },
    "突破性思考": {
        "english_title": "Harvard Business Review on Breakthrough Thinking",
        "author": "Harvard Business Review",
        "publication_date": "1999",
    },
    "策略思考": {
        "english_title": "Strategic Thinking",
        "author": "劉必榮",
        "publication_date": "",
        "note": "Chinese original",
    },
    "致富強心臟": {
        "english_title": "Wealthy Strong Heart",
        "author": "鄭華娟",
        "publication_date": "",
        "note": "Chinese original",
    },
    "金牌法則": {
        "english_title": "The Golden Rules",
        "author": "Bob Bowman",
        "publication_date": "2016",
    },

    # === 自我管理 ===
    "35歲前要做的33件事": {
        "english_title": "33 Things to Do Before 35",
        "author": "蔡虹",
        "publication_date": "",
        "note": "Chinese original",
    },
    "40歲一定要養成的關鍵習慣": {
        "english_title": "Key Habits to Build by 40",
        "author": "午堂登紀雄",
        "publication_date": "",
        "note": "Japanese original",
    },
    "Google時代一定要會的整理術": {
        "english_title": "A Perfect Mess",
        "author": "Eric Abrahamson, David H. Freedman",
        "publication_date": "2006",
        "id": "a-perfect-mess",
    },
    "一流的人讀書，都在哪裡畫線？": {
        "english_title": "Where Top Performers Draw the Line When Reading",
        "author": "土井英司",
        "publication_date": "",
        "note": "Japanese original",
        "id": "where-top-performers-highlight",
    },
    "世界頂尖人士如何實踐這樣的基本功": {
        "english_title": "How World-Class Performers Practice the Basics",
        "author": "戶塚隆將",
        "publication_date": "",
        "note": "Japanese original",
        "id": "how-world-class-practice-basics",
    },
    "人生思考題": {
        "english_title": "Life Questions",
        "author": "洪蘭",
        "publication_date": "",
        "note": "Chinese original",
    },
    "你要如何衡量你的人生": {
        "english_title": "How Will You Measure Your Life",
        "author": "Clayton M. Christensen",
        "publication_date": "May 2012",
    },
    "做自己的生命設計師": {
        "english_title": "Designing Your Life",
        "author": "Bill Burnett, Dave Evans",
        "publication_date": "September 2016",
    },
    "刻意練習": {
        "english_title": "Peak: Secrets from the New Science of Expertise",
        "author": "Anders Ericsson, Robert Pool",
        "publication_date": "April 2016",
        "id": "peak",
    },
    "原子習慣": {
        "english_title": "Atomic Habits",
        "author": "James Clear",
        "publication_date": "October 2018",
    },
    "哈佛教你做好自我管理": {
        "english_title": "HBR's 10 Must Reads on Managing Yourself",
        "author": "Harvard Business Review",
        "publication_date": "2010",
        "id": "hbr-managing-yourself",
    },
    "哈佛菁英課": {
        "english_title": "Harvard Elite Course",
        "author": "韋秀英",
        "publication_date": "",
        "note": "Chinese original",
    },
    "大人學選擇": {
        "english_title": "Adult Learning to Choose",
        "author": "姚詩豪, 張國洋",
        "publication_date": "",
        "note": "Chinese original",
    },
    "將世界菁英的工作方式整理成冊": {
        "english_title": "Work Methods of World Elites",
        "author": "金武貴",
        "publication_date": "",
        "note": "Japanese original",
        "id": "work-methods-of-world-elites",
    },
    "專業": {
        "english_title": "The Professional",
        "author": "大前研一",
        "publication_date": "",
        "note": "Japanese original by Kenichi Ohmae",
        "id": "the-professional-ohmae",
    },
    "少 但是更好": {
        "english_title": "Essentialism: The Disciplined Pursuit of Less",
        "author": "Greg McKeown",
        "publication_date": "April 2014",
        "id": "essentialism",
    },
    "恆毅力：人生成功的究極能力": {
        "english_title": "Grit: The Power of Passion and Perseverance",
        "author": "Angela Duckworth",
        "publication_date": "May 2016",
        "id": "grit",
    },
    "成功不再跌跌撞撞": {
        "english_title": "Failing Forward",
        "author": "John C. Maxwell",
        "publication_date": "2000",
    },
    "成功人士一定會做的9件事情": {
        "english_title": "Nine Things Successful People Do Differently",
        "author": "Heidi Grant Halvorson",
        "publication_date": "February 2012",
    },
    "時間，越用越有價值": {
        "english_title": "Time: The More You Use It the More Valuable It Becomes",
        "author": "洪雪珍",
        "publication_date": "",
        "note": "Chinese original",
        "id": "time-becomes-more-valuable",
    },
    "未來最需要的新人才": {
        "english_title": "The New Talent the Future Needs",
        "author": "洪雪珍",
        "publication_date": "",
        "note": "Chinese original",
        "id": "new-talent-for-the-future",
    },
    "每天最重要的2小時": {
        "english_title": "Two Awesome Hours",
        "author": "Josh Davis",
        "publication_date": "February 2015",
    },
    "深度學習力": {
        "english_title": "Ultralearning",
        "author": "Scott H. Young",
        "publication_date": "August 2019",
    },
    "深度工作力": {
        "english_title": "Deep Work",
        "author": "Cal Newport",
        "publication_date": "January 2016",
    },
    "生活的藝術": {
        "english_title": "The Art of Living",
        "author": "Epictetus / Sharon Lebell",
        "publication_date": "1995",
    },
    "終結低等勤奮": {
        "english_title": "End Low-Level Diligence",
        "author": "孫圈圈",
        "publication_date": "",
        "note": "Chinese original",
    },
    "與成功有約": {
        "english_title": "The 7 Habits of Highly Effective People",
        "author": "Stephen R. Covey",
        "publication_date": "1989",
    },
    "複製成功腦": {
        "english_title": "Copy the Successful Brain",
        "author": "石浦章士",
        "publication_date": "",
        "note": "Japanese original",
    },
    "週一清晨的領導課": {
        "english_title": "Monday Morning Leadership",
        "author": "David Cottrell",
        "publication_date": "2002",
    },
    "關鍵四秒": {
        "english_title": "Four Seconds",
        "author": "Peter Bregman",
        "publication_date": "February 2015",
    },
    "零秒思考力": {
        "english_title": "Zero Second Thinking",
        "author": "赤羽雄二",
        "publication_date": "",
        "note": "Japanese original",
    },
    "零秒思考力[實踐篇]": {
        "english_title": "Zero Second Thinking Practice",
        "author": "赤羽雄二",
        "publication_date": "",
        "note": "Japanese original",
    },
    "領導最好的自己": {
        "english_title": "Lead the Best of Yourself",
        "author": "洪雪珍",
        "publication_date": "",
        "note": "Chinese original",
    },
    "麥肯錫の零秒邏輯思考": {
        "english_title": "McKinsey Zero Second Logical Thinking",
        "author": "赤羽雄二",
        "publication_date": "",
        "note": "Japanese original",
        "id": "mckinsey-zero-second-thinking",
    },
    "麥肯錫情緒處理與菁英養成法": {
        "english_title": "McKinsey Emotional Management and Elite Training",
        "author": "赤羽雄二",
        "publication_date": "",
        "note": "Japanese original",
        "id": "mckinsey-emotional-management",
    },
    "麥肯錫菁英最重視的39個工作習慣": {
        "english_title": "McKinseys 39 Most Important Work Habits",
        "author": "大嶋祥譽",
        "publication_date": "",
        "note": "Japanese original",
        "id": "mckinsey-39-work-habits",
    },

    # === 財經企管 ===
    "The Richest Man in Babylon": {
        "english_title": "The Richest Man in Babylon",
        "author": "George S. Clason",
        "publication_date": "1926",
    },
    "富爸爸-有錢人的大陰謀": {
        "english_title": "Rich Dads Conspiracy of the Rich",
        "author": "Robert T. Kiyosaki",
        "publication_date": "2009",
    },
    "拿鐵因子": {
        "english_title": "The Latte Factor",
        "author": "David Bach, John David Mann",
        "publication_date": "May 2019",
    },
    "耶魯最受歡迎的金融通識課": {
        "english_title": "Finance for the Rest of Us",
        "author": "陳志武",
        "publication_date": "",
        "note": "Chinese original",
        "id": "yale-finance-course",
    },
    "致勝的答案": {
        "english_title": "The Winning Answer",
        "author": "洪雪珍",
        "publication_date": "",
        "note": "Chinese original",
    },
    "致富心態": {
        "english_title": "The Psychology of Money",
        "author": "Morgan Housel",
        "publication_date": "September 2020",
    },
    "讓人變有錢的36個微習慣": {
        "english_title": "36 Micro Habits That Make You Rich",
        "author": "菅原圭",
        "publication_date": "",
        "note": "Japanese original",
    },
    "財務自由實踐版": {
        "english_title": "Financial Freedom in Practice",
        "author": "吳淡如",
        "publication_date": "",
        "note": "Chinese original",
    },
    "走過失業，我喜歡現在的人生": {
        "english_title": "Through Unemployment I Like My Life Now",
        "author": "洪雪珍",
        "publication_date": "",
        "note": "Chinese original",
    },

    # === 身體保健 ===
    "不再痛風的生活": {
        "english_title": "Life Without Gout",
        "author": "",
        "publication_date": "",
        "note": "Health book",
    },
    "快速瘦肚！間歇性斷食減醣全書": {
        "english_title": "Intermittent Fasting and Low Carb Diet Complete Guide",
        "author": "江部康二",
        "publication_date": "",
        "note": "Japanese original",
        "id": "intermittent-fasting-low-carb",
    },
    "最高睡眠法": {
        "english_title": "Sleep Smarter",
        "author": "西野精治",
        "publication_date": "",
        "note": "Japanese original - Stanford sleep research",
        "id": "stanford-sleep-method",
    },

    # === 關係互動 ===
    "你的身體正在洩漏你的秘密": {
        "english_title": "What Every Body Is Saying",
        "author": "Joe Navarro",
        "publication_date": "2008",
    },
    "善用你的談話風格": {
        "english_title": "Thats Not What I Meant",
        "author": "Deborah Tannen",
        "publication_date": "1986",
    },
    "好好拜託": {
        "english_title": "Reinforcements: How to Get People to Help You",
        "author": "Heidi Grant",
        "publication_date": "March 2018",
        "id": "reinforcements",
    },
    "愛，生活與學習": {
        "english_title": "Living Loving and Learning",
        "author": "Leo Buscaglia",
        "publication_date": "1982",
    },
    "朋友與敵人": {
        "english_title": "Friend and Foe",
        "author": "Adam Galinsky, Maurice Schweitzer",
        "publication_date": "September 2015",
    },
    "沒人懂你怎麼辦？": {
        "english_title": "No One Understands You and What to Do About It",
        "author": "Heidi Grant Halvorson",
        "publication_date": "2015",
    },
    "眼神不敗術": {
        "english_title": "The Power of Eye Contact",
        "author": "Michael Ellsberg",
        "publication_date": "2010",
    },
    "說話的品格": {
        "english_title": "The Dignity of Speaking",
        "author": "李起周",
        "publication_date": "",
        "note": "Korean original",
    },
    "高效信任力": {
        "english_title": "The Speed of Trust",
        "author": "Stephen M.R. Covey",
        "publication_date": "2006",
    },
}


def to_kebab(title):
    """Convert English title to kebab-case ID"""
    import re
    # Remove special chars
    s = title.lower()
    s = re.sub(r"[''']", "", s)
    s = re.sub(r"[^a-z0-9\s-]", " ", s)
    s = re.sub(r"\s+", "-", s.strip())
    s = re.sub(r"-+", "-", s)
    s = s.strip("-")
    return s


def check_duplicate(book_id):
    """Check if a repo already exists"""
    return book_id in EXISTING_REPOS


# Category comments mapping (line number -> category)
CATEGORIES = {
    "基督信仰": [
        "上帝值夜班", "上帝的混沌理論", "世界在等待的門徒",
        "全民神學家巴刻：從認識神到事奉神", "工人之路", "幽谷之旅",
        "暴力世界中的溫柔", "有異象的人", "渴慕神", "禱告",
        "約櫃流浪記", "聖潔讓你想得不一樣", "行在水面上", "門徒好豐盛",
        "一個人的聖殿", "一路愛到底", "工作真重要", "成熟之愛",
        "為約會立界線", "男人是蚌女人是鐵撬", "神啊！說好的那個人呢？",
        "回到正統", "安息日的真諦", "福音作為悲劇、喜劇和童話",
        "認識上帝與認識人的9個探險", "邪惡與上帝新世界 ",
    ],
    "家庭教養": ["我是好爸爸", "教出孩子的生存力"],
    "專案管理": ["Peopleware", "Sprint衝刺計畫", "Team Geek", "顧問成功的祕密"],
    "心理建設": [
        "一流的人如何保持顛峰", "不讓自己成為箭靶",
        "你的不安，是因為太習慣受傷害", "哈佛教你打造健康人生",
        "心態致勝", "心靈療癒自助手冊", "思考的藝術", "怪咖心理學",
        "情緒勒索", "探索人格潛能，看見更真實的自己", "最珍貴的教訓",
        "柯維經典語錄", "比賽，從心開始", "潛意識正在控制你的行為",
        "煩惱都是自己想出來的", "真心接納自己，培養健全的自尊心",
        "與自己對話", "行為的藝術", "被討厭的勇氣 二", "被討厭的勇氣",
        "諮商與心理治療", "阿德勒心理學講義", "陪你飛一程",
    ],
    "愛情再思": [
        "愛情非童話", "愛的進化論", "我談的那場戀愛", "男女親密對話",
        "精準撩動人心的戀愛人類學", "給宅男的戀愛指導",
        "跟任何人都可以聊得來三", "非常關係", "非常關係2",
    ],
    "求職準備": [
        "下個十年，你在哪", "人才，自造者", "人生的長尾效應",
        "工作最重要的投資", "砍掉重練",
    ],
    "社會哲思": [
        "21世紀的21堂課", "29張當票2：當舖裡特有的人生風景",
        "29張當票3：門簾外的人生鑑定", "29張當票：典當不到的人生啟發",
        "你拿什麼定義自己", "動機背後的隱藏邏輯", "只有讀書能抵達的境界",
        "品格", "四騎士主宰的未來", "大象與跳蚤", "失控的正向思考",
        "如何與律師打交道", "學上當", "宗教的慰藉", "寫給每個人的社會學讀本",
        "工作的哲學", "心靈地圖", "心靈地圖2", "我們為何工作",
        "我愛身分地位", "擁抱B選項", "文明的代價", "未來地圖",
        "活出生命最好的可能", "生命是長期而持續的累積", "當呼吸化為空氣",
        "社會的趨勢", "窮忙：我們這樣的世代", "第二曲線", "終結平庸",
        "美國，再見？", "記得你是誰", "請問呂律師", "這一生，你想留下什麼",
        "選民進化論", "黑道商學院",
    ],
    "社會禮儀": ["為什麼菁英都是細節控", "用「空服員說話法」輕鬆搞定各種人"],
    "程式設計": ["改變世界的九大演算法", "決斷的演算", "程式設計師之禪"],
    "策略制定": [
        "QBQ！", "什麼才是經營最難的事？", "如何下決定", "思辨賽局",
        "決斷思考就是你的武器", "真正的問題你想通了嗎？", "突破性思考",
        "策略思考", "致富強心臟", "金牌法則",
    ],
    "自我管理": [
        "35歲前要做的33件事", "40歲一定要養成的關鍵習慣",
        "Google時代一定要會的整理術", "一流的人讀書，都在哪裡畫線？",
        "世界頂尖人士如何實踐這樣的基本功", "人生思考題",
        "你要如何衡量你的人生", "做自己的生命設計師", "刻意練習",
        "原子習慣", "哈佛教你做好自我管理", "哈佛菁英課", "大人學選擇",
        "將世界菁英的工作方式整理成冊", "專業", "少 但是更好",
        "恆毅力：人生成功的究極能力", "成功不再跌跌撞撞",
        "成功人士一定會做的9件事情", "時間，越用越有價值",
        "未來最需要的新人才", "每天最重要的2小時", "深度學習力",
        "深度工作力", "生活的藝術", "終結低等勤奮", "與成功有約",
        "複製成功腦", "週一清晨的領導課", "關鍵四秒", "零秒思考力",
        "零秒思考力[實踐篇]", "領導最好的自己", "麥肯錫の零秒邏輯思考",
        "麥肯錫情緒處理與菁英養成法", "麥肯錫菁英最重視的39個工作習慣",
    ],
    "財經企管": [
        "The Richest Man in Babylon", "富爸爸-有錢人的大陰謀", "拿鐵因子",
        "耶魯最受歡迎的金融通識課", "致勝的答案", "致富心態",
        "讓人變有錢的36個微習慣", "財務自由實踐版",
        "走過失業，我喜歡現在的人生",
    ],
    "身體保健": [
        "不再痛風的生活", "快速瘦肚！間歇性斷食減醣全書", "最高睡眠法",
    ],
    "關係互動": [
        "你的身體正在洩漏你的秘密", "善用你的談話風格", "好好拜託",
        "愛，生活與學習", "朋友與敵人", "沒人懂你怎麼辦？",
        "眼神不敗術", "說話的品格", "高效信任力",
    ],
}


def generate_yaml():
    lines = []
    lines.append("# Books Queue - 批次建立書籍")
    lines.append("# 執行: ./gradlew initBooks")
    lines.append("#")
    lines.append("# 狀態說明:")
    lines.append("#   pending    - 等待處理")
    lines.append("#   processing - AI 任務已產生，等待 Claude 處理")
    lines.append("#   completed  - 已完成建立")
    lines.append("#   error      - 處理失敗")
    lines.append("#   duplicate  - 已有 GitHub repo，跳過")
    lines.append("#")
    lines.append("# 使用方式:")
    lines.append("#   1. 在 books 列表中新增書籍資訊，status 設為 pending")
    lines.append("#   2. 執行 ./gradlew initBooks")
    lines.append("#   3. 依提示請 Claude 處理 AI 任務")
    lines.append("#   4. 再次執行 ./gradlew initBooks 完成建立")
    lines.append("#   5. 系統會自動處理下一本 pending 的書")
    lines.append("#")
    lines.append("# 進階用法:")
    lines.append("#   ./gradlew initBooks --status              # 顯示佇列狀態")
    lines.append("#   ./gradlew initBooks -Pid=book-id          # 只處理指定書籍")
    lines.append("#   ./gradlew initBooks -Pid=book-id --reset  # 重設狀態為 pending")
    lines.append("")
    lines.append("books:")

    duplicate_count = 0
    total_count = 0

    for category, book_titles in CATEGORIES.items():
        lines.append("")
        lines.append(f"    # === {category} ===")
        lines.append("")

        for chinese_title in book_titles:
            total_count += 1
            book = BOOKS.get(chinese_title)
            if not book:
                print(f"WARNING: No metadata for '{chinese_title}'")
                continue

            english_title = book["english_title"]
            author = book["author"]
            pub_date = book.get("publication_date", "")

            # Determine ID
            if "id" in book:
                book_id = book["id"]
            else:
                book_id = to_kebab(english_title)

            # Check duplicate
            is_duplicate = check_duplicate(book_id)
            status = "duplicate" if is_duplicate else "pending"
            if is_duplicate:
                duplicate_count += 1

            # Format the entry
            lines.append(f'-   id: {book_id}')
            lines.append(f'    status: {status}')
            lines.append(f'    chinese_title: "{chinese_title.strip()}"')
            lines.append(f'    english_title: "{english_title}"')
            lines.append(f'    author: "{author}"')
            lines.append(f'    publication_date: "{pub_date}"')
            lines.append(f'    cover_url: ""')
            lines.append(f'    purchase_url: ""')
            lines.append(f'    table_of_contents: |')
            lines.append(f'      （待補充）')
            lines.append("")

    print(f"\nTotal: {total_count} books")
    print(f"Duplicates (existing repos): {duplicate_count}")
    print(f"New books to create: {total_count - duplicate_count}")

    return "\n".join(lines) + "\n"


if __name__ == "__main__":
    yaml_content = generate_yaml()
    output_path = "/home/andrew/workspace/andrew/books-management/hugo-book-manager/templates/books-queue.yaml"
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(yaml_content)
    print(f"\nWritten to {output_path}")
