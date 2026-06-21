# Podcasts

恩普拉氏 與 院外門徒路 兩個 Podcast 頻道的規劃文件與音訊剪輯工具。

## 目錄

```
podcasts/
├── planning/
│   ├── 兩頻道規劃總結.md       # 頻道定位、模板台詞、SoundOn/上架流程、命名編號
│   └── 剪輯流程與工具指南.md   # 剪輯三段式流程、工具、DaVinci 對應、Mac 指令
└── scripts/
    ├── podcast-pipeline.sh     # Manager：掃描媒體根目錄、原地產出、冪等（推薦入口）
    ├── podcast-master.sh       # 母帶處理：high-pass→去噪→壓縮→響度正規化（two-pass）
    ├── trim-silence.sh         # 自動切死空氣（auto-editor 粗剪）
    └── transcribe.sh           # 產中文逐字稿/字幕（whisper.cpp + opencc 簡轉繁）
```

## Manager：一鍵掃描媒體資料夾

把錄音丟到 **`~/workspace/podcasts`**（可任意巢狀分頻道/系列/集），Manager 會逐檔按需要跑去空白→母帶→逐字稿，產出放在來源旁邊、用後綴標階段，狀態由「檔案是否存在」決定（冪等、重跑安全）。日常對話用 `/podcast-edit` skill 驅動。

```bash
PIPE=scripts/podcast-pipeline.sh
"$PIPE" status              # 看每個來源在哪一階段
"$PIPE" trim               # 只去空白（剪輯前）
"$PIPE" master             # 只母帶（有 -edited 用它，否則用 -trimmed）
"$PIPE" transcribe         # 只逐字稿（對 -master）
"$PIPE" auto               # 一條龍 trim→master→transcribe（略過人耳精修）
# 可加路徑限定範圍：scripts/podcast-pipeline.sh auto "恩普拉氏/恩普拉氏 － EP0.m4a"
```

後綴約定：`foo.m4a`（來源）→ `foo-trimmed.wav`（去空白）→ `foo-edited.wav`（DaVinci 手動精修，選用）→ `foo-master.wav`（母帶）→ `foo-master.srt/.txt`（繁體逐字稿）。
環境變數 `PODCAST_ROOT` 覆寫根目錄、`MASTER_FROM=trimmed|edited|auto` 指定母帶來源。

## 剪輯三段式流程

```
① 自動粗剪          →  ② 人力精修（耳朵）   →  ③ 母帶處理
   trim-silence.sh        DaVinci Fairlight       podcast-master.sh
```

詳見 `planning/剪輯流程與工具指南.md`。

## macOS 安裝

```bash
brew install ffmpeg sox python whisper-cpp opencc pipx
pipx install auto-editor
# whisper 模型：下載 ggml-large-v3.bin 放到 ~/.whisper-models/
#   curl -L -o ~/.whisper-models/ggml-large-v3.bin \
#     https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-large-v3.bin
```

## 腳本用法

```bash
chmod +x scripts/*.sh        # 首次（git 已保留可執行權限，clone 後通常免）

scripts/trim-silence.sh   raw.wav              # → raw-trimmed.wav
scripts/podcast-master.sh edited.wav           # → edited-master.wav（-16 LUFS / -1 dBTP）
scripts/transcribe.sh     edited.wav           # → edited.srt / edited.txt
```

各腳本開頭都有可用環境變數說明（如 `TARGET_I=-19`、`MARGIN=0.3sec`、`LANG_CODE=en`）。

## 卡頓錄音 → 通順講稿（逐字稿整理）

錄的時候邊想邊講、卡卡的（卡頓重講、贅詞、繞圈、邏輯跳躍），想把它整理成一份**語句通順、邏輯銜接補強、可照著重講**的口語講稿時，用這條兩步流程（接在 `transcribe.sh` 之後）：

```
transcribe.sh        →  ① 外部 API 點評        →  ② Skill 整理
（whisper 中文稿）       謄寫分段＋點評建議         /podcast-smooth-transcript
edited.txt              （只診斷不改寫）            → edited.smooth.md（通順講稿）
```

- **① 外部 API**：把 whisper 轉出的中文逐字稿，連同 prompt 貼給外部 LLM 服務，產出「重新分段的逐字稿＋逐條點評與建議」（卡頓／贅詞／重複／邏輯跳躍／疑似 ASR 誤轉）。**只診斷、不改寫。**
- **② Skill `/podcast-smooth-transcript`**：吃上一步的輸出，整理成通順口語講稿；補銜接但不新增講者沒講的內容，真缺的環節標 `〔需補〕`、疑似誤字標 `〔待確認〕`。

> 外部 API 的 prompt 正本附在 skill 檔末附錄：`tool-boxes/claude-code-commands/podcast-smooth-transcript.md`（GitHub: `Andrewnplus/claude-code-commands`）。
>
> 區別：本流程出**口語講稿**（為重講／發布逐字稿）；要進一步寫成**書面文章**請改接 `/transcribe-to-article`。

> 注意：院外門徒路的原始素材（封面 png、企劃 docx、計畫表 xlsx）仍在
> `~/workspace/andrew/documents/podcasts/院外門徒路/`，未納入本 repo。
