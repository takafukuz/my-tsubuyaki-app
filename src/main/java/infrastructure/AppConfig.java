package infrastructure;

import java.util.Properties;

import common.ConfigLoader;

public class AppConfig {

	private static final AppConfig INSTANCE = new AppConfig();
	
	private final String usersTable;
	private final String usernamesTable;
	private final String muttersTable;
	
	private AppConfig() {
		Properties props = ConfigLoader.load();
		
		this.usersTable = props.getProperty("dynamodb.table.users");
		this.usernamesTable = props.getProperty("dynamodb.table.usernames");
		this.muttersTable = props.getProperty("dynamodb.table.mutters");
		
		
	}
	
	public static AppConfig getInstance() {
	    return INSTANCE;
	}
	
	public String getUsersTable() {
	    return usersTable;
	}
	
	public String getUsernamesTable() {
	    return usernamesTable;
	}
	
	public String getMuttersTable() {
	    return muttersTable;
	}
}
