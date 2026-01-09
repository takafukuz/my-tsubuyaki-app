package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.DbOpeResult;
import entity.AuthInfo;
import infra.ConnectionFactory;


public class UsersDAO {
	
	public AuthInfo getPassword(String userName) {
		
		String sql = "select password_hash,salt,userid from users where username = ?";
		
		try	(Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)){
			
			pStmt.setString(1, userName);
			ResultSet rs = pStmt.executeQuery();
			
			if (rs.next()) {
				AuthInfo authInfo = new AuthInfo(rs.getString("password_hash"),rs.getString("salt"),rs.getInt("userid"));
				return authInfo;
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DbOpeResult updatePassword(int userId,String password,String salt) {
		
		String sql = "update users set password_hash = ?, salt = ? where userid = ?";
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)){
			
			pStmt.setString(1, password);
			pStmt.setString(2, salt);
			pStmt.setInt(3, userId);
			int result =  pStmt.executeUpdate();
			
			if (result != 1) {
				return DbOpeResult.ERROR;
			} else {
				return DbOpeResult.SUCCESS;	
			}
			
		} catch (SQLException e){
			e.printStackTrace();
			return DbOpeResult.ERROR;
		}
		
	}
	
	public DbOpeResult updateUserName(int userId,String userName) {
		
		String sql = "update users set username = ? where userid = ?";
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)){
			
			pStmt.setString(1, userName);
			pStmt.setInt(2, userId);
			int result = pStmt.executeUpdate();
			
			if (result == 1) {
				return DbOpeResult.SUCCESS;
			} else {
				return DbOpeResult.ERROR;
			}
			
		} catch (SQLException e){
			
            // 重複時は、専用エラーコードで返す
	        if (e.getErrorCode() == 23505) { 
	            return DbOpeResult.DUPLICATE;
	        }
			
			e.printStackTrace();
			return DbOpeResult.ERROR;
		}
	}
}
