package dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import common.DbOpeResult;
import entity.AuthInfo;
import infrastructure.ConnectionFactory;

class UsersDAOTest {

    private UsersDAO dao;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private MockedStatic<ConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() throws Exception {
        dao = new UsersDAO();

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

        AuthInfo result = dao.getPassword("taro");

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

    @Test
    void testGetPassword_sqlException() throws Exception {
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        AuthInfo result = dao.getPassword("taro");
        assertNull(result);
    }

    // ---------------------------
    // updatePassword()
    // ---------------------------
    @Test
    void testUpdatePassword_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        DbOpeResult result = dao.updatePassword(1, "hash", "salt");

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testUpdatePassword_error() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        DbOpeResult result = dao.updatePassword(1, "hash", "salt");

        assertEquals(DbOpeResult.ERROR, result);
    }

    @Test
    void testUpdatePassword_sqlException() throws Exception {
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        DbOpeResult result = dao.updatePassword(1, "hash", "salt");

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------
    // updateUserName()
    // ---------------------------
    @Test
    void testUpdateUserName_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        DbOpeResult result = dao.updateUserName(1, "taro");

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testUpdateUserName_error() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        DbOpeResult result = dao.updateUserName(1, "taro");

        assertEquals(DbOpeResult.ERROR, result);
    }

    @Test
    void testUpdateUserName_duplicate() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);

        SQLException ex = new SQLException("duplicate", "state", 23505);
        when(pstmt.executeUpdate()).thenThrow(ex);

        DbOpeResult result = dao.updateUserName(1, "taro");

        assertEquals(DbOpeResult.DUPLICATE, result);
    }

    @Test
    void testUpdateUserName_sqlException() throws Exception {
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        DbOpeResult result = dao.updateUserName(1, "taro");

        assertEquals(DbOpeResult.ERROR, result);
    }
}