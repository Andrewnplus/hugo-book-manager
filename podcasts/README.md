# Podcasts

恩普拉氏 與 院外門徒路 兩個 Podcast 頻道的**規劃文件**與**逐字稿工具**。

> 音訊剪輯與母帶處理（去空白、降噪、響度）改在 **DaVinci Resolve 手動完成** ——
> 腳本式自動處理對語音備忘錄這類來源容易越修越糟，故已移除。流程仍見
> `planning/剪輯流程與工具指南.md`。本 repo 在音訊端只保留「逐字稿」這支離線工具。

## 目錄

```
podcasts/
├── planning/                   # 頻道規劃、書單系列地圖、錄製佇列、切分藍圖、剪輯指南…
└── scripts/
    └── transcribe.sh           # 產中文逐字稿/字幕（whisper.cpp + opencc 簡轉繁）
```

## macOS 安裝（逐字稿用）

```bash
brew install ffmpeg whisper-cpp opencc
# whisper 模型：下載 ggml-large-v3.bin 放到 ~/.whisper-models/
#   curl -L -o ~/.whisper-models/ggml-large-v3.bin \
#     https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-large-v3.bin
```

## 逐字稿用法

```bash
chmod +x scripts/transcribe.sh        # 首次（git 已保留可執行權限，clone 後通常免）

scripts/transcribe.sh  edited.wav     # → edited.srt（字幕）/ edited.txt（文字稿，繁體）
```

腳本開頭有可用環境變數說明（如 `LANG_CODE=en`、`CONVERT_TW=0` 關閉簡轉繁、`OPENCC_CONFIG`、`MODEL`）。

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
