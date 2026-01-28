package dao;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.DbOpeResult;
import entity.AuthInfo;
import entity.NewUserInfo;
import entity.UserInfo;
import infrastructure.AppConfig;
import infrastructure.DynamoDbClientHolder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.CancellationReason;
import software.amazon.awssdk.services.dynamodb.model.Delete;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;
import software.amazon.awssdk.services.dynamodb.model.Update;

public class AdminUsersDAO {
	
	// DynamoDB接続クライアントを呼び出す
	private final DynamoDbClient dynamoDb = DynamoDbClientHolder.getInstance();
	
	// テーブル名を保持しているAppConfigを呼ぶ
    private final String usersTable;
    private final String usernamesTable;
    // private final String muttersTable;
    
    public AdminUsersDAO() {
        this.usersTable = AppConfig.getInstance().getUsersTable();
        this.usernamesTable = AppConfig.getInstance().getUsernamesTable();
    }

	// usernameから当該ユーザーの登録済パスワードを取得　戻り値password(password_hash)、salt、userid
	public AuthInfo getPassword(String username) {
		
		// 問い合わせ用クエリ
		// GSIを使うのでGetItemではなくscan
		// 結果は、1件もしくは0件の想定なので、limit(1)を付ける
		QueryRequest queryReq = QueryRequest.builder().tableName(usersTable).indexName("username-index")
				.keyConditionExpression("#username = :username")
				.expressionAttributeNames(Map.of("#username","username"))
				.expressionAttributeValues(Map.of(":username", AttributeValue.fromS(username)))
				.limit(1)
				.build();
		
		try {
			List<Map<String, AttributeValue>> items = dynamoDb.query(queryReq).items();
				
			if (items.isEmpty()) {
				return null;
			}
			// 結果の1件目を取得
			Map<String, AttributeValue> item = items.get(0);
			// AuthInfoに入れて返す。
			AuthInfo authInfo = new AuthInfo(item.get("password_hash").s(),item.get("salt").s(),item.get("userid").s());
			return authInfo;
			
		} catch (DynamoDbException e) {
			System.err.println(e.toString());
		    return null;
		}
		
	}
	
	// ユーザー一覧の取得
	public List<UserInfo> getUserList() {
	
		// GSIでソートして一覧取得する
		QueryRequest queryReq = QueryRequest.builder()
				.tableName(usersTable)
				.indexName("entity_type-updated_at-index")
				.keyConditionExpression("#entity_type = :entity_type")
				.expressionAttributeNames(Map.of(
						"#entity_type", "entity_type",
						"#userid", "userid",
						"#username", "username",
						"#adminpriv", "adminpriv"))
				.expressionAttributeValues(Map.of(":entity_type", AttributeValue.fromS("USER")))
				.projectionExpression("#userid, #username, #adminpriv")
				.scanIndexForward(false)
				.limit(1000) //ページネーションするまで暫定上限
				.build();
			
		try {	
			QueryResponse queryRes = dynamoDb.query(queryReq);
			List<Map<String, AttributeValue>> items = queryRes.items();
			
			// 結果がない場合
			if (items.isEmpty()) {
				System.out.println("ユーザー一覧取得の結果が0件です");
				return Collections.emptyList();
			} 
			
			// 結果をUserInfo型のリストに入れ替える

			List<UserInfo> userList = new ArrayList<>();
			for (Map<String, AttributeValue> item: items) {
				UserInfo userInfo = new UserInfo();
				userInfo.setUserId(item.get("userid").s());
				userInfo.setUserName(item.get("username").s());
				userInfo.setAdminPriv(Integer.parseInt(item.get("adminpriv").n()));
				userList.add(userInfo);
			}
			return userList;
		} catch (DynamoDbException e){
			System.err.println(e.toString());
			// エラー時は空のリストを返す。NPE対策
			return Collections.emptyList();
		}
	}
	
	// userIdをもとに、userid、username、adminprivを返す
	public UserInfo getUserInfo(String userId) {
		// GetItemをつかう		
		// 問い合わせ文の作成
		Map<String, AttributeValue> queryKey = Map.of("userid", AttributeValue.fromS(userId));
		GetItemRequest queryReq = GetItemRequest.builder()
				.tableName(usersTable)
				.key(queryKey)
				.projectionExpression("#userid, #username, #adminpriv")
				.expressionAttributeNames(Map.of(
						"#userid", "userid",
						"#username", "username",
						"#adminpriv", "adminpriv"))
				.build();
		
		try {
			Map<String, AttributeValue> item = dynamoDb.getItem(queryReq).item();
			
			if (item.isEmpty()) {
				System.out.println("ユーザー情報取得エラー：" +userId + "は存在しません。");
				return null;
			}
			
			UserInfo userInfo = new UserInfo(
					item.get("userid").s(),
					item.get("username").s(),
					Integer.parseInt(item.get("adminpriv").n())
			);
			
			return userInfo;
			
			
		} catch (DynamoDbException e) {
			System.err.println(e.toString());
			return null;
		}
	}
	
	public DbOpeResult updateUserInfo(UserInfo userInfo) {
		
		// 追加で、usernameが変更された場合、Muttersテーブルのusernameを全件書き換える処理を入れること
		
		String userId = userInfo.getUserId();
		String newUserName = userInfo.getUserName();
		int adminPriv = userInfo.getAdminPriv();
		String currentUserName ;
		
		// username用データ（新username)
		Map<String, AttributeValue> uniqueUserName = Map.of("username", AttributeValue.fromS(newUserName));
		
		// userIdから、現行usernameを取得する
		Map<String, AttributeValue> queryKey = Map.of("userid", AttributeValue.fromS(userId));
		GetItemRequest queryReq = GetItemRequest.builder().tableName(usersTable).key(queryKey).build();

		try {
			Map<String, AttributeValue> queryRes = dynamoDb.getItem(queryReq).item();
			currentUserName = queryRes.get("username").s();
		} catch (DynamoDbException e) {
			System.err.println(e.toString());
			return DbOpeResult.ERROR;
		}
		
	    // usernameが変更されているかどうか、でトランザクションの中身を変えるため
		// トランザクション項目をリストで構築
		String currentTime = Instant.now().toString();
	    List<TransactWriteItem> myTransactItems = new java.util.ArrayList<>();
		
		// usernameが変更されている場合は、usernameテーブルへの追加・削除をおこなう
	    // conditionExpressionのところおかしいかも
		if (!newUserName.equals(currentUserName)) {
			// usernameテーブルに新ユーザー名追加
			myTransactItems.add(
				TransactWriteItem.builder()
					.put(Put.builder()
						.tableName(usernamesTable)
						.item(uniqueUserName)
						.conditionExpression("attribute_not_exists(#username)")
						.expressionAttributeNames(Map.of("#username", "username"))
						.build())
					.build()
			);
			// usernameテーブルから、旧ユーザー名削除
			myTransactItems.add(
					TransactWriteItem.builder()
						.delete(Delete.builder()
							.tableName(usernamesTable)
							.key(Map.of("username", AttributeValue.fromS(currentUserName))).build())
						.build()
			);
		}
		
		// usersテーブルで、username、adminprivのUPDATEは常に行う
		myTransactItems.add(
			TransactWriteItem.builder()
				.update(Update.builder()
					.tableName(usersTable)
					.key(Map.of("userid", AttributeValue.fromS(userId)))
					.updateExpression("SET #username = :username, #adminpriv = :adminpriv, #updated_at = :updated_at")
					.expressionAttributeNames(Map.of(
							"#username", "username",
							"#adminpriv", "adminpriv",
							"#updated_at", "updated_at")
							)
					.expressionAttributeValues(Map.of(
							":username", AttributeValue.fromS(newUserName),
							":adminpriv", AttributeValue.fromN(Integer.toString(adminPriv)),
							":updated_at", AttributeValue.fromS(currentTime))
							)
					.build())
				.build()
		);
		
		try {
			
			dynamoDb.transactWriteItems(TransactWriteItemsRequest.builder().transactItems(myTransactItems).build());
			return DbOpeResult.SUCCESS;
			
		} catch (TransactionCanceledException e){
			System.err.println(e.toString());
			if (e.hasCancellationReasons()) {
				List<CancellationReason> reasons = e.cancellationReasons();
				if (reasons.size() > 0 && "ConditionalCheckFailed".equals(reasons.get(0).code())) {
					return DbOpeResult.DUPLICATE;
				}
			}
			return DbOpeResult.ERROR;
		} catch (DynamoDbException e) {
			return DbOpeResult.ERROR;
		}
		
	}
	
	public DbOpeResult addUser(NewUserInfo newUserInfo) {
		
		String userName = newUserInfo.getUserName();
		int adminPriv = newUserInfo.getAdminPriv();
		String passwordHash = newUserInfo.getPasswordHash();
		String salt = newUserInfo.getSalt();
		
		String userId = java.util.UUID.randomUUID().toString();
		
		// usernames用データ
		Map<String, AttributeValue> uniqueKey = Map.of("username", AttributeValue.fromS(userName));
		
		// users用データ（作成日時を追加）
		String currentTime = Instant.now().toString();
		//System.out.println(createdAt.toString());
		
		Map<String, AttributeValue> item = Map.of(
				"userid", AttributeValue.fromS(userId), 
				"username", AttributeValue.fromS(userName),
				// Java内でintのものは、文字列に変えてから、fromN（数値型）としてDynamoDBにわたす
				"adminpriv", AttributeValue.fromN(Integer.toString(adminPriv)),
				"password_hash", AttributeValue.fromS(passwordHash),
				"salt", AttributeValue.fromS(salt),
				"created_at", AttributeValue.fromS(currentTime),
				"updated_at", AttributeValue.fromS(currentTime),
				"entity_type", AttributeValue.fromS("USER")
				);
		
		// トランザクションの開始
		TransactWriteItemsRequest tx = TransactWriteItemsRequest.builder().transactItems(
				// usernameテーブルにユーザー名登録
				TransactWriteItem.builder()
					.put(Put.builder()
						.tableName(usernamesTable)
						.item(uniqueKey)
						.conditionExpression("attribute_not_exists(#username)")
						.expressionAttributeNames(Map.of("#username", "username"))
						.build())
					.build(),
				// usersテーブルにユーザーIDとユーザー名を登録		
				TransactWriteItem.builder()
					.put(Put.builder()
						.tableName(usersTable)
						.item(item)
						.build())
					.build()
				)
				.build();
		try {
			dynamoDb.transactWriteItems(tx);
			return DbOpeResult.SUCCESS;
		} catch (TransactionCanceledException e){
			System.err.println(e.toString());
			if (e.hasCancellationReasons()) {
				List<CancellationReason> reasons = e.cancellationReasons();
				if (reasons.size() > 0 && "ConditionalCheckFailed".equals(reasons.get(0).code())) {
					return DbOpeResult.DUPLICATE;
				}
			}
			return DbOpeResult.ERROR;
		} catch (DynamoDbException e) {
			return DbOpeResult.ERROR;
		}
	}
	
	public List<UserInfo> findUsersByIds(List<String> userIds) {
		
		// userIdsがnullまたは空の場合は、空のリストを返す。NPE回避。
		if (userIds == null || userIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		// userIdsの要素分、keyのMapを作る。キー名（userId）と値
		List<Map<String, AttributeValue>> keys = new ArrayList<>();
		
		for (String userId : userIds) {
			keys.add(Map.of("userid", AttributeValue.fromS(userId)));
		}
		
		// Map→KAA→itemsを作る
		KeysAndAttributes kaa = KeysAndAttributes.builder().keys(keys).build();
		
		Map<String, KeysAndAttributes> requestItems = new HashMap<>();
		requestItems.put(usersTable, kaa);
		
		// Batchリクエスト準備
		BatchGetItemRequest request = BatchGetItemRequest.builder().requestItems(requestItems).build();
		
		// 結果マージ用Map
		Map<String, List<Map<String, AttributeValue>>> mergedResponses = new HashMap<>();
		
		try {
			BatchGetItemResponse response = dynamoDb.batchGetItem(request);
			
			// 初回の結果をmergedResponseに入れる
			response.responses().forEach((table, items) -> {
				mergedResponses.computeIfAbsent(table, v -> new ArrayList<>()).addAll(items);
				});
			
			// 実行されなかった分について、改めてitemsを作り、再実行する
			Map<String, KeysAndAttributes> retryItems = response.unprocessedKeys();
			
			// （残作業）上限ループ回数を設定する
			
			int loopCount = 0;
			int loopMaxCount = 5;
			
			while (!retryItems.isEmpty() && loopCount < loopMaxCount) {
				BatchGetItemRequest retryRequest = BatchGetItemRequest.builder().requestItems(retryItems).build();
				BatchGetItemResponse retryResponse = dynamoDb.batchGetItem(retryRequest);
				
				// 再実行の結果を、結果に追加する
				// 初回の結果にkey（テーブル名）がなければ、キーを作成して、値を入れる
				retryResponse.responses().forEach((table, items) -> {
					mergedResponses.computeIfAbsent(table, v -> new ArrayList<>()).addAll(items);});
				
				// リトライでも実行されなかった分をretryItemsに入れる（なくなるまでループが回る）
				retryItems = retryResponse.unprocessedKeys();
				
				loopCount ++;
				
			}
		
		} catch (DynamoDbException e){
			// 上位に投げる
			throw e;
		}
		
		// 問い合わせ結果から、List<UserInfo> を作成して返す
		List<UserInfo> userInfoList = new ArrayList<>();
		
		// responses()からテーブル名を指定して取り出す
		// 要素ごとにMapになっている
		List<Map<String, AttributeValue>> items = mergedResponses.get(usersTable);
		
		for ( Map<String, AttributeValue> item : items) {
			UserInfo userInfo = new UserInfo(
					item.get("userid").s(),
					item.get("username").s(),
					Integer.parseInt(item.get("adminpriv").n())
					);
			userInfoList.add(userInfo);
		}
		
		return userInfoList;
		
	}
	//ユーザー削除処理
	public int delUser(List<String> userIds) {
		
		int resultCount = 0;
		
		// ループを回して削除し、削除件数を返す
		if (userIds == null || userIds.isEmpty()) {
			return resultCount;
		}
		
		for (String userId : userIds) {
			
			// userIdをもとにUsersから、usernameを取得して、Usernamesの削除対象とする
			Map<String, AttributeValue> key0 = Map.of("userid", AttributeValue.fromS(userId));
			
			GetItemRequest request = GetItemRequest.builder()
					.tableName(usersTable)
					.key(key0)
					.build();
			
			Map<String, AttributeValue> item;
			
			try { 
				item = dynamoDb.getItem(request).item();
			} catch (DynamoDbException e) {
				System.err.println(e.toString());
				return resultCount;
			}
			
			if (item.isEmpty() ) {
				System.err.println("対象ユーザーがusernamesに存在しません");
				return resultCount;
			}

			String userName = item.get("username").s();

			Map<String, AttributeValue> key1 = Map.of("username", AttributeValue.fromS(userName));

			Map<String, AttributeValue> key2 = Map.of("userid", AttributeValue.fromS(userId));
			
		
			// トランザクションの開始
			TransactWriteItemsRequest tx = TransactWriteItemsRequest.builder().transactItems(
					// usernameをもとにusernamesテーブルから削除
					TransactWriteItem.builder()
						.delete(Delete.builder()
							.tableName(usernamesTable)
							.key(key1)
							.conditionExpression("attribute_exists(username)")
							.build())
						.build(),
					// userIdをもとにUsersテーブルから削除		
					TransactWriteItem.builder()
						.delete(Delete.builder()
							.tableName(usersTable)
							.key(key2)
							.conditionExpression("attribute_exists(userid)")
							.build())
						.build()
					)
					.build();
			
			try {
				dynamoDb.transactWriteItems(tx);
				resultCount++;
				
			} catch (TransactionCanceledException e){
				System.err.println(e.toString());
				if (e.hasCancellationReasons()) {
					List<CancellationReason> reasons = e.cancellationReasons();
					if (reasons.size() > 0 && "ConditionalCheckFailed".equals(reasons.get(0).code())) {
						return resultCount;
					}
				}
				return resultCount;
			} catch (DynamoDbException e) {
				System.err.println(e.toString());
				return resultCount;
			}
		}
		
		return resultCount;
	}
}
