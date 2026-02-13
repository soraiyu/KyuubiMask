# コントリビューション ガイド

KyuubiMask プロジェクトへの関心をお寄せいただき、ありがとうございます！ 🦊

## 質問・相談について

### アプリについて相談したい場合

作りたいアプリや機能についてご相談されたい場合は、以下の方法でお気軽にお問い合わせください：

1. **GitHubのIssueを作成する**
   - [Issues ページ](https://github.com/soraiyu/KyuubiMask/issues)にアクセス
   - 「New Issue」をクリック
   - アプリのアイデアや相談内容を記載してください

2. **Discussionsを利用する**
   - GitHub Discussionsで質問や提案を投稿できます
   - アイデアの共有や意見交換に最適です

### どのような相談でも歓迎します

- 新機能のアイデア
- 既存機能の改善提案
- 使い方に関する質問
- プライバシーに関する懸念事項
- 技術的な質問

## コントリビューションの方法

### バグ報告

バグを見つけた場合：

1. [Issues](https://github.com/soraiyu/KyuubiMask/issues)で既存のバグ報告を確認
2. 新しいIssueを作成し、以下を含める：
   - バグの詳細な説明
   - 再現手順
   - 期待される動作
   - 実際の動作
   - 環境情報（Android バージョンなど）

### 機能リクエスト

新しい機能を提案する場合：

1. [Issues](https://github.com/soraiyu/KyuubiMask/issues)を作成
2. 機能の詳細な説明
3. ユースケース（なぜその機能が必要か）
4. 可能であれば、実装のアイデア

### プルリクエスト

コードの貢献を歓迎します：

1. リポジトリをフォーク
2. 機能ブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. プルリクエストを作成

#### コーディング規約

- Kotlin のコーディングスタイルに従う
- 既存のコードスタイルを尊重する
- 変更には適切なコメントを含める

## 開発環境のセットアップ

```bash
# リポジトリをクローン
git clone https://github.com/soraiyu/KyuubiMask.git
cd KyuubiMask

# ビルド
./gradlew assembleDebug
```

## プライバシーとセキュリティ

KyuubiMaskはプライバシーを重視しています：

- インターネット権限を追加しない
- ユーザーデータを保存・送信しない
- 完全オフライン動作を維持する

これらの原則に違反する変更は受け入れられません。

## 質問がある場合

どんな質問でもお気軽にどうぞ！Issueを作成するか、既存のIssueにコメントしてください。

私たちは、すべてのコントリビューターを歓迎し、サポートいたします。

---

## English (for non-Japanese speakers)

### Questions and Consultations

Feel free to ask questions or discuss app ideas:

1. **Create a GitHub Issue**
   - Visit the [Issues page](https://github.com/soraiyu/KyuubiMask/issues)
   - Click "New Issue"
   - Describe your idea or question

2. **Use GitHub Discussions**
   - Share ideas and exchange opinions

We welcome:
- Feature ideas
- Improvement suggestions
- Usage questions
- Privacy concerns
- Technical questions

For more details, see the Japanese section above (using translation tools if needed).
