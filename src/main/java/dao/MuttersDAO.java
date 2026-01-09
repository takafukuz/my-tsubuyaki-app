package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import common.DbOpeResult;
import entity.Mutter;
import infra.ConnectionFactory;

public class MuttersDAO {
	
	// つぶやきリストの取得
	public List<Mutter> selectAllMutters() {
		
		String sql = "select m.mutterid,m.userid,u.username,m.mutter,m.createdat from mutters as m inner join users as u "
				+ "on m.userid = u.userid order by m.mutterid desc";
				
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)){
			
			ResultSet rs = pStmt.executeQuery();
			
			List<Mutter> mutterList = new ArrayList<>();
			
			// DBからの結果をもとにMutterインスタンスを作成して、アレイリストに入れる
			// 空の場合は while(rs.next()) が実行されず、結果リストは空のまま返る
			while (rs.next()) {
				
				int mutterId = rs.getInt("mutterid");
				int userId = rs.getInt("userid");
				String userName = rs.getString("username");
				String mutterText = rs.getString("mutter");
				Timestamp createdAt = rs.getTimestamp("createdAt");
				
				//Mutter mutter = new Mutter(mutterId,userName,mutterText,createdAt);
				Mutter mutter = new Mutter(mutterId,userId,userName,mutterText,createdAt);
				
				mutterList.add(mutter);
			}
			
			return mutterList;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
	        throw new RuntimeException(e);
		}
	}

	// つぶやきの追加
	public DbOpeResult addMutter(int userId,String text) {
		
		String sql = "insert into mutters (userid,mutter) values (?, ?)";
		
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)){
			
			pStmt.setInt(1, userId);
			pStmt.setString(2, text);
			
			int result = pStmt.executeUpdate();
			
			if (result != 1) {
				return DbOpeResult.ERROR;
			} else {
				return DbOpeResult.SUCCESS;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
	}

}
