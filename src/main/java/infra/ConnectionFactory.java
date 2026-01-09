package infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // H2 DB 接続情報
//    private static final String JDBC_URL = "jdbc:h2:tcp://localhost/~/DokoTsubu4";
//    private static final String DB_USER = "sa";
//    private static final String DB_PASSWORD = "takayuki";
    
    // MySQL
    private static final String JDBC_URL = "jdbc:mysql://sandmysql3.cdco0i46w18w.ap-northeast-1.rds.amazonaws.com:3306/DokoTsubu4?serverTimezone=UTC";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "VV344g>[4_(Gh(|CRmT9yRyeTyxF";
    
    // DBコネクション作成
    public static Connection getConnection() throws SQLException {
    	
    	try {
    		return DriverManager.getConnection(JDBC_URL,DB_USER,DB_PASSWORD);
    	} catch  (SQLException e) {
            throw new SQLException("DBコネクションの作成に失敗", e);
    	}
    	
    }
	
}
