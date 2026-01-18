package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.DbOpeResult;
import entity.AuthInfo;
import entity.UserInfo;
import infrastructure.ConnectionFactory;

public class AdminUsersDAO {

	public AuthInfo getPassword(String username) {
		String sql = "select password_hash, salt, userid from users where username = ? and adminpriv = 1";
		
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

	// ユーザー一覧の取得
	public List<UserInfo> getUserList() {

		List<UserInfo> userList = new ArrayList<>();
		
		String sql = "select userid, username, adminpriv from users order by userid";
		
		// DB接続して、クエリを実行
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			
			// クエリを実行
			ResultSet rs = pStmt.executeQuery();
			
			// 結果を取得するには、rs.nextが必要
	        while (rs.next()) {
	        	// レコード内容をUserInfo型に入れてリストに入れる
	            UserInfo userInfo = new UserInfo(rs.getInt("userid"),rs.getString("username"),rs.getInt("adminpriv"));
	            userList.add(userInfo);
	        }
			
		} catch (SQLException e) {
			e.printStackTrace();
			// nullで返すと、呼び出し元でNPEを起こしやすいので、空のリストを返す
			return Collections.emptyList();
		}
				
		return userList;
		
	}
	
	public UserInfo getUserInfo(int userId) {
		
		String sql = "select userid ,username, adminpriv from users where userid = ?";
				
		// DB接続して、クエリを実行
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			
			// クエリを実行
			pStmt.setInt(1, userId);
			ResultSet rs = pStmt.executeQuery();
			
			// 結果を取得するには、rs.nextが必要。1行目を返す
	        if (rs.next()) {
	        	// レコード内容をUserInfo型に入れて返す
	            UserInfo userInfo = new UserInfo(rs.getInt("userid"),rs.getString("username"),rs.getInt("adminpriv"));
	            return userInfo;
	        } else {
	        	return null;
	        }
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
						
	}
	
	public DbOpeResult updateUserInfo(UserInfo userInfo) {
		
		String sql = "update users set username = ?, adminpriv = ? where userid = ?";
			
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)){
			
			int userId = userInfo.getUserId();
			String userName = userInfo.getUserName();
			int adminPriv = userInfo.getAdminPriv();
			
			pStmt.setString(1, userName);
			pStmt.setInt(2, adminPriv);
			pStmt.setInt(3, userId);
			
			int affectedRows = pStmt.executeUpdate();
			
			if (affectedRows != 1) {
				return DbOpeResult.ERROR;
			} else {
				return DbOpeResult.SUCCESS;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return DbOpeResult.ERROR;
		}
		
	}
	

}
