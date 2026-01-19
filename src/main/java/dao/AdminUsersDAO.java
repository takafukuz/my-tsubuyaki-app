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
import entity.NewUserInfo;
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
		
		String sql = "select userid, username, adminpriv from users order by userid";
		
		List<UserInfo> userList = new ArrayList<>();
		
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
	        
			return userList;
			
		} catch (SQLException e) {
			e.printStackTrace();
			// nullで返すと、呼び出し元でNPEを起こしやすいので、空のリストを返す
			return Collections.emptyList();
		}
				

		
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
	
	public DbOpeResult addUser(NewUserInfo newUserInfo) {
		
		String sql = "insert into users (username, adminpriv, password_hash, salt) values (?, ?, ?, ?)";
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			
			String userName =  newUserInfo.getUserName();
			int adminPriv = newUserInfo.getAdminPriv();
			String passwordHash = newUserInfo.getPasswordHash();
			String salt = newUserInfo.getSalt();
			
			pStmt.setString(1, userName);
			pStmt.setInt(2, adminPriv);
			pStmt.setString(3, passwordHash);
			pStmt.setString(4, salt);
			
			int affectRows = pStmt.executeUpdate();
			
			if (affectRows != 1) {
				return DbOpeResult.ERROR;
			} else {
				return DbOpeResult.SUCCESS;
			}
			
		} catch (SQLException e) {
			
	        if (e.getErrorCode() == 1062) { 
	            // user_name UNIQUE 制約違反
	            return DbOpeResult.DUPLICATE;
	        }
	        
			e.printStackTrace();
			return DbOpeResult.ERROR;
		}
	}

public List<UserInfo> findUsersByIds(List<Integer> userIds) {
	
		// もし、userIdsが空なら、空のリストを返す
	    if (userIds == null || userIds.isEmpty()) {
	        return Collections.emptyList();
	    }

		// プレースホルダとして、userIdsの個数分？を用意する
		// , 区切りだが、最後の？には , をつけない
		StringBuilder sb = new StringBuilder();
		
		for ( int i = 0; i < userIds.size(); i++) {
			sb.append("?");
			if ( i < userIds.size() - 1) {
				sb.append(",");
			}
		}
		
		String sql = "select userid, username, adminpriv from users where userid in (" + sb.toString() + ")";
		
		List<UserInfo> userList = new ArrayList<>();
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			
			// プレースホルダの数だけ、値を用意する
			for ( int n = 0; n < userIds.size(); n++) {
				pStmt.setInt(n+1, userIds.get(n));
			}
			
			ResultSet rs = pStmt.executeQuery();
			
			// 結果を取得するには、rs.nextが必要
	        while (rs.next()) {
	        	// レコード内容をUserInfo型に入れてリストに入れる
	            UserInfo userInfo = new UserInfo(rs.getInt("userid"),rs.getString("username"),rs.getInt("adminpriv"));
	            userList.add(userInfo);
	        }
			
			return userList;
			
		} catch (SQLException e) {
			e.printStackTrace();
			// nullで返すと、呼び出し元でNPEを起こしやすいので、空のリストを返す
			return Collections.emptyList();
		}
	}
	
	public int delUser(List<Integer> userIds) {
		
		// プレースホルダとして、userIdsの個数分？を用意する
		// , 区切りだが、最後の？には , をつけない
		StringBuilder sb = new StringBuilder();
		
		for ( int i = 0; i < userIds.size(); i++) {
			sb.append("?");
			if ( i < userIds.size() - 1) {
				sb.append(",");
			}
		}
		
		String sql = "delete from users where userid in (" + sb.toString() + ")";
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {
			
			// プレースホルダの数だけ、値を用意する
			for ( int n = 0; n < userIds.size(); n++) {
				pStmt.setInt(n+1, userIds.get(n));
			}
			
			int affectedRows = pStmt.executeUpdate();
			
//			if (affectedRows == userIds.size()) {
//				return DbOpeResult.SUCCESS;
//			} else {
//				return DbOpeResult.ERROR;
//			}
			// 削除件数を返す
			return affectedRows;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			// エラー時は-1を返す
			return -1;
		}
	}

}
