# CalcApp — レトロゲーム風 Android 計算機アプリ

> Kotlin + Material Design 3 で作る、Press Start 2P フォントのピクセルデザイン計算機。

<!-- スクリーンショット（後で差し替え） -->
<!-- <img width="xxx" height="xxx" alt="CalcApp" src="スクリーンショットURLをここに" /> -->

---

## 概要

**CalcApp** は、レトロゲーム風のビジュアルを持つ Android 計算機アプリです。

- Press Start 2P フォントとピクセルデザインでレトロゲーム感を演出
- MVVM アーキテクチャ（ViewModel + StateFlow）で堅牢な状態管理
- 計算履歴を最大 20 件保持し、スワイプで削除可能
- ダークテーマ対応（システム設定に追従）
- JUnit 4 による 37 件のユニットテストで品質を担保

---

## 機能

| 機能 | 説明 |
|------|------|
| 四則演算 | 加減乗除、連続計算対応 |
| 計算履歴 | 最大 20 件、スワイプで削除 |
| DEL ボタン | 1 文字ずつバックスペース |
| パーセント / 符号切替 | `%` と `+/-` ボタン |
| ダークテーマ | システム設定に追従 |

---

## 技術スタック

| カテゴリ | 技術 |
|----------|------|
| 言語 | Kotlin |
| アーキテクチャ | MVVM (ViewModel + StateFlow) |
| UI | XML Layout, ViewBinding, RecyclerView |
| デザイン | Material Design 3, Press Start 2P フォント |
| ビルド | Gradle 8.10.2 |
| 対応 SDK | minSdk 24 / targetSdk 34 |
| テスト | JUnit 4 (37 ケース) |

---

## セットアップ

### 必要環境

- Android Studio Hedgehog 以降
- JDK 17 以上
- Android 7.0 (API 24) 以上のデバイスまたはエミュレータ

### インストール・実行手順

```bash
# 1. リポジトリをクローン
git clone https://github.com/unoharu/AndroidCalcApp.git
cd AndroidCalcApp
```

**2. Android Studio でプロジェクトを開く**
- Android Studio を起動 → `Open` → クローン先フォルダを選択

**3. ビルド & 実行**
- デバイスまたはエミュレータを接続・起動
- `Run > Run 'app'`（または ▶ ボタン）で実機／エミュレータに転送

**4. ユニットテスト実行（任意）**

```bash
./gradlew test
```

---

## 実行デモ

<!-- デモ画像（後で差し替え） -->
<!-- ![CalcApp Demo](デモ画像URLをここに) -->
