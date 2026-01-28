package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import common.DbOpeResult;
import dao.MuttersDAO;
import dao.UsersDAO;
import entity.Mutter;

public class MutterLogicTest {

    private MutterLogic logic;

    @BeforeEach
    void setup() {
        logic = new MutterLogic();
    }

    // -----------------------------
    // getAllMutters のテスト
    // -----------------------------
    @Test
    void testGetAllMutters_success() {

        try (
            MockedConstruction<MuttersDAO> mockedMutters =
                mockConstruction(MuttersDAO.class, (mock, context) -> {

                    when(mock.selectAllMutters()).thenReturn(
                        Arrays.asList(
                            new Mutter("m1", "u1", "Taro", "Hello",
                                new Timestamp(System.currentTimeMillis())),
                            new Mutter("m2", "u2", "Hanako", "World",
                                new Timestamp(System.currentTimeMillis()))
                        )
                    );
                });

            MockedConstruction<UsersDAO> mockedUsers =
                mockConstruction(UsersDAO.class, (mock, context) -> {

                    when(mock.findUserByIds(Set.of("u1", "u2")))
                        .thenReturn(Map.of(
                            "u1", "Taro",
                            "u2", "Hanako"
                        ));
                })
        ) {

            List<Mutter> result = logic.getAllMutters();

            assertEquals(2, result.size());

            // 1件目
            assertEquals("m1", result.get(0).getMutterId());
            assertEquals("u1", result.get(0).getUserId());
            assertEquals("Taro", result.get(0).getUserName());
            assertEquals("Hello", result.get(0).getMutter());

            // 2件目
            assertEquals("m2", result.get(1).getMutterId());
            assertEquals("u2", result.get(1).getUserId());
            assertEquals("Hanako", result.get(1).getUserName());
            assertEquals("World", result.get(1).getMutter());
        }
    }

    // -----------------------------
    // addMutter のテスト
    // -----------------------------
    @Test
    void testAddMutter_success() {

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {

                    when(mock.addMutter("u1", "Hello"))
                        .thenReturn(DbOpeResult.SUCCESS);
                })) {

            DbOpeResult result = logic.addMutter("u1", "Hello");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }

    // -----------------------------
    // delMutter のテスト
    // -----------------------------
    @Test
    void testDelMutter_success() {

        try (MockedConstruction<MuttersDAO> mocked =
                mockConstruction(MuttersDAO.class, (mock, context) -> {

                    when(mock.delMutter("u1", "m1"))
                        .thenReturn(DbOpeResult.SUCCESS);
                })) {

            DbOpeResult result = logic.delMutter("u1", "m1");

            assertEquals(DbOpeResult.SUCCESS, result);
        }
    }
}