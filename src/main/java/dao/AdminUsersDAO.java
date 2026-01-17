package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.AuthInfo;
import infrastructure.ConnectionFactory;

public class AdminUsersDAO {

	public AuthInfo getAdminPassword(String username) {
		String sql = "select password_hash, salt, userid from users where username = ?";
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setString(1, username);
			ResultSet rs = pStmt.executeQuery();
			
			// 結果の1行目を取得して、AuthInfo型で返す。1行もなければ null を返す
			if (rs.next()) {
				AuthInfo authInfo = new AuthInfo(rs.getString("password_hash"), rs.getString("salt"), rs.getInt("userid"));
				return authInfo;			
			} else {
				return null;
			}
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}
		
	}


}
