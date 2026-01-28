package entity;

import java.io.Serializable;

public class LoginUser implements Serializable{
	
	private String userId;
	private String userName;
	
	public LoginUser() {}
	public LoginUser(String userId,String userName) {
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