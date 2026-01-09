package dao;

import common.DbOpeResult;

public class UsersDAOTest3 {

	public static void main(String[] args) {
		
		// 正常系
		UsersDAO dao = new UsersDAO();
		DbOpeResult result = dao.updateUserName(4, "hogehoge");
		
		if (result == DbOpeResult.SUCCESS) {
			System.out.println("成功");
		} else {
			System.out.println("失敗");
		}

	}

}
