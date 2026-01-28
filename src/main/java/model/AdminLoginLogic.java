package model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import dao.AdminUsersDAO;
import entity.AuthInfo;

public class AdminLoginLogic {
	
	// 繰り返しとハッシュの長さを指定
    private static int iterations = 10000; // 反復回数
    private static int keyLength = 256;    // ハッシュの長さ
	
	public String canLogin(String username, String password) {
		
		try {
			AdminUsersDAO dao = new AdminUsersDAO();
			AuthInfo authInfo = dao.getPassword(username);
			
			if (authInfo == null) {
				System.out.println("認証失敗" + username + "のパスワード情報がありませんでした。");
				return null;
			}
			
			// DBから取得したソルト文字列をデコード（byte[]に変換）する
			byte[] saltBytes = Base64.getDecoder().decode(authInfo.getSalt());
			
			// 入力されたパスワード文字列をソルトをもとにハッシュ化する（byte[]）
	        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, keyLength);
	        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	        byte[] inputPasswordHash = skf.generateSecret(spec).getEncoded();
	        
	        // DBから取得したハッシュ化済パスワード文字列をデコード（byte[]に変換する）
	        byte[] collectPasswordHash = Base64.getDecoder().decode(authInfo.getPassword());
	        
	        // slowEqualsメソッドを使って、2つのパスワードを比較する。ログイン成功時は、ユーザーIDを返す
	        if ( slowEquals(inputPasswordHash,collectPasswordHash)) {
	        	return authInfo.getUserId();
	        } else {
	        	return null;
	        }
		
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
	        // コンソールにエラー出力。終了せずにnullで返す。
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
