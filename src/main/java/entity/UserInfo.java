package entity;

import java.io.Serializable;

public class UserInfo implements Serializable{

	private int userId ;
	private String userName;
	private int adminPriv;
	
	public UserInfo(){};
	
	public UserInfo(int userId,String userName,int adminPriv) {
		this.userId = userId;
		this.userName = userName;
		this.adminPriv = adminPriv;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getAdminPriv() {
		return adminPriv;
	}
	
	public void setAdminPriv(int adminPriv) {
		this.adminPriv = adminPriv;
	}
	
}
