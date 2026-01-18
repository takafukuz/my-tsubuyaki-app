package entity;

import java.io.Serializable;

public class NewUserInfo implements Serializable {
	private String userName;
	private int adminPriv;
	private String passwordHash;
	private String salt;
	
	public NewUserInfo() {}
	
	public NewUserInfo(String userName, int adminPriv, String passwordHash, String salt) {
		this.userName = userName;
		this.adminPriv = adminPriv;
		this.passwordHash = passwordHash;
		this.salt = salt;
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
	
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
}
