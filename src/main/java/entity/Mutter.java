package entity;

import java.sql.Timestamp;

public class Mutter {
	
	private int mutterId;
	private int userId;
	private String userName;
	private String mutter;
	private Timestamp createdAt;
	
	public Mutter() {}
	
	public Mutter(int mutterId,int userId,String userName,String mutter,Timestamp createdAt) {
		this.mutterId = mutterId;
		this.userId = userId;
		this.userName = userName;
		this.mutter = mutter;
		this.createdAt = createdAt;
	}

	public int getMutterId() {
		return mutterId;
	}
	
	public void setMutterId(int mutterId) {
		this.mutterId = mutterId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int setUserId(int userId) {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getMutter() {
		return mutter;
	}
	
	public void setMutter(String mutter) {
		this.mutter = mutter;
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	

	
}
