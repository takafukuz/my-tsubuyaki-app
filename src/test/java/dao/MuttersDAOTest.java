package dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import common.DbOpeResult;
import entity.Mutter;
import infrastructure.ConnectionFactory;

class MuttersDAOTest {

    private MuttersDAO dao;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private MockedStatic<ConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() throws Exception {
        dao = new MuttersDAO();

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
    // selectAllMutters()
    // ---------------------------
    @Test
    void testSelectAllMutters_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        // 1件だけ返すケース
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("mutterid")).thenReturn(1);
        when(rs.getInt("userid")).thenReturn(10);
        when(rs.getString("username")).thenReturn("taro");
        when(rs.getString("mutter")).thenReturn("こんにちは");
        when(rs.getTimestamp("createdAt")).thenReturn(new Timestamp(1000));

        List<Mutter> list = dao.selectAllMutters();

        assertEquals(1, list.size());
        Mutter m = list.get(0);
        assertEquals(1, m.getMutterId());
        assertEquals(10, m.getUserId());
        assertEquals("taro", m.getUserName());
        assertEquals("こんにちは", m.getMutter());
    }

    @Test
    void testSelectAllMutters_empty() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        // 結果が0件
        when(rs.next()).thenReturn(false);

        List<Mutter> list = dao.selectAllMutters();

        assertTrue(list.isEmpty());
    }

    @Test
    void testSelectAllMutters_sqlException() throws Exception {
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> dao.selectAllMutters());
    }

    // ---------------------------
    // addMutter()
    // ---------------------------
    @Test
    void testAddMutter_success() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);

        DbOpeResult result = dao.addMutter(10, "テスト投稿");

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testAddMutter_error() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(0);

        DbOpeResult result = dao.addMutter(10, "テスト投稿");

        assertEquals(DbOpeResult.ERROR, result);
    }

    @Test
    void testAddMutter_sqlException() throws Exception {
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () -> dao.addMutter(10, "テスト投稿"));
    }
}