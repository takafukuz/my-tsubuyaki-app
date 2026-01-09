package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int iterations = 10000; // LoginLogic と同じ
    private static final int keyLength = 256;    // LoginLogic と同じ
    private static final int saltLength = 16;    // 16バイト推奨

    // ソルト生成
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // パスワードハッシュ生成
    public static String hashPassword(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("パスワードハッシュ生成に失敗しました", e);
        }
    }

    // テスト用：ソルトとハッシュをまとめて生成
    public static void main(String[] args) {
        String password = "takayuki";

        String salt = generateSalt();
        String hash = hashPassword(password, salt);

        System.out.println("ソルト: " + salt);
        System.out.println("ハッシュ: " + hash);
    }
}