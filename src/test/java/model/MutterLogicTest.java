package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import common.DbOpeResult;
import dao.MuttersDAO;
import entity.Mutter;

class MutterLogicTest {

    // ---------------------------
    // getAllMutters()
    // ---------------------------
    @Test
    void testGetAllMutters_success() {

        List<Mutter> mockList = Arrays.asList(
                new Mutter(1, 10, "taro", "hello", Timestamp.valueOf("2024-01-01 10:00:00")),
                new Mutter(2, 20, "hanako", "good morning", Timestamp.valueOf("2024-01-02 11:00:00"))
        );

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {
                    when(mock.selectAllMutters()).thenReturn(mockList);
                })) {

            MutterLogic logic = new MutterLogic();
            List<Mutter> result = logic.getAllMutters();

            assertEquals(2, result.size());
            assertEquals("hello", result.get(0).getMutter());
            assertEquals("taro", result.get(0).getUserName());
        }
    }

    // ---------------------------
    // addMutter() SUCCESS
    // ---------------------------
    @Test
    void testAddMutter_success() {

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {
                    when(mock.addMutter(10, "test")).thenReturn(DbOpeResult.SUCCESS);
                })) {

            MutterLogic logic = new MutterLogic();
            DbOpeResult result = logic.addMutter(10, "test");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    // ---------------------------
    // addMutter() ERROR
    // ---------------------------
    @Test
    void testAddMutter_error() {

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {
                    when(mock.addMutter(10, "test")).thenReturn(DbOpeResult.ERROR);
                })) {

            MutterLogic logic = new MutterLogic();
            DbOpeResult result = logic.addMutter(10, "test");

            assertEquals(DbOpeResult.ERROR, result);
        }
    }
    
    // ---------------------------
    // delMutter() SUCCESS
    // ---------------------------
    @Test
    void testDelMutter_success() {

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {
                    when(mock.delMutter(10, 1)).thenReturn(DbOpeResult.SUCCESS);
                })) {

            MutterLogic logic = new MutterLogic();
            DbOpeResult result = logic.delMutter(10, 1);

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    // ---------------------------
    // delMutter() ERROR
    // ---------------------------
    @Test
    void testDelMutter_error() {

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {
                    when(mock.delMutter(10, 1)).thenReturn(DbOpeResult.ERROR);
                })) {

            MutterLogic logic = new MutterLogic();
            DbOpeResult result = logic.delMutter(10, 1);

            assertEquals(DbOpeResult.ERROR, result);
        }
    }

    
    
}