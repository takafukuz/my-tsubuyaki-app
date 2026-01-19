package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import dao.UsersDAO;
import entity.AuthInfo;

class LoginLogicTest {

    // ---------------------------
    // 1. ユーザーが存在しない → null
    // ---------------------------
    @Test
    void testCanLogin_userNotFound() {

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(null);
                })) {

            LoginLogic logic = new LoginLogic();
            Integer result = logic.canLogin("taro", "pass");

            assertNull(result);
        }
    }

    // ---------------------------
    // 2. パスワード一致 → userId を返す
    // ---------------------------
    @Test
    void testCanLogin_success() throws Exception {

        String password = "secret123";
        byte[] salt = "1234567890123456".getBytes();
        String saltStr = Base64.getEncoder().encodeToString(salt);

        // 入力パスワードをハッシュ化
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(
                new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 10000, 256)
        ).getEncoded();
        String hashStr = Base64.getEncoder().encodeToString(hash);

        AuthInfo authInfo = new AuthInfo(hashStr, saltStr, 99);

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(authInfo);
                })) {

            LoginLogic logic = new LoginLogic();
            Integer result = logic.canLogin("taro", "secret123");

            assertEquals(99, result);
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

        // 正しいパスワードのハッシュ
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] correctHash = skf.generateSecret(
                new javax.crypto.spec.PBEKeySpec(correctPassword.toCharArray(), salt, 10000, 256)
        ).getEncoded();
        String correctHashStr = Base64.getEncoder().encodeToString(correctHash);

        AuthInfo authInfo = new AuthInfo(correctHashStr, saltStr, 99);

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(authInfo);
                })) {

            LoginLogic logic = new LoginLogic();
            Integer result = logic.canLogin("taro", "wrongPassword");

            assertNull(result);
        }
    }

    // ---------------------------
    // 4. ハッシュ生成で例外 → null
    // ---------------------------
    @Test
    void testCanLogin_hashingException() {

        AuthInfo authInfo = new AuthInfo("dummyHash", "dummySalt", 99);

        try (MockedConstruction<UsersDAO> mockedDao =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.getPassword("taro")).thenReturn(authInfo);
                });
             MockedStatic<SecretKeyFactory> mockedSkf = mockStatic(SecretKeyFactory.class)) {

            // SecretKeyFactory.getInstance が例外を投げる
            mockedSkf.when(() -> SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"))
                     .thenThrow(new NoSuchAlgorithmException("hash error"));

            LoginLogic logic = new LoginLogic();
            Integer result = logic.canLogin("taro", "pass");

            assertNull(result);
        }
    }
}