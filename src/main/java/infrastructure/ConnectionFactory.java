package infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConnectionFactory {

  private static final String JDBC_URL;
  private static final String DB_USER;
  private static final String DB_PASSWORD;
  
  private static final String secretForUrl = "MyTsubuyakiApp/db/config";
  private static final String secretFroAuth = "rds!db-34efca95-d5f8-4eed-8fdd-7637b773a9e0";

  //スタティックブロック（クラスが初めて使われたときに実行される＝1回だけ実行）
  static {
	  try {
		// TOMCATの起動オプションからenvの値を取得する。なければ、dev
		String env = System.getProperty("env", "dev");
		// SecretsManagerからJSON文字列を取得し、Map(辞書）に変換後、取り出す
		String json;
		ObjectMapper mapper = new ObjectMapper();
		// urlの取得
		json = SecretsManagerUtil.getSecret(secretForUrl);
		Map<String, String> map = mapper.readValue(json, Map.class);
		
		if ("prod".equals(env)) {
			JDBC_URL = map.get("ProdUrl");
		} else {
			JDBC_URL = map.get("DevUrl");
		}
		
		// 認証情報の取得
		json = SecretsManagerUtil.getSecret(secretFroAuth);
		map = mapper.readValue(json, Map.class);
		DB_USER = map.get("username");
		DB_PASSWORD = map.get("password");
		
//		System.out.println("SecretsMangerから取得した情報：");
//		System.out.println(JDBC_URL);
//		System.out.println(DB_USER);
//		System.out.println(DB_PASSWORD);

	  } catch (Exception e) {
		  throw new RuntimeException("DB接続情報の取得に失敗", e);
	  }
  }
  
    
    // DBコネクション作成
    public static Connection getConnection() throws SQLException {
    	
    	try {
    		return DriverManager.getConnection(JDBC_URL,DB_USER,DB_PASSWORD);
    	} catch  (SQLException e) {
            throw new SQLException("DBコネクションの作成に失敗", e);
    	}
    	
    }
	
}
