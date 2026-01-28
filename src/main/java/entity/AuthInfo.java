package entity;

import java.io.Serializable;

public class AuthInfo implements Serializable{
	
	private String password;
	private String salt;
	private String userId;
	
	public AuthInfo() {
		
	}
	
	public AuthInfo(String password,String salt,String userId) {
		this.password = password;
		this.salt = salt;
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
