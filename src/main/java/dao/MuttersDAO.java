package dao;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.DbOpeResult;
import entity.Mutter;
import infrastructure.AppConfig;
import infrastructure.DynamoDbClientHolder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

public class MuttersDAO {
	
	// DynamoDB接続クライアントを呼び出す
	private final DynamoDbClient dynamoDb = DynamoDbClientHolder.getInstance();
	
	// テーブル名を保持しているAppConfigを呼ぶ
    private final String usersTable;
    private final String muttersTable;
    
    public MuttersDAO() {
        this.usersTable = AppConfig.getInstance().getUsersTable();
        this.muttersTable = AppConfig.getInstance().getMuttersTable();
    }
	
	// つぶやきリストの取得
	// すべてのつぶやきを取得して、created_atで逆順に並べる
	public List<Mutter> selectAllMutters() {
		
		QueryRequest request = QueryRequest.builder()
				.tableName(muttersTable)
				.indexName("created_at-index")
				.keyConditionExpression("#entity_type = :entity_type")
				.expressionAttributeNames(Map.of(
						"#entity_type","entity_type",
						"#mutter_id", "mutter_id",
						"#userid", "userid",
						"#username", "username",
						"#mutter", "mutter",
						"#created_at", "created_at"))
				.expressionAttributeValues(Map.of(":entity_type", AttributeValue.fromS("MUTTER")))
				.projectionExpression("#mutter_id, #userid, #username, #mutter, #created_at")
				.scanIndexForward(false)
				.limit(100) // ページネーション実装までの暫定上限
				.build();
		
		List<Map<String, AttributeValue>> items;
		
		try {
			items = dynamoDb.query(request).items();
			// 結果がない場合
			if (items.isEmpty()) {
				System.out.println("つぶやき一覧取得の結果が0件です");
				return Collections.emptyList();
			}
		} catch (DynamoDbException e) {
			// エラー発生時は、空のリストを返す
			System.err.println(e.getMessage());
			return Collections.emptyList();
		}
		
		// 結果をMutterDynamoのリストにして返す
		List<Mutter> mutterList = new ArrayList<>();
		
		for (Map<String, AttributeValue> item : items) {
			
			// 投稿日時を文字列→Timestamp型に変換
			String createdAtStr = item.get("created_at").s();
			Instant createdAt = Instant.parse(createdAtStr);
			Timestamp timestamp = Timestamp.from(createdAt);
					
			Mutter mutter = new Mutter(
					item.get("mutter_id").s(),
					item.get("userid").s(),
					item.get("username").s(),
					item.get("mutter").s(),
					timestamp
					);
			
			mutterList.add(mutter);
		}
		
		return mutterList;

	}
	
	// つぶやきの追加
	public DbOpeResult addMutter(String userId,String text) {
		
		// ユーザーIDの生成		
		String mutterId = java.util.UUID.randomUUID().toString();
		
		// 時刻の取得
		String currentTime = Instant.now().toString();
		
		// ユーザーIDから、ユーザー名を取得
		Map<String, AttributeValue> key = Map.of("userid", AttributeValue.fromS(userId));
		GetItemRequest request = GetItemRequest.builder().tableName(usersTable).key(key).build();
		Map<String, AttributeValue> response = dynamoDb.getItem(request).item();
		String userName = response.get("username").s();
		
		// 挿入するitem
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("userid", AttributeValue.fromS(userId));
		item.put("username",AttributeValue.fromS(userName));
		item.put("mutter_id", AttributeValue.fromS(mutterId));
		item.put("mutter", AttributeValue.fromS(text));
		item.put("entity_type", AttributeValue.fromS("MUTTER"));
		item.put("created_at", AttributeValue.fromS(currentTime));
		
		PutItemRequest putItemRequest = PutItemRequest.builder()
				.tableName(muttersTable)
				.item(item)
				.build();
		
		try {
			dynamoDb.putItem(putItemRequest);
			return DbOpeResult.SUCCESS;
		} catch (DynamoDbException e){
			System.err.println(e.getMessage());
			return DbOpeResult.ERROR;
		}
	
}

	// つぶやきの削除
	public DbOpeResult delMutter(String userId, String mutterId) {
		
		// mutterIdとuserIdをもとに、Muttersから、当該itemを削除する
		Map<String, AttributeValue> key = Map.of("mutter_id", AttributeValue.fromS(mutterId));
		
		DeleteItemRequest request = DeleteItemRequest.builder()
				.tableName(muttersTable)
				.key(key)
				.conditionExpression("#userid = :userid")
				.expressionAttributeNames(Map.of(
						"#userid", "userid"))
				.expressionAttributeValues(Map.of(
						":userid", AttributeValue.fromS(userId)))
				.build();
		
		try {
			dynamoDb.deleteItem(request);
			return DbOpeResult.SUCCESS;
		} catch (ConditionalCheckFailedException e) {
			System.err.println(e.getMessage());
			return DbOpeResult.NOT_FOUND;
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			return DbOpeResult.ERROR;
		} 
	}
}
