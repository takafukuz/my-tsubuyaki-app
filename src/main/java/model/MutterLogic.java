package model;

import java.util.ArrayList;
import java.util.List;

import common.DbOpeResult;
import dao.MuttersDAO;
import entity.Mutter;

public class MutterLogic {

	public List<Mutter> getAllMutters(){
		
		List<Mutter> mutterList = new ArrayList<>();
		
		MuttersDAO dao = new MuttersDAO();
		mutterList = dao.selectAllMutters();
		
		return mutterList;
	}
	
	public DbOpeResult addMutter(int userId,String text) {
		
		MuttersDAO dao = new MuttersDAO();
		DbOpeResult result = dao.addMutter(userId,text);
		
		return result;
	}
	
	public DbOpeResult delMutter(int userId, int mutterId) {
		
		MuttersDAO dao = new MuttersDAO();
		DbOpeResult result = dao.delMutter(userId, mutterId);
		
		return result;
	}
}
