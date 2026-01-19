package dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import common.DbOpeResult;
import entity.AuthInfo;
import entity.NewUserInfo;
import entity.UserInfo;
import infrastructure.ConnectionFactory;

class AdminUsersDAOTest {

    private AdminUsersDAO dao;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private MockedStatic<ConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() throws Exception {
        dao = new AdminUsersDAO();

        conn = mock(Connection.class);
        pstmt = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);

        mockedFactory = mockStatic(ConnectionFactory.class);
        mockedFactory.when(ConnectionFactory::getConnection).thenReturn(conn);
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    // ---------------------------
    // getPassword()
    // ---------------------------
    @Test
    void testGetPassword_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("password_hash")).thenReturn("hash123");
        when(rs.getString("salt")).thenReturn("salt123");
        when(rs.getInt("userid")).thenReturn(10);

        AuthInfo result = dao.getPassword("admin");

        assertNotNull(result);
        assertEquals("hash123", result.getPassword());
        assertEquals("salt123", result.getSalt());
        assertEquals(10, result.getUserId());
    }

    @Test
    void testGetPassword_notFound() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        AuthInfo result = dao.getPassword("unknown");
        assertNull(result);
    }

    // ---------------------------
    // getUserList()
    // ---------------------------
    @Test
    void testGetUserList_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("userid")).thenReturn(1, 2);
        when(rs.getString("username")).thenReturn("taro", "hanako");
        when(rs.getInt("adminpriv")).thenReturn(1, 0);

        List<UserInfo> list = dao.getUserList();

        assertEquals(2, list.size());
        assertEquals("taro", list.get(0).getUserName());
        assertEquals("hanako", list.get(1).getUserName());
    }

    // ---------------------------
    // getUserInfo()
    // ---------------------------
    @Test
    void testGetUserInfo_found() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("userid")).thenReturn(5);
        when(rs.getString("username")).thenReturn("taro");
        when(rs.getInt("adminpriv")).thenReturn(1);

        UserInfo user = dao.getUserInfo(5);

        assertNotNull(user);
        assertEquals(5, user.getUserId());
        assertEquals("taro", user.getUserName());
    }

    @Test
    void testGetUserInfo_notFound() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        UserInfo user = dao.getUserInfo(99);
        assertNull(user);
    }

    // ---------------------------
    // updateUserInfo()
    // ---------------------------
    @Test
    void testUpdateUserInfo_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        UserInfo user = new UserInfo(1, "taro", 1);
        DbOpeResult result = dao.updateUserInfo(user);

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testUpdateUserInfo_error() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        UserInfo user = new UserInfo(1, "taro", 1);
        DbOpeResult result = dao.updateUserInfo(user);

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------
    // addUser()
    // ---------------------------
    @Test
    void testAddUser_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        NewUserInfo newUser = new NewUserInfo("taro", 1, "hash", "salt");
        DbOpeResult result = dao.addUser(newUser);

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testAddUser_duplicate() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        SQLException ex = new SQLException("duplicate", "state", 1062);
        when(pstmt.executeUpdate()).thenThrow(ex);

        NewUserInfo newUser = new NewUserInfo("taro", 1, "hash", "salt");
        DbOpeResult result = dao.addUser(newUser);

        assertEquals(DbOpeResult.DUPLICATE, result);
    }

    // ---------------------------
    // findUsersByIds()
    // ---------------------------
    @Test
    void testFindUsersByIds_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("userid")).thenReturn(1);
        when(rs.getString("username")).thenReturn("taro");
        when(rs.getInt("adminpriv")).thenReturn(1);

        List<UserInfo> list = dao.findUsersByIds(Arrays.asList(1));

        assertEquals(1, list.size());
        assertEquals("taro", list.get(0).getUserName());
    }

    // ---------------------------
    // delUser()
    // ---------------------------
    @Test
    void testDelUser_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(2);

        int result = dao.delUser(Arrays.asList(1, 2));

        assertEquals(2, result);
    }

    @Test
    void testDelUser_error() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        when(pstmt.executeUpdate()).thenThrow(new SQLException());

        int result = dao.delUser(Arrays.asList(1, 2));

        assertEquals(-1, result);
    }
}