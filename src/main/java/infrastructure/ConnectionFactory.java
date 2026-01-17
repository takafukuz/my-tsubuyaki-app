package infrastructure;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

  private static final String JDBC_URL;
  private static final String DB_USER;
  private static final String DB_PASSWORD;

  //スタティックブロック（クラスが初めて使われたときに実行される＝1回だけ実行）
  static {
	  try {
		  Properties props = new Properties();
		  
		  String env = System.getProperty("env", "dev"); // デフォルトは dev
		  String fileName = "db.properties." + env;
		  
		  System.out.println(fileName);
		  
		  InputStream is = ConnectionFactory.class.getClassLoader().getResourceAsStream(fileName);
		  
		  props.load(is);
		  
		  JDBC_URL = props.getProperty("db.url");
		  DB_USER = props.getProperty("db.user");
		  DB_PASSWORD = props.getProperty("db.password");
		  
//		  System.out.println(JDBC_URL);
//		  System.out.println(DB_USER);
//		  System.out.println(DB_PASSWORD);
		  
	  } catch (Exception e) {
		  throw new RuntimeException("DB設定ファイルの読み込みに失敗", e);
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
