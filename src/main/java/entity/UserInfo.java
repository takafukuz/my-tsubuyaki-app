package entity;

import java.io.Serializable;

public class UserInfo implements Serializable{

	private String userId ;
	private String userName;
	private int adminPriv;
	
	public UserInfo(){};
	
	public UserInfo(String userId,String userName,int adminPriv) {
		this.userId = userId;
		this.userName = userName;
		this.adminPriv = adminPriv;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
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
