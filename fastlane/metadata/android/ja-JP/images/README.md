# Fastlaneスクリーンショット

このディレクトリにはF-Droid用のスクリーンショットが含まれます。

## ファイルの配置場所

| ファイル/ディレクトリ | 用途 |
|----------------------|------|
| `phoneScreenshots/*.png` | **F-Droidアプリページ** — F-Droidクライアントとウェブサイトに表示されます |
| `featureGraphic.png` | F-Droidアプリページ上部に表示されるバナー画像（任意） |

ここに配置したスクリーンショットは **`README.md` には自動的に表示されません**。  
`README.md` にスクリーンショットを追加するには、リポジトリ内の任意の場所（例: `docs/screenshots/`）に画像を置き、  
相対パスでMarkdownから参照してください：

```markdown
![設定画面](docs/screenshots/settings.png)
```

## ファイル仕様

- **`phoneScreenshots/`**: PNG または JPEG、推奨サイズ 1080×1920 px
- **`featureGraphic.png`**: PNG または JPEG、推奨サイズ 1024×500 px

## ガイドライン
- アプリの主要機能を明確に表示してください
- 個人情報や機密情報を含めないようにしてください
