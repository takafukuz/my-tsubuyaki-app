package infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConnectionFactory {

    private static String JDBC_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    private static final String authSecret = "rds!db-34efca95-d5f8-4eed-8fdd-7637b773a9e0";
  
    // 遅延ロード（これでやらないと、Github上でTestコードが動かない）
    // getConnectionが呼ばれたときに動く。かつ、同時に動かない（synchronized)
    private static synchronized void loadIfNeeded() {
		try {
			// すでに実行済みなら実施しない
			if (JDBC_URL != null) {
			    return; // すでに初期化済み
			}

			// TOMCATの起動オプションからenvの値を取得する。取得できなければ、devとする
			String env = System.getProperty("env", "dev");
		
			// envの値をもとにurlSecretの名前を決定する
			String urlSecret = "MyTsubuyakiApp/" + env + "/db";
		
			// SecretsManagerからJSON文字列を取得し、Map(辞書）に変換後、取り出す
			String json;
			ObjectMapper mapper = new ObjectMapper();
		
			// urlの取得
			json = SecretsManagerUtil.getSecret(urlSecret);
			Map<String, String> map = mapper.readValue(json, Map.class);
			JDBC_URL = map.get("url");
		
			// 認証情報の取得
			json = SecretsManagerUtil.getSecret(authSecret);
			map = mapper.readValue(json, Map.class);
			DB_USER = map.get("username");
			DB_PASSWORD = map.get("password");
		
			System.out.println("SecretsMangerから取得した情報：");
			System.out.println(JDBC_URL);
			System.out.println(DB_USER);
			System.out.println(DB_PASSWORD);
		
		} catch (Exception e) {
			  throw new RuntimeException("DB接続情報の取得に失敗", e);
		}
    }
  
    // DBコネクション作成
    public static Connection getConnection() throws SQLException {
    	
    	try {
    		loadIfNeeded();
    		return DriverManager.getConnection(JDBC_URL,DB_USER,DB_PASSWORD);
    	} catch  (SQLException e) {
            throw new SQLException("DBコネクションの作成に失敗", e);
    	}
    	
    }
	
}
