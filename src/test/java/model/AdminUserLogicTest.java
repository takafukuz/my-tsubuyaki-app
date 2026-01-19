package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.crypto.SecretKeyFactory;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import common.DbOpeResult;
import dao.AdminUsersDAO;
import entity.NewUserForm;
import entity.NewUserInfo;
import entity.UserInfo;

class AdminUserLogicTest {

    // ---------------------------
    // getUserList()
    // ---------------------------
    @Test
    void testGetUserList_success() {

        List<UserInfo> mockList = Arrays.asList(
                new UserInfo(1, "taro", 1),
                new UserInfo(2, "hanako", 0)
        );

        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.getUserList()).thenReturn(mockList);
                })) {

            AdminUserLogic logic = new AdminUserLogic();
            List<UserInfo> result = logic.getUserList();

            assertEquals(2, result.size());
            assertEquals("taro", result.get(0).getUserName());
        }
    }

    // ---------------------------
    // getUserInfo()
    // ---------------------------
    @Test
    void testGetUserInfo_success() {

        UserInfo mockUser = new UserInfo(10, "taro", 1);

        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.getUserInfo(10)).thenReturn(mockUser);
                })) {

            AdminUserLogic logic = new AdminUserLogic();
            UserInfo result = logic.getUserInfo(10);

            assertNotNull(result);
            assertEquals("taro", result.getUserName());
        }
    }

    // ---------------------------
    // addUser() 正常系
    // ---------------------------
    @Test
    void testAddUser_success() {

        NewUserForm form = new NewUserForm();
        form.setUserName("taro");
        form.setAdminPriv(1);
        form.setPassword("pass123");

        try (MockedConstruction<AdminUsersDAO> mockedDao =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.addUser(any(NewUserInfo.class))).thenReturn(DbOpeResult.SUCCESS);
                })) {

            AdminUserLogic logic = new AdminUserLogic();
            DbOpeResult result = logic.addUser(form);

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    // ---------------------------
    // addUser() DAO が ERROR を返す
    // ---------------------------
    @Test
    void testAddUser_daoError() {

        NewUserForm form = new NewUserForm();
        form.setUserName("taro");
        form.setAdminPriv(1);
        form.setPassword("pass123");

        try (MockedConstruction<AdminUsersDAO> mockedDao =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.addUser(any(NewUserInfo.class))).thenReturn(DbOpeResult.ERROR);
                })) {

            AdminUserLogic logic = new AdminUserLogic();
            DbOpeResult result = logic.addUser(form);

            assertEquals(DbOpeResult.ERROR, result);
        }
    }

    // ---------------------------
    // addUser() ハッシュ生成で例外 → ERROR
    // ---------------------------
    @Test
    void testAddUser_hashingException() {

        NewUserForm form = new NewUserForm();
        form.setUserName("taro");
        form.setAdminPriv(1);
        form.setPassword("pass123");

        // SecretKeyFactory.getInstance が例外を投げるようにモック
        try (MockedStatic<SecretKeyFactory> mockedSkf = mockStatic(SecretKeyFactory.class)) {

            mockedSkf.when(() -> SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"))
                     .thenThrow(new NoSuchAlgorithmException("hash error"));

            AdminUserLogic logic = new AdminUserLogic();
            DbOpeResult result = logic.addUser(form);

            // addUser は例外を catch して ERROR を返す
            assertEquals(DbOpeResult.ERROR, result);
        }
    }


    // ---------------------------
    // findUsersByIds()
    // ---------------------------
    @Test
    void testFindUsersByIds_success() {

        List<UserInfo> mockList = Collections.singletonList(
                new UserInfo(1, "taro", 1)
        );

        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.findUsersByIds(Arrays.asList(1))).thenReturn(mockList);
                })) {

            AdminUserLogic logic = new AdminUserLogic();
            List<UserInfo> result = logic.findUsersByIds(Arrays.asList(1));

            assertEquals(1, result.size());
            assertEquals("taro", result.get(0).getUserName());
        }
    }

    // ---------------------------
    // delUser()
    // ---------------------------
    @Test
    void testDelUser_success() {

        try (MockedConstruction<AdminUsersDAO> mocked =
                mockConstruction(AdminUsersDAO.class, (mock, context) -> {
                    when(mock.delUser(Arrays.asList(1, 2))).thenReturn(2);
                })) {

            AdminUserLogic logic = new AdminUserLogic();
            int result = logic.delUser(Arrays.asList(1, 2));

            assertEquals(2, result);
        }
    }
}