package dao;

import entity.AuthInfo;

public class UsersDAOTest2 {

	public static void main(String[] args) {
		UsersDAO dao = new UsersDAO();
		AuthInfo passwordInfo = dao.getPassword("testuser");
		System.out.println(passwordInfo.getPassword());
		System.out.println(passwordInfo.getSalt());
		System.out.println(passwordInfo.getUserId());
	}

}
