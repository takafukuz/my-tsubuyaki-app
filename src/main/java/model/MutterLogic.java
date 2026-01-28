package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.DbOpeResult;
import dao.MuttersDAO;
import dao.UsersDAO;
import entity.Mutter;

public class MutterLogic {

	public List<Mutter> getAllMutters(){
		
		// 投稿直後につぶやき一覧が更新されない問題（GSI反映遅延）への暫定対応。少し待つ。
		try {
		    Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		    Thread.currentThread().interrupt();
		    return Collections.emptyList();
		}
		
		// つぶやき一覧を取得
		List<Mutter> mutterList = new ArrayList<>();
		
		MuttersDAO dao = new MuttersDAO();
		mutterList = dao.selectAllMutters();
		// System.out.println("selectAllMutters終了");
		
		// つぶやき一覧に出現するuseridについて、現在のusernameを取得し、
		// mutterListのusernameを更新する
		Set<String> userIds = new HashSet<>();
		for (Mutter m : mutterList) {
			userIds.add(m.getUserId());
		}
		
		UsersDAO usersDao = new UsersDAO();
		Map<String, String> userNameMap = usersDao.findUserByIds(userIds);
		// System.out.println("findUserByIds終了");
		
		for (Mutter m : mutterList) {
			String latestUserName = userNameMap.get(m.getUserId());
			m.setUserName(latestUserName != null ? latestUserName : "DeletedUser");
			
			// System.out.println(m.getUserId() + "：" + m.getUserName());
		}
		
		// System.out.println("getAllMutters終了");
		return mutterList;
	}
	
	public DbOpeResult addMutter(String userId,String text) {
		
		MuttersDAO dao = new MuttersDAO();
		DbOpeResult result = dao.addMutter(userId,text);
		
		return result;
	}
	
	public DbOpeResult delMutter(String userId, String mutterId) {
		
		MuttersDAO dao = new MuttersDAO();
		DbOpeResult result = dao.delMutter(userId, mutterId);
		
		return result;
	}
}
