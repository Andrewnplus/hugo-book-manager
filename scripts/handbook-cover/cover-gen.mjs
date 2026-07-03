import { writeFileSync } from 'node:fs';

// ============================================================
//  NPLUS Handbook — unified cover generator
//  Style A (Editorial Dark), PORTRAIT 3:4 book-cover, 900x1200
//  Usage: node cover-portrait.mjs <key> <outHtmlPath>
// ============================================================

const MOTIFS = {
  // 01 binary tree — algorithms
  tree: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g stroke="${c}" stroke-width="7" opacity=".9" stroke-linecap="round">
      <line x1="200" y1="60" x2="90" y2="200"/><line x1="200" y1="60" x2="310" y2="200"/>
      <line x1="90" y1="200" x2="50" y2="340"/><line x1="90" y1="200" x2="150" y2="340"/>
      <line x1="310" y1="200" x2="250" y2="340"/><line x1="310" y1="200" x2="350" y2="340"/></g>
    <g fill="${c}">
      <circle cx="200" cy="60" r="34"/><circle cx="90" cy="200" r="34"/><circle cx="310" cy="200" r="34"/>
      <circle cx="50" cy="340" r="34"/><circle cx="150" cy="340" r="34"/><circle cx="250" cy="340" r="34"/>
      <circle cx="350" cy="340" r="34"/></g></svg>`,

  // 02 CPU chip — computer systems
  chip: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g stroke="${c}" stroke-width="7" stroke-linecap="round">
      ${[130,175,225,270].map(x=>`<line x1="${x}" y1="96" x2="${x}" y2="132"/><line x1="${x}" y1="268" x2="${x}" y2="304"/>`).join('')}
      ${[130,175,225,270].map(y=>`<line x1="96" y1="${y}" x2="132" y2="${y}"/><line x1="268" y1="${y}" x2="304" y2="${y}"/>`).join('')}
    </g>
    <rect x="130" y="130" width="140" height="140" rx="20" fill="none" stroke="${c}" stroke-width="7"/>
    <rect x="168" y="168" width="64" height="64" rx="10" fill="${c}"/></svg>`,

  // 03 code brackets </> — programming languages
  code: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g stroke="${c}" stroke-width="18" fill="none" stroke-linecap="round" stroke-linejoin="round">
      <polyline points="150,130 80,200 150,270"/>
      <polyline points="250,130 320,200 250,270"/>
      <line x1="222" y1="120" x2="178" y2="280"/></g></svg>`,

  // 04 coffee mug — java
  mug: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g stroke="${c}" stroke-width="7" fill="none" stroke-linecap="round">
      <path d="M150 120 q-14 -22 8 -40"/><path d="M200 120 q-14 -22 8 -40"/><path d="M250 120 q-14 -22 8 -40"/>
      <path d="M262 190 q46 0 46 36 q0 36 -46 36"/></g>
    <path d="M132 168 h130 v70 q0 60 -65 60 q-65 0 -65 -60 z" fill="${c}"/>
    <ellipse cx="197" cy="322" rx="86" ry="16" fill="${c}" opacity=".5"/></svg>`,

  // 05 browser window — frontend
  browser: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <rect x="86" y="108" width="228" height="184" rx="18" fill="none" stroke="${c}" stroke-width="7"/>
    <line x1="86" y1="152" x2="314" y2="152" stroke="${c}" stroke-width="7"/>
    <g fill="${c}"><circle cx="112" cy="130" r="7"/><circle cx="136" cy="130" r="7"/><circle cx="160" cy="130" r="7"/></g>
    <rect x="110" y="180" width="80" height="88" rx="8" fill="${c}"/>
    <g stroke="${c}" stroke-width="12" stroke-linecap="round">
      <line x1="216" y1="192" x2="290" y2="192"/><line x1="216" y1="224" x2="290" y2="224"/><line x1="216" y1="256" x2="260" y2="256"/></g></svg>`,

  // 06 database stack — infrastructure & storage
  db: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <path d="M110 132 v136 a90 30 0 0 0 180 0 v-136" fill="none" stroke="${c}" stroke-width="7"/>
    <ellipse cx="200" cy="132" rx="90" ry="30" fill="${c}"/>
    <path d="M110 178 a90 30 0 0 0 180 0" fill="none" stroke="${c}" stroke-width="7"/>
    <path d="M110 224 a90 30 0 0 0 180 0" fill="none" stroke="${c}" stroke-width="7"/></svg>`,

  // 07 funnel — recommendation
  funnel: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g fill="${c}"><circle cx="130" cy="96" r="15"/><circle cx="200" cy="96" r="15"/><circle cx="270" cy="96" r="15"/></g>
    <path d="M96 140 h208 l-72 92 v66 l-64 30 v-96 z" fill="${c}"/>
    <path d="M200 316 l16 -30 h-32 z" fill="${c}"/>
    <g fill="${c}"><circle cx="200" cy="352" r="15"/></g></svg>`,

  // 08 git branch — software engineering
  git: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g stroke="${c}" stroke-width="9" fill="none" stroke-linecap="round">
      <line x1="150" y1="96" x2="150" y2="304"/>
      <path d="M150 180 C150 235, 258 210, 258 262"/></g>
    <g fill="${c}">
      <circle cx="150" cy="96" r="26"/><circle cx="150" cy="304" r="26"/><circle cx="258" cy="262" r="26"/></g></svg>`,

  // 09 compass — tech leadership & product
  compass: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <circle cx="200" cy="200" r="120" fill="none" stroke="${c}" stroke-width="7"/>
    <path d="M200 96 L236 200 L200 214 L164 200 Z" fill="${c}"/>
    <path d="M200 304 L164 200 L200 186 L236 200 Z" fill="${c}" opacity=".45"/>
    <circle cx="200" cy="200" r="12" fill="${c}"/></svg>`,

  // 10 growth steps — career growth
  growth: (c) => `<svg viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
    <g fill="${c}">
      <rect x="96" y="240" width="56" height="72" rx="8"/>
      <rect x="172" y="192" width="56" height="120" rx="8"/>
      <rect x="248" y="132" width="56" height="180" rx="8"/></g>
    <g stroke="${c}" stroke-width="9" fill="none" stroke-linecap="round" stroke-linejoin="round">
      <polyline points="110,196 200,150 300,96"/>
      <polyline points="262,96 300,96 300,134"/></g></svg>`,
};

const BOOKS = {
  'algorithms-data-structures': { title:'演算法與數學', en:'Algorithms & Mathematics', index:'01', accent:'#6d8cff', accent2:'#3a52cc', motif:'tree' },
  'computer-systems':           { title:'計算機系統',   en:'Computer Systems',       index:'02', accent:'#35c6d6', accent2:'#1f8fa0', motif:'chip' },
  'programming-languages':      { title:'程式語言精通', en:'Programming Languages',  index:'03', accent:'#a678ff', accent2:'#6f47c9', motif:'code' },
  'java-mastery':               { title:'Java 精通',     en:'Java Mastery',           index:'04', accent:'#e8a23d', accent2:'#c06f1c', motif:'mug' },
  'frontend-development':       { title:'前端開發',     en:'Frontend Development',   index:'05', accent:'#ff7a9c', accent2:'#d64d72', motif:'browser' },
  'infrastructure-architecture':{ title:'架構、網絡與存儲', en:'Infrastructure & Storage', index:'06', accent:'#2fbf9e', accent2:'#1c8a72', motif:'db' },
  'recommendation-system':      { title:'推薦系統',     en:'Recommendation Systems', index:'07', accent:'#4fc26b', accent2:'#2f8f46', motif:'funnel' },
  'software-engineering-practice':{ title:'軟體工程實踐', en:'Software Engineering', index:'08', accent:'#4d9dff', accent2:'#2a6ac9', motif:'git' },
  'tech-leadership-product':    { title:'技術領導與產品思維', en:'Tech Leadership & Product', index:'09', accent:'#c264d6', accent2:'#8f3fa0', motif:'compass' },
  'career-growth':              { title:'職場成長與軟實力', en:'Career & Soft Skills', index:'10', accent:'#d9a441', accent2:'#a9781f', motif:'growth' },
};

const key = process.argv[2];
const out = process.argv[3];
const b = BOOKS[key];
if (!b) { console.error('unknown key', key); process.exit(1); }

const n = [...b.title].length;
const titleSize = n <= 4 ? 156 : n <= 5 ? 136 : n <= 6 ? 116 : n <= 7 ? 100 : n <= 8 ? 88 : 78;

const html = `<!doctype html><html><head><meta charset="utf-8"><style>
*{margin:0;padding:0;box-sizing:border-box}
html,body{width:900px;height:1200px;overflow:hidden}
body{font-family:'Noto Sans CJK TC',sans-serif;background:#0e1220;position:relative}
.wrap{width:900px;height:1200px;position:relative;padding:88px 76px}
.glow{position:absolute;top:-160px;right:-140px;width:620px;height:620px;
  background:radial-gradient(circle,${b.accent}3d 0%,transparent 62%)}
.grid{position:absolute;inset:0;opacity:.5;
  background-image:radial-gradient(#ffffff10 1.4px,transparent 1.4px);background-size:30px 30px}
.kicker{position:relative;display:flex;align-items:center;gap:18px;color:${b.accent};
  font-size:26px;font-weight:700;letter-spacing:.26em}
.rule{display:inline-block;width:58px;height:4px;background:${b.accent};border-radius:2px}
.motif{position:absolute;left:50%;top:432px;transform:translate(-50%,-50%);
  width:420px;height:420px;opacity:.95;filter:drop-shadow(0 0 60px ${b.accent}55)}
.title-block{position:absolute;left:76px;right:76px;bottom:232px}
h1{color:#fff;font-size:${titleSize}px;font-weight:900;line-height:1.08;letter-spacing:.02em}
.en{color:#8b93a7;font-size:32px;font-weight:500;letter-spacing:.12em;margin-top:26px;text-transform:uppercase}
.foot{position:absolute;left:76px;bottom:104px;display:flex;align-items:center;gap:18px;
  color:#5a6274;font-size:26px;font-weight:600;letter-spacing:.05em}
.foot .dot{font-size:11px;color:${b.accent}}
.baseline{position:absolute;left:0;bottom:0;width:100%;height:12px;
  background:linear-gradient(90deg,${b.accent} 0%,${b.accent2} 100%)}
</style></head><body><div class="wrap">
  <div class="glow"></div><div class="grid"></div>
  <div class="kicker"><span class="rule"></span>NPLUS · ${b.index}</div>
  <div class="motif">${MOTIFS[b.motif](b.accent)}</div>
  <div class="title-block">
    <h1>${b.title}</h1>
    <div class="en">${b.en}</div>
  </div>
  <div class="foot"><span>HANDBOOK</span><span class="dot">●</span><span>nplus.wiki</span></div>
  <div class="baseline"></div>
</div></body></html>`;

writeFileSync(out, html);
console.log('ok', key, 'titleSize', titleSize);
