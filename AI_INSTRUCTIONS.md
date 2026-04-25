# AI_INSTRUCTIONS.md

## アーキテクチャの基本方針
- 本プロジェクトではシンプルなレイヤードアーキテクチャを採用してください。
- 依存性の方向は `controller` -> `service` -> `repository` -> `infrastructure` の単方向とします。
- 重複コードはなるべく排除し、過度な抽象化は避けてください。
- 各レイヤーのクラスはインターフェースを作らず、具体的なクラスとして実装してください。

## パッケージ構成（Package by Layer）
トップレベルに以下のパッケージを配置してください（例: `jp.eno314.vcu.pdate.ruler.sample.controller`）。

- `controller`: エントリポイントの定義とServiceの呼び出しのみを行う。
- `service`: ビジネスロジック、バリデーション、データの変換（Request -> Dto, Dto -> Response）を行う。
- `repository`: `infrastructure` のクライアントを呼び出し、データの変換（Dto -> RemoteRequest, RemoteResponse -> Dto）を行う。
- `infrastructure`: 外部APIや外部リソースへの具体的なアクセスを行うクライアントクラスを定義する。
- `configuration`: Beanの定義など、DIコンテナの設定を行う独立したレイヤー。

## クラス命名規則とデータフロー
各レイヤーで扱うデータクラスの命名は以下に従ってください。

- **Service層**
  - リクエストデータ: `XxxRequest`
  - レスポンスデータ: `XxxResponse`
  - ※ これらは一般的にDTOと呼ばれるものですが、本プロジェクトでは便宜上これらをビジネスエンティティに近い扱いとします。エントリポイントのリクエスト/レスポンスとしても使用されます。
- **Repository層**
  - データ転送用: `XxxDto`
- **Infrastructure層**
  - 外部APIリクエスト: `XxxRemoteRequest`
  - 外部APIレスポンス: `XxxRemoteResponse`

## バリデーション方針
- 入力値のバリデーションは **`service` 層** で実施してください。
- `XxxRequest` クラスに Spring Validation (`jakarta.validation.constraints`) のアノテーションを付与してください。
- Springのバリデーション機能を有効にするため、サービスクラスに `@org.springframework.validation.annotation.Validated` を付与し、メソッド引数のリクエストクラスに `@jakarta.validation.Valid` を付与してください。
- 相関チェックなどのビジネスロジックを伴うバリデーションは、サービスクラス内またはそのヘルパークラスで実装してください。

## テスト方針
- フレームワーク: **JUnit 5**
- アサーション: **AssertJ**
- モックライブラリ: **MockK**

### ユニットテスト
- 単位: クラス
- 自クラス以外の依存コンポーネントは **MockK** を使ってモック化してください。

### インテグレーションテスト
- 単位: エントリポイント（Controller経由）
- `@SpringBootTest` アノテーションを使用してください。
- インテグレーションテストでは **なるべく MockK は使わず**、本物のコンポーネントを組み合わせてテストしてください。
- 外部API参照部分は **`MockRestServiceServer`** を使ってモック化してください。
