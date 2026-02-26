# AndroidCalcApp 改善 TODO

## Phase 1: 土台整備

- [x] 新ディレクトリ構造 `com/unoharu/androidcalcapp/` を3箇所に作成
- [x] MainActivity.kt — パッケージ変更 + Kotlin規約準拠（定数命名、Log.d削除、コメント整理）
- [x] ExampleUnitTest.kt — パッケージ変更
- [x] ExampleInstrumentedTest.kt — パッケージ変更 + assertEquals文字列更新
- [x] build.gradle.kts — namespace, applicationId 変更
- [x] AndroidManifest.xml — テーマ参照を `Theme.CalcApp` に変更
- [x] strings.xml — アプリ名を `CalcApp` に変更
- [x] themes.xml（通常 + night）— テーマ名を `Base.Theme.CalcApp` / `Theme.CalcApp` に変更
- [x] 旧ディレクトリ `com/ih13a213_unoharu/` を3箇所削除
- [x] grepで旧パッケージ名の残存なしを確認
- [x] `./gradlew assembleDebug` ビルド成功
- [x] `./gradlew test` テスト成功
- [x] Qiita.md に Phase 1 の変更を追記

## Phase 2: コード品質

- [x] ViewBindingの導入（`findViewById` 全廃止）
- [x] 計算ロジックの分離（`Calculator.kt` — 純粋Kotlin、Android依存なし）
- [x] MVVM + ViewModel導入（`CalculatorViewModel.kt` + `StateFlow`）
- [x] バグ修正: 演算子条件式の優先度（`toDoubleOrNull()` バリデーションに置換）
- [x] バグ修正: ゼロ除算で `CalculationResult.Error` を返し、Toast でエラーメッセージ表示
- [x] バグ修正: 右スワイプ無効化（左スワイプのみ backspace、最小距離 50px）
- [x] ユニットテスト追加（`CalculatorTest.kt` — 22テストケース）
- [x] ボイラープレート `ExampleUnitTest.kt` 削除
- [x] `./gradlew assembleDebug` ビルド成功
- [x] `./gradlew test` 全テストパス
- [x] `findViewById` 残存なし確認
- [x] Qiita.md に Phase 2 の変更を追記

## Phase 3: レトロゲーム風デザイン刷新

- [x] Press Start 2P フォント配置（`res/font/press_start_2p.ttf`）
- [x] カラーパレット定義: ライトテーマ16色（`values/colors.xml`）
- [x] カラーパレット定義: ダークテーマ16色（`values-night/colors.xml`）
- [x] Drawable作成: ディスプレイ背景（`display_background.xml`）
- [x] Drawable作成: ボタン4種 × 3ファイル（normal, pressed, selector）= 12ファイル
- [x] テーマ更新: windowBackground, statusBarColor, navigationBarColor 設定
- [x] スタイル追加: buttonStyleNumber, buttonStyleOperator, buttonStyleFunction, buttonStyleEquals
- [x] スタイル追加: displayStyle（autoSizeText, ピクセルフォント）
- [x] ダークテーマ: `values-night/themes.xml` にテーマ属性Override
- [x] レイアウト更新: ボタンに種別スタイル割当、高さ0dp化、padding/margin調整
- [x] `./gradlew assembleDebug` ビルド成功
- [x] `./gradlew test` 全テストパス（ロジック変更なし）
- [x] Qiita.md に Phase 3 の変更を追記

## Phase 4: 機能追加

- [x] 連続計算サポート（`inputOperator()` で中間計算を実行）
- [x] 計算履歴（`HistoryItem` + RecyclerView、最大20件、ディスプレイ上部に表示）
- [x] バックスペースボタン（DEL ボタン、GridLayout Row 5 に4列スパンで配置）
- [x] 画面回転時の状態保持（Phase 2 の ViewModel 導入で既に対応済み）
- [x] ユニットテスト追加（連続計算、履歴、setInput — 計31テストケース）
- [x] `./gradlew assembleDebug` ビルド成功
- [x] `./gradlew test` 全テストパス
