package entity;

import java.io.Serializable;

public class NewUserForm implements Serializable {
	
	private String userName;
	private int adminPriv;
	private String password;
	
	public NewUserForm() {}
	
	public NewUserForm(String userName, int adminPriv, String password) {
		this.userName = userName;
		this.adminPriv = adminPriv;
		this.password = password;
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
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
