package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import dao.AdminUsersDAO;
import entity.AuthInfo;

class AdminLoginLogicTest {

    // パスワードハッシュを生成する補助メソッド
    private String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // ---------------------------
    // 1. ユーザーが存在しない
    // ---------------------------
    @Test
    void testCanLogin_userNotFound() {
        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(null);
                })) {

            AdminLoginLogic logic = new AdminLoginLogic();
            String result = logic.canLogin("taro", "pass");

            assertNull(result);
        }
    }

    // ---------------------------
    // 2. パスワード一致 → userId を返す
    // ---------------------------
    @Test
    void testCanLogin_success() throws Exception {

        String password = "secret123";
        byte[] salt = "1234567890123456".getBytes(); // 16バイト固定
        String saltStr = Base64.getEncoder().encodeToString(salt);

        String hashedPassword = hashPassword(password, salt);

        AuthInfo authInfo = new AuthInfo(hashedPassword, saltStr, "99");

        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(authInfo);
                })) {

            AdminLoginLogic logic = new AdminLoginLogic();
            String result = logic.canLogin("taro", "secret123");

            assertEquals("99", result);
        }
    }

    // ---------------------------
    // 3. パスワード不一致 → null
    // ---------------------------
    @Test
    void testCanLogin_wrongPassword() throws Exception {

        String correctPassword = "secret123";
        byte[] salt = "1234567890123456".getBytes();
        String saltStr = Base64.getEncoder().encodeToString(salt);

        String hashedPassword = hashPassword(correctPassword, salt);

        AuthInfo authInfo = new AuthInfo(hashedPassword, saltStr, "99");

        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(authInfo);
                })) {

            AdminLoginLogic logic = new AdminLoginLogic();
            String result = logic.canLogin("taro", "wrongPassword");

            assertNull(result);
        }
    }

    // ---------------------------
    // 4. ハッシュ生成処理で例外 → null
    // ---------------------------
    @Test
    void testCanLogin_hashingThrowsException() {

        // DAO は正常に AuthInfo を返す（例外は投げない）
        AuthInfo authInfo = new AuthInfo("dummyHash", "dummySalt", "99");

        try (MockedConstruction<AdminUsersDAO> mockedDao =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(authInfo);
                });
             MockedStatic<SecretKeyFactory> mockedSkf = mockStatic(SecretKeyFactory.class)) {

            // SecretKeyFactory.getInstance が例外を投げるようにする
            mockedSkf.when(() -> SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"))
                     .thenThrow(new RuntimeException("hash error"));

            AdminLoginLogic logic = new AdminLoginLogic();
            String result = logic.canLogin("taro", "pass");

            assertNull(result);
        }
    }
}