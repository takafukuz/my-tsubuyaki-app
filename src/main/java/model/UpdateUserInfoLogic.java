package model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import common.DbOpeResult;
import dao.UsersDAO;

public class UpdateUserInfoLogic {
	
    private static int iterations = 10000; // 反復回数
    private static int keyLength = 256;    // ハッシュの長さ
	
    //入力パスワードをハッシュ化して、DBに保存する
	public DbOpeResult changePassword(String userId,String password) {
		
		if (userId == null || userId.isBlank()) {
			return DbOpeResult.ERROR;
		}
		
		if (password == null || password.isBlank()) {
			return DbOpeResult.ERROR;
		}
		
		try {
			// ソルトの生成
	        SecureRandom random = new SecureRandom();
	        byte[] salt = new byte[16];
	        random.nextBytes(salt); // ランダムなバイト列を生成
	        String saltStr = Base64.getEncoder().encodeToString(salt); // 文字列に変換
	        
	        // パスワードのハッシュ化
	        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        byte[] passwordHash = skf.generateSecret(spec).getEncoded();
		    String passwordHashStr = Base64.getEncoder().encodeToString(passwordHash); //文字列に変換
		    
		    //DBのUPDATE
		    UsersDAO dao = new UsersDAO();
		    return dao.updatePassword(userId, passwordHashStr, saltStr);
		    		
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
			
			e.printStackTrace();
	        throw new RuntimeException(e);
		}
		
	}
	
	// ユーザー名を変更する
	public DbOpeResult changeUserName(String userId,String userName) {
		
		if (userId == null || userId.isBlank()) {
			return DbOpeResult.ERROR;
		}
		
		if (userName == null || userName.isBlank()) {
			return DbOpeResult.ERROR;
		}
		
		UsersDAO dao = new UsersDAO();
		DbOpeResult result = dao.updateUserName(userId, userName);
		return result;
		
	}
}
