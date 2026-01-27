package model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import common.DbOpeResult;
import dao.AdminUsersDAO;
import entity.NewUserForm;
import entity.NewUserInfo;
import entity.UserInfo;

public class AdminUserLogic {
	
    private static int iterations = 10000; // 反復回数
    private static int keyLength = 256;    // ハッシュの長さ
	
	public List<UserInfo> getUserList(){
		
		AdminUsersDAO dao = new AdminUsersDAO();
		List<UserInfo> userList = dao.getUserList();
		
		return userList;

	}
	
	public UserInfo getUserInfo(String userId) {
		
		AdminUsersDAO dao = new AdminUsersDAO();
		UserInfo userInfo = dao.getUserInfo(userId);
		
		return userInfo;
	}
	
	public DbOpeResult addUser(NewUserForm newUserForm) {
		
		if (newUserForm == null) {
			return DbOpeResult.ERROR;
		}
		
		String userName = newUserForm.getUserName();
		int adminPriv = newUserForm.getAdminPriv();
		String password = newUserForm.getPassword();
		
		if (userName == null || userName.isBlank()) {
			return DbOpeResult.ERROR;
		}
		
		if (password == null || password.isBlank()) {
			return DbOpeResult.ERROR;
		}
		
		NewUserInfo newUserInfo = new NewUserInfo();
		
		try {
			// ソルトの生成
	        SecureRandom random = new SecureRandom();
	        byte[] salt = new byte[16];
	        random.nextBytes(salt); // ランダムなバイト列を生成
	        String saltStr = Base64.getEncoder().encodeToString(salt); // 文字列に変換=DB保存用
	        
	        // パスワードのハッシュ化
	        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        byte[] passwordHash = skf.generateSecret(spec).getEncoded();
		    String passwordHashStr = Base64.getEncoder().encodeToString(passwordHash); //文字列に変換
		    
		    // NewUserInfo型に保存して、DAOに投げる
		    newUserInfo = new NewUserInfo(userName, adminPriv, passwordHashStr, saltStr);
		    
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			e.printStackTrace();
	        return DbOpeResult.ERROR;
		}
		
	    AdminUsersDAO dao = new AdminUsersDAO();
	    DbOpeResult result = dao.addUser(newUserInfo);
	    
	    return result;	
		
	}
	
	public List<UserInfo> findUsersByIds(List<String> userIds){
		
		AdminUsersDAO dao = new AdminUsersDAO();
		List<UserInfo> userList = dao.findUsersByIds(userIds);

		return userList;
	}
	
	public int delUser(List<String> userIds) {

		AdminUsersDAO dao = new AdminUsersDAO();
		int result = dao.delUser(userIds);
		
		return result;
		
	}
	
}
