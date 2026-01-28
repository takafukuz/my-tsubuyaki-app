package entity;

import java.io.Serializable;

public class AdminLoginUser implements Serializable{
	
	private String userId;
	private String userName;
	
	public AdminLoginUser() {}
	public AdminLoginUser(String userId,String userName) {
		this.userId = userId;
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}