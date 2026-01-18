package model;

import java.util.List;

import dao.AdminUsersDAO;
import entity.UserInfo;

public class AdminUserLogic {
	
	public List<UserInfo> getUserList(){
		
		AdminUsersDAO dao = new AdminUsersDAO();
		List<UserInfo> userList = dao.getUserList();
		
		return userList;

	}
	
	public UserInfo getUserInfo(int userId) {
		
		AdminUsersDAO dao = new AdminUsersDAO();
		UserInfo userInfo = dao.getUserInfo(userId);
		
		return userInfo;
	}
}
