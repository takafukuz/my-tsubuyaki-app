package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class DokoTsubu4Listener
 *
 */
@WebListener
public class MyTsubuyakiAppListener implements ServletContextListener {

	// H2
	// private static final String DRIVER_NAME = "org.h2.Driver";
	// MySQL
	private static final String DRIVER_NAME="com.mysql.cj.jdbc.Driver";
	
	@Override
    public void contextInitialized(ServletContextEvent sce)  { 
		try {
			Class.forName(DRIVER_NAME);
			System.out.println("DBドライバを読み込みました");
		} catch (ClassNotFoundException e){
			throw new IllegalStateException("DBドライバを読み込めませんでした");
		}
    }

	@Override
    public void contextDestroyed(ServletContextEvent sce)  { 
         // 処理なし
    }
	
}
