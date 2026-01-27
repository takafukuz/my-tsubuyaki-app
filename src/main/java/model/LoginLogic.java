package model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import dao.UsersDAO;
import entity.AuthInfo;

public class LoginLogic {
	
    private static int iterations = 10000; // 反復回数
    private static int keyLength = 256;    // ハッシュの長さ
	
	public String canLogin(String userName,String password) {
		
		try {
			// ユーザー名をもとにDBから登録パスワードとソルト値を取得
			UsersDAO dao = new UsersDAO();
			AuthInfo authInfo = dao.getPassword(userName);
			
			// DAOの結果がnullであれば、false
			if ( authInfo == null ) {
				System.out.println("認証失敗：" + userName + "のパスワード情報がありません");
				return null;
			}
			
			// 入力されたパスワードを同じソルト値でハッシュ化（ハッシュ文字列はbyte[]にする必要あり）
			byte[] salt = Base64.getDecoder().decode(authInfo.getSalt());
	
	        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        byte[] inputPasswordHash = skf.generateSecret(spec).getEncoded();
	        
			// 入力パスワードと登録パスワードを比較
	        byte[] collectPasswordHash = Base64.getDecoder().decode(authInfo.getPassword());
	        
	        // ログイン成功時は、userIdを返す。失敗時はnullを返す
	        if (slowEquals(inputPasswordHash,collectPasswordHash)) {
	        	return authInfo.getUserId()	;
	        } else {
	        	return null;
	        }
	        
		
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e){
	        // コンソールにエラー吐き出して、終了はしない
	        e.printStackTrace();
	        return null;
		}
	}

    // 遅い比較 (タイミング攻撃対策)
	// パスワードに違いがあっても、短い方の長さまでは必ずループを回す。
	// 違いがあっても、応答時間をなるべく引き伸ばすことで、タイミング攻撃を防ぐ
    private static boolean slowEquals(byte[] a, byte[] b) {
    	// 2つの配列の長さが同じなら0、違うなら 0以外
        int diff = a.length ^ b.length;
        // 2つの配列のうち、短い方の桁数分だけ比較する
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        // diffが0のままなら、true、0以外ならfalse
        return diff == 0;
    }
	
}
