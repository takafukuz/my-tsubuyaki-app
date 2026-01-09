package dao;

import java.util.List;

import entity.Mutter;

public class MutterDAOTest {

	public static void main(String[] args) {
		MuttersDAO dao = new MuttersDAO();
		List<Mutter> mutterList = dao.selectAllMutters();
		
		for (Mutter m : mutterList) {
			System.out.println(m.getMutterId());
			System.out.println(m.getUserName());
			System.out.println(m.getMutter());
			System.out.println(m.getCreatedAt());
		}

	}

}
