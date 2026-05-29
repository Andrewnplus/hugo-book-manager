# Podcasts

恩普拉氏 與 院外門徒路 兩個 Podcast 頻道的規劃文件與音訊剪輯工具。

## 目錄

```
podcasts/
├── planning/
│   ├── 兩頻道規劃總結.md       # 頻道定位、模板台詞、SoundOn/上架流程、命名編號
│   └── 剪輯流程與工具指南.md   # 剪輯三段式流程、工具、DaVinci 對應、Mac 指令
└── scripts/
    ├── podcast-master.sh       # 母帶處理：high-pass→去噪→壓縮→響度正規化（two-pass）
    ├── trim-silence.sh         # 自動切死空氣（auto-editor 粗剪）
    └── transcribe.sh           # 產中文逐字稿/字幕（whisper.cpp）
```

## 剪輯三段式流程

```
① 自動粗剪          →  ② 人力精修（耳朵）   →  ③ 母帶處理
   trim-silence.sh        DaVinci Fairlight       podcast-master.sh
```

詳見 `planning/剪輯流程與工具指南.md`。

## macOS 安裝

```bash
brew install ffmpeg sox python whisper-cpp
pip3 install auto-editor
# whisper 模型：下載 ggml-large-v3.bin 放到 ~/.whisper-models/
```

## 腳本用法

```bash
chmod +x scripts/*.sh        # 首次（git 已保留可執行權限，clone 後通常免）

scripts/trim-silence.sh   raw.wav              # → raw-trimmed.wav
scripts/podcast-master.sh edited.wav           # → edited-master.wav（-16 LUFS / -1 dBTP）
scripts/transcribe.sh     edited.wav           # → edited.srt / edited.txt
```

各腳本開頭都有可用環境變數說明（如 `TARGET_I=-19`、`MARGIN=0.3sec`、`LANG_CODE=en`）。

> 注意：院外門徒路的原始素材（封面 png、企劃 docx、計畫表 xlsx）仍在
> `~/workspace/andrew/documents/podcasts/院外門徒路/`，未納入本 repo。
