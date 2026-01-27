package dao;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import common.DbOpeResult;
import entity.AuthInfo;
import infrastructure.AppConfig;
import infrastructure.DynamoDbClientHolder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CancellationReason;
import software.amazon.awssdk.services.dynamodb.model.Delete;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;
import software.amazon.awssdk.services.dynamodb.model.Update;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class UsersDAO {
	
	// DynamoDB接続クライアントを呼び出す
	private final DynamoDbClient dynamoDb = DynamoDbClientHolder.getInstance();
	
    private final String usersTable;
    private final String usernamesTable;
    
    public UsersDAO() {
        this.usersTable = AppConfig.getInstance().getUsersTable();
        this.usernamesTable = AppConfig.getInstance().getUsernamesTable();
    }
	
	public AuthInfo getPassword(String userName) {
		
		QueryRequest request = QueryRequest.builder()
				.tableName(usersTable)
				.indexName("username-index")
				.keyConditionExpression("#username = :username")
				.projectionExpression("#password_hash, #salt, #userid")
				.expressionAttributeNames(Map.of(
						"#username", "username",
						"#password_hash", "password_hash",
						"#salt", "salt",
						"#userid", "userid"))
				.expressionAttributeValues(Map.of(
						":username", AttributeValue.fromS(userName)))
				.limit(1)
				.build();
		
		try {
			QueryResponse response = dynamoDb.query(request);
			
			List<Map<String, AttributeValue>> items = response.items();
			
			// response.items()はnullにならない（空が返る）
			if (items.isEmpty()) {
				return null;
			}
			
			// 1件だけ取り出す
			AuthInfo authInfo = new AuthInfo(
				items.get(0).get("password_hash").s(),
				items.get(0).get("salt").s(),
				items.get(0).get("userid").s()
				);
			
			return authInfo;
		} catch (DynamoDbException e){
			System.err.println(e.getMessage());
			return null;
		}
		
		
	}
	
	public DbOpeResult updatePassword(String userId,String password,String salt) {
		
		// 現在時刻を取得
		String currentTime = Instant.now().toString();
		
		// userIdをもとにUsersテーブルのpassword_hashとsaltをアップデートする
		// 対象itemがない場合も、エラーにならないので、conditionExpressionを入れる
		UpdateItemRequest request = UpdateItemRequest.builder()
				.tableName(usersTable)
				.key(Map.of("userid", AttributeValue.fromS(userId)))
				.updateExpression("set #password_hash = :password_hash, #salt = :salt, #updated_at = :updated_at")
				.conditionExpression("attribute_exists(userid)")
				.expressionAttributeNames(Map.of(
						"#password_hash", "password_hash",
						"#salt", "salt",
						"#updated_at", "updated_at"))
				.expressionAttributeValues(Map.of(
						":password_hash", AttributeValue.fromS(password),
						":salt", AttributeValue.fromS(salt),
						":updated_at", AttributeValue.fromS(currentTime)))
				.build();
		
		try {
			dynamoDb.updateItem(request);
			return DbOpeResult.SUCCESS;
		} catch (DynamoDbException e){
			System.err.println(e.getMessage());
			return DbOpeResult.ERROR;
		}
	}
	
	

	public DbOpeResult updateUserName(String userId,String userName) {
		// userIdをもとにUsersのusernameをupdateする
		// 新しいユーザー名が既に使われていないかをUsernamesテーブルでチェックする
		// Usersへのusername追加と同時に、Usernamesへの新usernameの追加と、旧usernameの削除を実行する
		
		// 追加で、Muttersテーブルのusernameを全件書き換える処理を入れること
		
		// userIdから現行usernameを取得（GetItem）
		Map<String, AttributeValue> key = Map.of("userid", AttributeValue.fromS(userId));
		GetItemRequest request = GetItemRequest.builder()
				.tableName(usersTable)
				.key(key)
				.build();
		
		String currentUserName;
		try {
			Map<String, AttributeValue> response = dynamoDb.getItem(request).item();
			
			currentUserName = response.get("username").s();
			if (currentUserName == null || currentUserName.isEmpty()) {
				System.out.println(userId + "は存在しません");
				return DbOpeResult.ERROR;
			}

		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			return DbOpeResult.ERROR;
		}
		
		// ユーザー名が変更されていなければ終了する
		// サーブレットで弾くので、このようなデータは入ってこない想定
		if (userName.equals(currentUserName)) {
			System.out.println("ユーザー名が変更されていません");
			return DbOpeResult.ERROR;
		}
		
		// トランザクション項目をリストで構築
	    List<TransactWriteItem> myTransactItems = new java.util.ArrayList<>();
	
		// Usernamesに新しいユーザー名を登録する
		Map<String, AttributeValue> addKey = Map.of("username", AttributeValue.fromS(userName));
	    myTransactItems.add(
	    		TransactWriteItem.builder()
		    		.put(
		    			Put.builder()
	    				.tableName(usernamesTable)
	    				.item(addKey)
	    				.conditionExpression("attribute_not_exists(#username)")
	    				.expressionAttributeNames(Map.of("#username", "username"))
	    				.build())
		    		.build());
	    

		// Usernamesから、現在のユーザー名を削除する
		Map<String, AttributeValue> delKey = Map.of("username", AttributeValue.fromS(currentUserName));
		
	    myTransactItems.add(
	    		TransactWriteItem.builder()
	    			.delete(
	    				Delete.builder()
	    				.tableName(usernamesTable)
	    				.key(delKey)
	    				.build()
	    				)
	    			.build());
	    
	    // Usersのユーザー名をUpdateする
		String currentTime = Instant.now().toString();
	    Map<String, AttributeValue> updateKey = Map.of("userid", AttributeValue.fromS(userId));
	    
	    myTransactItems.add(
	    		TransactWriteItem.builder()
	    			.update(
	    				Update.builder()
	    				.tableName(usersTable)
	    				.key(updateKey)
	    				.updateExpression("set #username = :username, #updated_at = :updated_at")
	    				.expressionAttributeNames(Map.of(
	    						"#username", "username",
	    						"#updated_at", "updated_at"))
	    				.expressionAttributeValues(Map.of(
	    						":username", AttributeValue.fromS(userName),
	    						":updated_at", AttributeValue.fromS(currentTime)))
	    				.build()
	    				)
	    			.build());
	    
	    try {
	
	    	dynamoDb.transactWriteItems(TransactWriteItemsRequest.builder().transactItems(myTransactItems).build());
	    	return DbOpeResult.SUCCESS;
	    } catch (TransactionCanceledException e) {
	    	
			System.err.println(e.getMessage());
	    	
	    	if (e.hasCancellationReasons()) {
	    		List<CancellationReason> reasons = e.cancellationReasons();
	    		if (reasons.size() > 0 && "ConditionalCheckFailed".equals(reasons.get(0).code())) {
	    			return DbOpeResult.DUPLICATE;
	    		}
	    	}
	    	return DbOpeResult.ERROR;
	    }
	}
	
}
