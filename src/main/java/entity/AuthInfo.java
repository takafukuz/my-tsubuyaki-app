package entity;

import java.io.Serializable;

public class AuthInfo implements Serializable{
	
	private String password;
	private String salt;
	private Integer userId;
	
	public AuthInfo() {
		
	}
	
	public AuthInfo(String password,String salt,Integer userId) {
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
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

}
