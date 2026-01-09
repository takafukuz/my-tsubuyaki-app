package entity;

import java.io.Serializable;

public class LoginUser implements Serializable{
	
	private int userId;
	private String userName;
	
	public LoginUser() {}
	public LoginUser(int userId,String userName) {
		this.userId = userId;
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}