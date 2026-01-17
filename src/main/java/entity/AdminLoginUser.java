package entity;

import java.io.Serializable;

public class AdminLoginUser implements Serializable{
	
	private int userId;
	private String userName;
	
	public AdminLoginUser() {}
	public AdminLoginUser(int userId,String userName) {
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