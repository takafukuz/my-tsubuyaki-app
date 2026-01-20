# my-tsubuyaki-app

ユーザーが「つぶやき」を投稿できるシンプルなWebアプリケーションです。

書籍「スッキリわかるサーブレット＆JSP入門（第4版）」（インプレス社）の題材をベースに、
ユーザー管理機能や管理者向け画面などを独自に追加・拡張しました。

また GitHub への push をトリガーに、  
Maven によるビルド → Docker イメージ作成（nginx＋Tomcat） →  
AWS ECS（Fargate）への自動デプロイまで行う CI/CD パイプラインを構築しています。

稼働環境では、ALBおよびElastiCache（Valkey・セッション管理）を利用し、スケールアウト可能な実務に近い環境を再現しています。

## 概要
- 言語: Java 21（Servlet / JSP / JSTL）
- ビルド: Maven
- 実行環境: Tomcat 10
- インフラ: Docker, AWS ECS(Fargate), ALB, ElastiCache（Valkey）
- CI/CD: GitHub Actions


## 目次
- 前提条件
- ビルドと実行（ローカル）
- Tomcat にデプロイする手順（Windows / cmd）
- プロジェクト構成の簡易マップ
- よくある問題と対処
- 開発のヒント
- CI / GitHub Actions

---

## 前提条件
- JDK 17 以上（プロジェクトの pom.xml を確認してください）
- Maven（3.x）
- Tomcat（9/10 など）またはその他の Servlet コンテナ

## ビルドと実行（ローカル）
Maven でパッケージを作成します。Windows の cmd.exe の例:

```bat
cd C:\my_project\my-tsubuyaki-app
mvn clean package
```

生成された WAR は `target/` に出力されます。ローカルの Tomcat に手動で配置するか、開発用には IDE の Server 機能（Eclipse 等）を利用してください。

### テスト
Maven のユニットテストを実行する場合:

```bat
cd C:\my_project\my-tsubuyaki-app
mvn test
```

（プロジェクトにテストがある場合に実行されます）

## Tomcat にデプロイする手順（Windows / cmd）
1. Tomcat を停止（既に起動している場合）:

```bat
"%CATALINA_HOME%\bin\shutdown.bat"
```

2. ビルドして WAR を生成:

```bat
cd C:\my_project\my-tsubuyaki-app
mvn clean package
```

3. 生成された WAR を Tomcat の webapps 配下へコピー（例）:

```bat
copy target\my-tsubuyaki-app.war "%CATALINA_HOME%\webapps\"
```

4. Tomcat を起動:

```bat
"%CATALINA_HOME%\bin\startup.bat"
```

5. ブラウザでアクセス:

```
http://localhost:8080/my-tsubuyaki-app/
```

※ context 名（`my-tsubuyaki-app`）は WAR 名や Tomcat 設定によって変わります。

## プロジェクト構成の簡易マップ
（各フォルダ内にある "ファイルの種類" を一階層深く示します — ファイル名は省略しています）

```
my-tsubuyaki-app/
├─ pom.xml
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ common/           (共通列挙型・ユーティリティ、共通定数や DB 結果型)
│  │  │  ├─ dao/              (データベースアクセスオブジェクト - DAO: ユーザー/つぶやき操作)
│  │  │  ├─ entity/           (エンティティ / DTO - セッション保存オブジェクトやフォームデータ)
│  │  │  ├─ filter/           (Servlet フィルタ - 認証やリクエスト制御)
│  │  │  ├─ infrastructure/    (インフラ層ユーティリティ - DB 接続／Secrets 管理)
│  │  │  ├─ listener/         (ServletContextListener 等の起動処理)
│  │  │  ├─ model/            (ビジネスロジック / サービス層)
│  │  │  └─ servlet/          (HTTP コントローラ / サーブレット)
│  │  └─ webapp/
│  │     ├─ WEB-INF/
│  │     │  └─ jsp/
│  │     │     ├─ admin/      (管理画面向け JSP 群: 一覧・追加・編集・削除・確認・ログイン 等)
│  │     │     ├─ user/       (一般ユーザー向け JSP: ログイン、メイン、変更フォーム 等)
│  │     │     └─ shared/     (共通パーツやレイアウト用 JSP — ここでは代表的な JSP を格納)
│  │     ├─ css/              (スタイルシート: 管理用と一般用の CSS)
│  │     └─ public/           (静的アセット: 画像 / JS / その他の公開リソース)
│  └─ test/              (ユニットテスト)
└─ target/               (ビルド成果物)
```

上記はフォルダごとにどのような "種類のクラス／ファイル" が入っているかを示したものです。ファイル名は README では省略しています。

管理ページの JSP は `WEB-INF/jsp/admin/` にまとまっています（上記に代表的な JSP を列挙）。

## よくある問題と対処
- CSS や JSP の変更が反映されない
  - ブラウザのキャッシュ（Ctrl+F5）を消す。
  - Tomcat を再デプロイ／再起動する。
- JSTL / タグライブラリのエラー
  - pom.xml に依存があるか、Tomcat の lib に配置されているかを確認してください（jakarta タグの使用に注意）。
- 500 エラーが出る
  - Tomcat のログ（`%CATALINA_HOME%\logs\catalina.out` 等）を確認して、スタックトレースを確認する。

## 開発のヒント
- JSP の編集は `src/main/webapp/WEB-INF/jsp/` 配下で行い、都度ブラウザで確認してください。
- 共通 CSS は `src/main/webapp/css/admin-style.css` を編集して全体の見た目を調整します。
- ログアウトなどのリンクは管理用の Servlet マッピングに従っています。既存のコントローラ（Servlet）を参照してください。

## CI / GitHub Actions

このリポジトリには GitHub Actions のワークフロー定義が含まれており、`.github/workflows/main.yaml` が CI パイプラインを構成しています。主な特徴と流れを以下にまとめます。

- トリガー
  - `main` ブランチへの push でワークフローが実行されます。

- 実行環境
  - GitHub のホストランナー (`ubuntu-latest`) 上で実行されます。
  - JDK 21 がセットアップされ、Maven を使ったビルド／テストが行われます。

- 主なステップ（順序）
  1. リポジトリをチェックアウト（`actions/checkout`）
  2. JDK 21 をセットアップ（`actions/setup-java`）
  3. `mvn clean test` によるテスト実行
  4. テストレポートをアーティファクトとして保存（`actions/upload-artifact`）
  5. `mvn package` による WAR ビルド
  6. WAR をアーティファクトとして保存
  7. Docker イメージ（`Dockerfile.nginx` と `Dockerfile.tomcat`）をビルド
  8. AWS の認証情報を設定して ECR にログイン
  9. ビルドしたイメージを AWS ECR に push

- アーティファクト
  - テスト結果（`target/surefire-reports/`）が `test-reports` として保存されます。
  - ビルドした WAR（`target/${{ env.APP_NAME }}.war`）が `my-tsubuyaki-app-war` として保存されます。

- Docker / レジストリ
  - ワークフロー内で Docker イメージをビルドし、AWS ECR（`${{ secrets.AWS_ECR_REPO }}`）へ push します。
  - 以前は GHCR（GitHub Container Registry）へ push するステップがコメントアウトされており、現在は ECR を使う構成になっています。

- 使用している環境変数（`env`）
  - `AWS_REGION`（例: `ap-northeast-1`）
  - `APP_NAME`（`my-tsubuyaki-app`）
  - `WEB_NAME`（`my-nginx`）

- 必要なリポジトリシークレット
  ワークフロー実行に必要なシークレットはリポジトリの Settings > Secrets に登録しておく必要があります。主に以下が参照されます：
  - `AWS_ACCESS_KEY_ID`（AWS のアクセスキー）
  - `AWS_SECRET_ACCESS_KEY`（AWS のシークレットキー）
  - `AWS_ECR_REPO`（ECR リポジトリ URL / 接続先。例: `123456789012.dkr.ecr.ap-northeast-1.amazonaws.com`）
  - （コメントされているが）`GHCR_PAT` など GitHub Container Registry 用のトークンを使う場合は `GHCR_PAT` を設定すること

- 注意点 / 運用メモ
  - ワークフローは本番（`production`）向けの `environment` を指定して実行されます。デプロイ前に適切な環境保護ルールがないか確認してください。
  - ECR に push するために `aws-actions/configure-aws-credentials` を利用しており、正しい AWS 権限（ECR への push / ECR 認証）が必要です。
  - ランナーで Docker ビルドを行うため、ビルドの実行に時間がかかる場合があります。不要なイメージビルドや push はコメントアウトして運用できます。
  - 機密情報（アクセスキー等）は必ず GitHub Secrets に格納し、ワークフロー内で直接埋め込まないでください。

- ローカルで試す場合
  - ワークフローと同等の手順はローカル環境でも実行可能です（JDK 21 + Maven + Docker が必要）。ただし GitHub Actions 固有の `${{ github.run_number }}` や `secrets` は手動で差し替えてください。
  - `act`（サードパーティ製ツール）で GitHub Actions をローカル実行できますが、環境差異に注意してください。

このセクションは `.github/workflows/main.yaml` の現在の内容（実行時点: 2026-01-20）を元に作成しています。ワークフローを変更した場合は README の記載も合わせて更新してください。

## ソースファイル概要 (src/main/java)

以下は `src/main/java` 配下の主要なクラスとその簡単な説明です。開発者がコードの役割を把握するための参照用です。

- package `common`
  - `DbOpeResult` - データベース操作の結果を表す列挙型（SUCCESS / DUPLICATE / NOT_FOUND / ERROR）。

- package `dao` (データベースアクセスオブジェクト)
  - `AdminUsersDAO` - 管理者向けユーザー関連の DB 操作（一覧取得、追加、更新、削除、認証情報取得）。
  - `MuttersDAO` - つぶやき（Mutter）に関する DB 操作（一覧取得、追加）。
  - `UsersDAO` - 通常ユーザーのパスワードやユーザー名更新に関する DB 操作。

- package `entity` (データ保持オブジェクト / DTO)
  - `AdminLoginUser` - 管理者ログイン時にセッションに保持する最小限の情報（userId / userName）。
  - `AuthInfo` - 認証に必要な情報（password hash、salt、userId）。
  - `LoginUser` - ユーザー用ログイン情報（セッション保存用）。
  - `Mutter` - つぶやき（投稿）のエンティティ（id, userId, userName, text, createdAt）。
  - `NewUserForm` - 新規ユーザー作成フォームからの入力データを格納する DTO。
  - `NewUserInfo` - 新規ユーザーを DB に保存するための情報（ハッシュ化済パスワード、ソルト含む）。
  - `UserInfo` - ユーザー情報（userId, userName, adminPriv）。

- package `filter`
  - `LoginStateFilter` - リクエストごとにログイン状態をチェックし、未ログインの場合は適切なログインページへリダイレクトするフィルタ。

- package `infrastructure`
  - `ConnectionFactory` - DB 接続を取得するユーティリティ（SecretsManager から接続情報を取得して DriverManager による接続を提供）。
  - `SecretsManagerUtil` - AWS Secrets Manager からシークレット文字列を取得するヘルパー。

- package `listener`
  - `MyTsubuyakiAppListener` - アプリケーション起動時に DB ドライバをロードする ServletContextListener。

- package `model` (ビジネスロジック)
  - `AdminLoginLogic` - 管理者ログインの認証ロジック（PBKDF2 によるパスワード検証）。
  - `AdminUserLogic` - 管理者用のユーザー操作ロジック（一覧取得、新規追加、削除など）。
  - `LoginLogic` - 通常ユーザーのログイン認証ロジック（PBKDF2 検証）。
  - `MutterLogic` - つぶやき関連の業務ロジック（一覧取得、投稿処理）。
  - `UpdateUserInfoLogic` - ユーザー情報更新（パスワードハッシュ化、ユーザー名変更）を扱うロジック。

- package `servlet` (HTTP エンドポイント / コントローラ)
  - `AdminLoginServlet` - 管理画面ログインページの表示と認証処理を行うサーブレット。
  - `AdminLogoutServlet` - 管理者ログアウトを処理しセッションを破棄するサーブレット。
  - `AdminMainServlet` - 管理者トップ（ユーザー一覧）を表示するサーブレット。
  - `AdminAddUserServlet` - 管理者によるユーザー追加フォームを表示するサーブレット。
  - `AdminAddUserExecServlet` - ユーザー追加処理（DB 登録）を実行するサーブレット。
  - `AdminEditUserServlet` - ユーザー編集フォームを表示するサーブレット。
  - `AdminEditUserConfirmServlet` - 編集内容の確認画面へ渡すサーブレット。
  - `AdminEditUserExecServlet` - ユーザー編集の確定（DB 更新）を行うサーブレット。
  - `AdminDelUserServlet` - ユーザー削除の確認一覧を表示するサーブレット。
  - `AdminDelUserExecServlet` - 選択ユーザーの削除を実行するサーブレット。
  - `LoginServlet` - 通常ユーザーのログイン表示と認証処理を行うサーブレット。
  - `LogoutServlet` - 通常ユーザーのログアウト処理を行うサーブレット。
  - `MainServlet` - 一般ユーザー向けのメインページ（つぶやき一覧の表示・投稿処理）を扱うサーブレット。
  - `ChangeUserInfoServlet` - ユーザー名変更のフォーム表示と処理を担当するサーブレット。
  - `ChangePasswordServlet` - パスワード変更フォームの表示と更新処理を担当するサーブレット。

## 貢献・連絡
小さな学習プロジェクトのため、変更は自由です。重大な変更を意図する場合はソースのバックアップを取り、動作確認を行ってください。

---

必要なら、README に次の追加を作成します：
- 詳細な開発セットアップ手順（Eclipse / Tomcat 統合、環境変数設定）
- よく使うコマンドのショートカットスクリプト（Windows bat）
- プロジェクト固有の環境変数やプロパティの説明

ご希望があれば追加で書き込みます。