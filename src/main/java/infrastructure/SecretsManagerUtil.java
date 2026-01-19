package infrastructure;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManagerUtil {
	//シークレット名を指定してJSON文字列を取得する
    public static String getSecret(String secretName) {

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.AP_NORTHEAST_1) // 東京リージョン
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);

        return response.secretString(); // JSON文字列が返る
    }
    
}
