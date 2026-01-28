package infrastructure;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDbClientHolder {
	
	private static final DynamoDbClient INSTANCE = DynamoDbClient.builder().region(Region.AP_NORTHEAST_1).build();
	
	private DynamoDbClientHolder() {}

	public static DynamoDbClient getInstance() {
		System.out.println("DynamoDbClient.getInstanceが呼び出されました："+ java.time.LocalDateTime.now());
		return INSTANCE;
	}
}
