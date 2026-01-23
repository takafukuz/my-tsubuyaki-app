# my-tsubuyaki-app

ユーザーが「つぶやき」を投稿できるシンプルなWebアプリケーションです。

書籍「スッキリわかるサーブレット＆JSP入門（第4版）」（インプレス社）の題材をもとに、
パスワードのハッシュ化機能やユーザー情報変更機能、管理者向け画面などを独自に追加・拡張しました。

フレームワークを使用せず Servlet/JSP のみでアプリケーション全体をスクラッチ構築したものです。

Web アプリケーション開発の全体像を意識し、GitHub への push をトリガーに、  
Maven によるビルド → Docker イメージ作成（nginx＋Tomcat） →  
AWS ECS（Fargate）への自動デプロイまで行う CI/CD パイプラインを構築しています（.github/workflows/main.yaml）。

稼働環境では、ALBおよびElastiCache（Valkey・セッション管理）を利用し、スケールアウト可能な実務に近い環境を再現しています。

## 概要
- 言語: Java 21（Servlet / JSP / JSTL）
- ビルド: Maven
- 実行環境: Tomcat 10（ElastiCache接続用にRedissonライブラリを利用）
- インフラ: Docker, AWS ECS(Fargate), ALB, ElastiCache（Valkey）, RDS(MySQL), SecretsManager, ACM(TLS証明書)
- CI/CD: GitHub Actions

## Web サイトの利用方法

- ユーザーサイト ログイン URL

  https://my-tsubuyaki-app.takafukuz.dev/

- 管理サイト ログイン URL

  https://my-tsubuyaki-app.takafukuz.dev/admin/

- デモ用ログイン情報（ユーザーサイト・管理サイト共通）

  ユーザー名：demouser01
  パスワード：Demouser#2026


  ユーザー名：demouser02
  パスワード：Demouser#2026
  

  ユーザー名：demouser03
  パスワード：Demouser#2026
  
  ※ご自由にご操作いただけますが、個人情報や機密情報は登録しないようご注意ください。
    なお、デモ環境のため、パスワードを変更された場合は、元の内容にお戻しいただけますと幸いです。

## インフラ構成
「tsubuyaki_インフラ構成図.png」を参照

## 画面遷移図
「tsubuyaki_画面遷移図.pdf」を参照

## アーキテクチャ概要
「tsubuyaki_ユーザーサイト_アーキテクチャ概要図.pdf」および
「tsubuyaki_管理サイト_アーキテクチャ概要図.pdf」を参照

## プロジェクト構成
各フォルダごとにどのような種類のクラスが入っているかを示したものです。

管理ページの JSP は `WEB-INF/jsp/admin/` にまとめています。

```
my-tsubuyaki-app/
├─ pom.xml
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ common/           (共通ユーティリティ　- DB 結果型)
│  │  │  ├─ dao/              (データベースアクセスオブジェクト - DAO: ユーザー/つぶやき操作)
│  │  │  ├─ entity/           (エンティティ / DTO - セッション保存オブジェクトやフォームデータ)
│  │  │  ├─ filter/           (フィルタ - ログイン状態確認)
│  │  │  ├─ infrastructure/   (インフラ層ユーティリティ - DB 接続／Secrets 管理)
│  │  │  ├─ listener/         (リスナー - DB ドライバ読込)
│  │  │  ├─ model/            (ビジネスロジック)
│  │  │  └─ servlet/          (サーブレット)
│  │  └─ webapp/
│  │     ├─ WEB-INF/
│  │     │  └─ jsp/           (ユーザー画面向け JSP: ログイン、メイン、ユーザー情報変更 等)
│  │     │     └─ admin/      (管理画面向け JSP: ログイン、ユーザーの一覧・追加・編集・削除・確認 等)
│  │     └─ css/              (スタイルシート: 管理用と一般用の CSS)
│  └─ test/              (ユニットテスト)
└─ target/               (ビルド成果物)
```

## ソースファイル概要 (src/main/java)

以下は `src/main/java` 配下の主要なクラスとその簡単な説明です。

- package `common`
  - `DbOpeResult` - データベース操作の結果を表す列挙型（SUCCESS / DUPLICATE / NOT_FOUND / ERROR）。

- package `dao` (データベースアクセスオブジェクト)
  - `AdminUsersDAO` - （管理画面）ユーザー関連の DB 操作（一覧取得、追加、更新、削除、認証情報取得）。
  - `MuttersDAO` - （ユーザー画面）つぶやき（Mutter）に関する DB 操作（一覧取得、追加）。
  - `UsersDAO` - （ユーザー画面）通常ユーザーのパスワードやユーザー名更新に関する DB 操作。

- package `entity` (データ保持オブジェクト / DTO)
  - `AdminLoginUser` - 管理画面用ログイン情報（セッション保存用）。
  - `AuthInfo` - 認証情報。
  - `LoginUser` - ユーザー画面用ログイン情報（セッション保存用）。
  - `Mutter` - つぶやき（投稿）情報。
  - `NewUserForm` - 新規ユーザー作成フォームからの入力データ。
  - `NewUserInfo` - 新規ユーザーを DB に保存するための情報（ハッシュ化済パスワード、ソルト含む）。
  - `UserInfo` - ユーザー情報。

- package `filter`
  - `LoginStateFilter` - リクエストごとにログイン状態をチェックし、未ログインの場合は適切なログインページへリダイレクトするフィルタ。

- package `infrastructure`
  - `ConnectionFactory` - DB 接続を取得するユーティリティ（SecretsManager から接続情報を取得して DriverManager による接続を提供）。
  - `SecretsManagerUtil` - AWS Secrets Manager からシークレット文字列を取得するヘルパー。

- package `listener`
  - `MyTsubuyakiAppListener` - アプリケーション起動時に DB ドライバをロードするリスナー。

- package `model` (ビジネスロジック)
  - `AdminLoginLogic` - （管理画面）管理者ログインの認証ロジック（PBKDF2 によるパスワード検証）。
  - `AdminUserLogic` - （管理画面）ユーザー操作ロジック（一覧取得、新規追加、削除など）。
  - `LoginLogic` - （ユーザー画面）ログイン認証ロジック（PBKDF2 検証）。
  - `MutterLogic` - （ユーザー画面）つぶやき関連の業務ロジック（一覧取得、投稿処理）。
  - `UpdateUserInfoLogic` - （ユーザー画面）ユーザー情報更新（パスワードハッシュ化、ユーザー名変更）を扱うロジック。

- package `servlet` (HTTP エンドポイント / コントローラ)
  - `AdminLoginServlet` - （管理画面）ログインページの表示と認証処理を行うサーブレット。
  - `AdminLogoutServlet` - （管理画面）ログアウトを処理しセッションを破棄するサーブレット。
  - `AdminMainServlet` - （管理画面）メイン画面（ユーザー一覧）を表示するサーブレット。
  - `AdminAddUserServlet` - （管理画面）ユーザー追加フォームを表示するサーブレット。
  - `AdminAddUserExecServlet` - （管理画面）ユーザー追加処理（DB 登録）を実行するサーブレット。
  - `AdminEditUserServlet` - （管理画面）ユーザー編集フォームを表示するサーブレット。
  - `AdminEditUserConfirmServlet` - （管理画面）編集内容の確認画面へ渡すサーブレット。
  - `AdminEditUserExecServlet` - （管理画面）ユーザー編集の確定（DB 更新）を行うサーブレット。
  - `AdminDelUserServlet` - （管理画面）ユーザー削除の確認一覧を表示するサーブレット。
  - `AdminDelUserExecServlet` - （管理画面）選択ユーザーの削除を実行するサーブレット。
  - `LoginServlet` - （ユーザー画面）ログイン表示と認証処理を行うサーブレット。
  - `LogoutServlet` - （ユーザー画面）ログアウト処理を行うサーブレット。
  - `MainServlet` - （ユーザー画面）メインページ（つぶやき一覧の表示・投稿処理）を扱うサーブレット。
  - `ChangeUserInfoServlet` - （ユーザー画面）ユーザー名変更のフォーム表示と処理を担当するサーブレット。
  - `ChangePasswordServlet` - （ユーザー画面）パスワード変更フォームの表示と更新処理を担当するサーブレット。


## CI / GitHub Actions

このリポジトリには GitHub Actions のワークフロー定義が含まれており、`.github/workflows/main.yaml` が CI パイプラインを構成しています。主な特徴と流れを以下にまとめます。

- トリガー
  - `main` ブランチへの push でワークフローが実行されます。

- 実行環境
  - GitHub のホストランナー (`ubuntu-latest`) 上で実行されます。
  - JDK 21 がセットアップされ、Maven を使ったビルド／テストが行われます。

- 主なステップ（順序）
  1. リポジトリをチェックアウト（actions/checkout）
  2. JDK 21 をセットアップ（actions/setup-java）
  3. mvn clean test によるテスト実行
  4. テストレポートをアーティファクトとして保存（actions/upload-artifact）
  5. mvn package による WAR ビルド
  6. WAR をアーティファクトとして保存（確認用）
  7. Docker イメージ（Dockerfile.nginx / Dockerfile.tomcat）をビルド
  8. AWS 認証情報を設定し ECR にログイン
  9. ビルドしたイメージを ECR に push
  10. ECS タスク定義を Tomcat → nginx の順に更新
  11. 更新後のタスク定義をもとに ECS サービスを更新（本番環境デプロイ）

- アーティファクト
  - テスト結果（`target/surefire-reports/`）が `test-reports` として保存されます。
  - ビルドした WAR（`target/${{ env.APP_NAME }}.war`）が `my-tsubuyaki-app-war` として保存されます。

- Docker / レジストリ
  - ワークフロー内で Docker イメージをビルドし、AWS ECR（`${{ secrets.AWS_ECR_REPO }}`）へ push します。

- 使用している環境変数（`env`）
  - `AWS_REGION`（例: `ap-northeast-1`）
  - `APP_NAME`（`my-tsubuyaki-app`）
  - `WEB_NAME`（`my-nginx`）

- 必要なリポジトリシークレット
  ワークフロー実行に必要なシークレットはリポジトリの Settings > Secrets に登録しています。主に以下が参照されます：
  - `AWS_ACCESS_KEY_ID`（AWS のアクセスキー）
  - `AWS_SECRET_ACCESS_KEY`（AWS のシークレットキー）
  - `AWS_ECR_REPO`（ECR リポジトリ URL / 接続先。例: `123456789012.dkr.ecr.ap-northeast-1.amazonaws.com`）

## 今後の課題

プログラミング技術の向上と AWS サービスへの理解を深めるため、以下の改善に取り組む予定です。

- アプリケーション関連
  - ユーザーサイト
    - つぶやき削除機能(済)
    - ページネーション
    - 検索機能
    - UI改善
    
  - 管理サイト
    - ユーザー管理
      - パスワード変更
      - 検索機能
    - つぶやき管理
      - つぶやき削除
      - 検索機能
      
  - 共通
    - 入力データのバリデーション強化
    - CSRF対策

- インフラ関連
  - DynamoDBへの変更
  - CDKによる構成管理
  
---

**最終更新**: 2026年1月23日