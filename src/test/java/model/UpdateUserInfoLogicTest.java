package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import common.DbOpeResult;
import dao.UsersDAO;

public class UpdateUserInfoLogicTest {

    private UpdateUserInfoLogic logic;

    @BeforeEach
    void setup() {
        logic = new UpdateUserInfoLogic();
    }

    // -----------------------------
    // changePassword のテスト
    // -----------------------------

    @Test
    void testChangePassword_success() {
        try (MockedConstruction<UsersDAO> mocked = mockConstruction(UsersDAO.class,
                (mock, context) -> {
                    when(mock.updatePassword(anyString(), anyString(), anyString()))
                        .thenReturn(DbOpeResult.SUCCESS);
                })) {

            DbOpeResult result = logic.changePassword("u1", "pass123");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    @Test
    void testChangePassword_userIdInvalid() {
        DbOpeResult result1 = logic.changePassword(null, "pass");
        DbOpeResult result2 = logic.changePassword("", "pass");

        assertEquals(DbOpeResult.ERROR, result1);
        assertEquals(DbOpeResult.ERROR, result2);
    }

    @Test
    void testChangePassword_passwordInvalid() {
        DbOpeResult result1 = logic.changePassword("u1", null);
        DbOpeResult result2 = logic.changePassword("u1", "");

        assertEquals(DbOpeResult.ERROR, result1);
        assertEquals(DbOpeResult.ERROR, result2);
    }

    // -----------------------------
    // changeUserName のテスト
    // -----------------------------

    @Test
    void testChangeUserName_success() {
        try (MockedConstruction<UsersDAO> mocked = mockConstruction(UsersDAO.class,
                (mock, context) -> {
                    when(mock.updateUserName("u1", "Taro"))
                        .thenReturn(DbOpeResult.SUCCESS);
                })) {

            DbOpeResult result = logic.changeUserName("u1", "Taro");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    @Test
    void testChangeUserName_invalidUserId() {
        assertEquals(DbOpeResult.ERROR, logic.changeUserName(null, "Taro"));
        assertEquals(DbOpeResult.ERROR, logic.changeUserName("", "Taro"));
    }

    @Test
    void testChangeUserName_invalidUserName() {
        assertEquals(DbOpeResult.ERROR, logic.changeUserName("u1", null));
        assertEquals(DbOpeResult.ERROR, logic.changeUserName("u1", ""));
    }
}