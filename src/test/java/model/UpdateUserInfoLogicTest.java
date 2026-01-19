package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKeyFactory;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import common.DbOpeResult;
import dao.UsersDAO;

class UpdateUserInfoLogicTest {

    // ---------------------------
    // changePassword() 正常系
    // ---------------------------
    @Test
    void testChangePassword_success() {

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.updatePassword(anyInt(), anyString(), anyString()))
                            .thenReturn(DbOpeResult.SUCCESS);
                })) {

            UpdateUserInfoLogic logic = new UpdateUserInfoLogic();
            DbOpeResult result = logic.changePassword(10, "newpass");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    // ---------------------------
    // changePassword() DAO が ERROR を返す
    // ---------------------------
    @Test
    void testChangePassword_daoError() {

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.updatePassword(anyInt(), anyString(), anyString()))
                            .thenReturn(DbOpeResult.ERROR);
                })) {

            UpdateUserInfoLogic logic = new UpdateUserInfoLogic();
            DbOpeResult result = logic.changePassword(10, "newpass");

            assertEquals(DbOpeResult.ERROR, result);
        }
    }

    // ---------------------------
    // changePassword() ハッシュ生成で例外 → RuntimeException
    // ---------------------------
    @Test
    void testChangePassword_hashingException() {

        try (MockedStatic<SecretKeyFactory> mockedSkf = mockStatic(SecretKeyFactory.class)) {

            // SecretKeyFactory.getInstance が NoSuchAlgorithmException を投げる
            mockedSkf.when(() -> SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"))
                     .thenThrow(new NoSuchAlgorithmException("hash error"));

            UpdateUserInfoLogic logic = new UpdateUserInfoLogic();

            assertThrows(RuntimeException.class,
                    () -> logic.changePassword(10, "newpass"));
        }
    }

    // ---------------------------
    // changeUserName() 正常系
    // ---------------------------
    @Test
    void testChangeUserName_success() {

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.updateUserName(10, "taro"))
                            .thenReturn(DbOpeResult.SUCCESS);
                })) {

            UpdateUserInfoLogic logic = new UpdateUserInfoLogic();
            DbOpeResult result = logic.changeUserName(10, "taro");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    // ---------------------------
    // changeUserName() DAO が ERROR を返す
    // ---------------------------
    @Test
    void testChangeUserName_error() {

        try (MockedConstruction<UsersDAO> mocked =
                mockConstruction(UsersDAO.class, (mock, context) -> {
                    when(mock.updateUserName(10, "taro"))
                            .thenReturn(DbOpeResult.ERROR);
                })) {

            UpdateUserInfoLogic logic = new UpdateUserInfoLogic();
            DbOpeResult result = logic.changeUserName(10, "taro");

            assertEquals(DbOpeResult.ERROR, result);
        }
    }
}