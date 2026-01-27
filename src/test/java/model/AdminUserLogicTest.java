package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import common.DbOpeResult;
import dao.AdminUsersDAO;
import entity.NewUserForm;
import entity.NewUserInfo;
import entity.UserInfo;

public class AdminUserLogicTest {

    private AdminUserLogic logic;

    @BeforeEach
    void setup() {
        logic = new AdminUserLogic();
    }

    @Test
    void testGetUserList() {
        // AdminUsersDAO のコンストラクタをモック化
        try (MockedConstruction<AdminUsersDAO> mocked = mockConstruction(AdminUsersDAO.class,
                (mock, context) -> {
                    when(mock.getUserList()).thenReturn(
                        Arrays.asList(new UserInfo("u1", "Taro", 0))
                    );
                })) {

            List<UserInfo> result = logic.getUserList();

            assertEquals(1, result.size());
            assertEquals("u1", result.get(0).getUserId());
        }
    }

    @Test
    void testGetUserInfo() {
        try (MockedConstruction<AdminUsersDAO> mocked = mockConstruction(AdminUsersDAO.class,
                (mock, context) -> {
                    when(mock.getUserInfo("u1"))
                        .thenReturn(new UserInfo("u1", "Taro", 1));
                })) {

            UserInfo result = logic.getUserInfo("u1");

            assertNotNull(result);
            assertEquals("u1", result.getUserId());
            assertEquals("Taro", result.getUserName());
        }
    }

    @Test
    void testAddUser_success() {
        NewUserForm form = new NewUserForm();
        form.setUserName("Taro");
        form.setAdminPriv(1);
        form.setPassword("pass123");

        try (MockedConstruction<AdminUsersDAO> mocked = mockConstruction(AdminUsersDAO.class,
                (mock, context) -> {
                    when(mock.addUser(any(NewUserInfo.class)))
                        .thenReturn(DbOpeResult.SUCCESS);
                })) {

            DbOpeResult result = logic.addUser(form);

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    @Test
    void testAddUser_error() {
        NewUserForm form = new NewUserForm();
        form.setUserName("Taro");
        form.setAdminPriv(1);
        form.setPassword(null); // パスワード null → IllegalArgumentException

        DbOpeResult result = logic.addUser(form);

        assertEquals(DbOpeResult.ERROR, result);
    }

    @Test
    void testFindUsersByIds() {
        List<String> ids = Arrays.asList("u1", "u2");

        try (MockedConstruction<AdminUsersDAO> mocked = mockConstruction(AdminUsersDAO.class,
                (mock, context) -> {
                    when(mock.findUsersByIds(ids))
                        .thenReturn(Arrays.asList(
                            new UserInfo("u1", "Taro", 0),
                            new UserInfo("u2", "Hanako", 1)
                        ));
                })) {

            List<UserInfo> result = logic.findUsersByIds(ids);

            assertEquals(2, result.size());
        }
    }

    @Test
    void testDelUser() {
        List<String> ids = Arrays.asList("u1", "u2");

        try (MockedConstruction<AdminUsersDAO> mocked = mockConstruction(AdminUsersDAO.class,
                (mock, context) -> {
                    when(mock.delUser(ids)).thenReturn(2);
                })) {

            int result = logic.delUser(ids);

            assertEquals(2, result);
        }
    }
}