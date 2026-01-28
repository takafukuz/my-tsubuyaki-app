package dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import common.DbOpeResult;
import entity.AuthInfo;
import entity.NewUserInfo;
import entity.UserInfo;
import infrastructure.AppConfig;
import infrastructure.DynamoDbClientHolder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.CancellationReason;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

class AdminUsersDAOTest {

    private AdminUsersDAO dao;
    private DynamoDbClient mockDynamo;
    private AppConfig mockConfig;

    private MockedStatic<DynamoDbClientHolder> dynamoMockStatic;
    private MockedStatic<AppConfig> appConfigMockStatic;

    @BeforeEach
    void setUp() {
        mockDynamo = mock(DynamoDbClient.class);
        mockConfig = mock(AppConfig.class);

        dynamoMockStatic = Mockito.mockStatic(DynamoDbClientHolder.class);
        appConfigMockStatic = Mockito.mockStatic(AppConfig.class);

        dynamoMockStatic.when(DynamoDbClientHolder::getInstance).thenReturn(mockDynamo);
        appConfigMockStatic.when(AppConfig::getInstance).thenReturn(mockConfig);

        when(mockConfig.getUsersTable()).thenReturn("UsersTable");
        when(mockConfig.getUsernamesTable()).thenReturn("UsernamesTable");

        dao = new AdminUsersDAO();
    }

    @AfterEach
    void tearDown() {
        dynamoMockStatic.close();
        appConfigMockStatic.close();
    }

    // ---------------------------------------------------------
    // getPassword()
    // ---------------------------------------------------------
    @Test
    void testGetPassword_success() {

        Map<String, AttributeValue> item = Map.of(
                "password_hash", AttributeValue.fromS("hash123"),
                "salt", AttributeValue.fromS("salt123"),
                "userid", AttributeValue.fromS("u1")
        );

        QueryResponse response = QueryResponse.builder()
                .items(item)
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        AuthInfo result = dao.getPassword("taro");

        assertNotNull(result);
        assertEquals("hash123", result.getPassword());
        assertEquals("salt123", result.getSalt());
        assertEquals("u1", result.getUserId());
    }

    @Test
    void testGetPassword_notFound() {

        QueryResponse response = QueryResponse.builder()
                .items(List.of())
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        AuthInfo result = dao.getPassword("unknown");

        assertNull(result);
    }

    @Test
    void testGetPassword_error() {

        when(mockDynamo.query(any(QueryRequest.class)))
                .thenThrow(DynamoDbException.builder().build());

        AuthInfo result = dao.getPassword("error");

        assertNull(result);
    }

    // ---------------------------------------------------------
    // getUserList()
    // ---------------------------------------------------------
    @Test
    void testGetUserList_success() {

        Map<String, AttributeValue> item1 = Map.of(
                "userid", AttributeValue.fromS("u1"),
                "username", AttributeValue.fromS("taro"),
                "adminpriv", AttributeValue.fromN("1")
        );

        Map<String, AttributeValue> item2 = Map.of(
                "userid", AttributeValue.fromS("u2"),
                "username", AttributeValue.fromS("hanako"),
                "adminpriv", AttributeValue.fromN("0")
        );

        QueryResponse response = QueryResponse.builder()
                .items(item1, item2)
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        List<UserInfo> list = dao.getUserList();

        assertEquals(2, list.size());
        assertEquals("taro", list.get(0).getUserName());
        assertEquals("hanako", list.get(1).getUserName());
    }

    @Test
    void testGetUserList_empty() {

        QueryResponse response = QueryResponse.builder()
                .items(List.of())
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        List<UserInfo> list = dao.getUserList();

        assertTrue(list.isEmpty());
    }

    @Test
    void testGetUserList_error() {

        when(mockDynamo.query(any(QueryRequest.class)))
                .thenThrow(DynamoDbException.builder().build());

        List<UserInfo> list = dao.getUserList();

        assertTrue(list.isEmpty());
    }

    // ---------------------------------------------------------
    // getUserInfo()
    // ---------------------------------------------------------
    @Test
    void testGetUserInfo_success() {

        Map<String, AttributeValue> item = Map.of(
                "userid", AttributeValue.fromS("u1"),
                "username", AttributeValue.fromS("taro"),
                "adminpriv", AttributeValue.fromN("1")
        );

        GetItemResponse response = GetItemResponse.builder()
                .item(item)
                .build();

        when(mockDynamo.getItem(any(GetItemRequest.class))).thenReturn(response);

        UserInfo user = dao.getUserInfo("u1");

        assertNotNull(user);
        assertEquals("u1", user.getUserId());
        assertEquals("taro", user.getUserName());
        assertEquals(1, user.getAdminPriv());
    }

    @Test
    void testGetUserInfo_notFound() {

        GetItemResponse response = GetItemResponse.builder()
                .item(Map.of())
                .build();

        when(mockDynamo.getItem(any(GetItemRequest.class))).thenReturn(response);

        UserInfo user = dao.getUserInfo("unknown");

        assertNull(user);
    }

    @Test
    void testGetUserInfo_error() {

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenThrow(DynamoDbException.builder().build());

        UserInfo user = dao.getUserInfo("error");

        assertNull(user);
    }

    // ---------------------------------------------------------
    // updateUserInfo()
    // ---------------------------------------------------------
    @Test
    void testUpdateUserInfo_success_noUserNameChange() {

        Map<String, AttributeValue> item = Map.of(
                "username", AttributeValue.fromS("taro")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        DbOpeResult result = dao.updateUserInfo(new UserInfo("u1", "taro", 1));

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testUpdateUserInfo_success_withUserNameChange() {

        Map<String, AttributeValue> item = Map.of(
                "username", AttributeValue.fromS("oldName")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        DbOpeResult result = dao.updateUserInfo(new UserInfo("u1", "newName", 1));

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testUpdateUserInfo_duplicateUserName() {

        Map<String, AttributeValue> item = Map.of(
                "username", AttributeValue.fromS("oldName")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        TransactionCanceledException ex = TransactionCanceledException.builder()
                .cancellationReasons(
                        List.of(CancellationReason.builder().code("ConditionalCheckFailed").build())
                )
                .build();

        doThrow(ex).when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        DbOpeResult result = dao.updateUserInfo(new UserInfo("u1", "newName", 1));

        assertEquals(DbOpeResult.DUPLICATE, result);
    }

    @Test
    void testUpdateUserInfo_error() {

        Map<String, AttributeValue> item = Map.of(
                "username", AttributeValue.fromS("oldName")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        doThrow(DynamoDbException.builder().build())
                .when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        DbOpeResult result = dao.updateUserInfo(new UserInfo("u1", "newName", 1));

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------------------------------------
    // addUser()
    // ---------------------------------------------------------
    @Test
    void testAddUser_success() {

        NewUserInfo newUser = new NewUserInfo("taro", 1, "hash", "salt");

        DbOpeResult result = dao.addUser(newUser);

        assertEquals(DbOpeResult.SUCCESS, result);
    }

    @Test
    void testAddUser_duplicate() {

        NewUserInfo newUser = new NewUserInfo("taro", 1, "hash", "salt");

        TransactionCanceledException ex = TransactionCanceledException.builder()
                .cancellationReasons(
                        List.of(CancellationReason.builder().code("ConditionalCheckFailed").build())
                )
                .build();

        doThrow(ex).when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        DbOpeResult result = dao.addUser(newUser);

        assertEquals(DbOpeResult.DUPLICATE, result);
    }

    @Test
    void testAddUser_error() {

        NewUserInfo newUser = new NewUserInfo("taro", 1, "hash", "salt");

        doThrow(DynamoDbException.builder().build())
                .when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        DbOpeResult result = dao.addUser(newUser);

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------------------------------------
    // findUsersByIds()
    // ---------------------------------------------------------
    @Test
    void testFindUsersByIds_success() {

        Map<String, AttributeValue> item = Map.of(
                "userid", AttributeValue.fromS("u1"),
                "username", AttributeValue.fromS("taro"),
                "adminpriv", AttributeValue.fromN("1")
        );

        Map<String, List<Map<String, AttributeValue>>> responses =
                Map.of("UsersTable", List.of(item));

        BatchGetItemResponse response = BatchGetItemResponse.builder()
                .responses(responses)
                .unprocessedKeys(Collections.emptyMap())
                .build();

        when(mockDynamo.batchGetItem(any(BatchGetItemRequest.class)))
                .thenReturn(response);

        List<UserInfo> list = dao.findUsersByIds(List.of("u1"));

        assertEquals(1, list.size());
        assertEquals("taro", list.get(0).getUserName());
    }

    @Test
    void testFindUsersByIds_emptyInput() {

        List<UserInfo> list = dao.findUsersByIds(Collections.emptyList());

        assertTrue(list.isEmpty());
    }

    @Test
    void testFindUsersByIds_error() {

        when(mockDynamo.batchGetItem(any(BatchGetItemRequest.class)))
                .thenThrow(DynamoDbException.builder().build());

        assertThrows(DynamoDbException.class,
                () -> dao.findUsersByIds(List.of("u1")));
    }
    
 // -----------------------------
    // 正常系：1件削除成功
    // -----------------------------
    @Test
    void testDelUser_success() {

        // getItem の戻り値（username を返す）
        Map<String, AttributeValue> item =
                Map.of("username", AttributeValue.fromS("taro"));

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        // トランザクション成功
        when(mockDynamo.transactWriteItems(any(TransactWriteItemsRequest.class)))
        .thenReturn(TransactWriteItemsResponse.builder().build());

        int result = dao.delUser(List.of("u1"));

        assertEquals(1, result);
    }

    // -----------------------------
    // getItem が例外 → 0件
    // -----------------------------
    @Test
    void testDelUser_getItemException() {

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenThrow(DynamoDbException.builder().message("error").build());

        int result = dao.delUser(List.of("u1"));

        assertEquals(0, result);
    }

    // -----------------------------
    // getItem が空 → 0件
    // -----------------------------
    @Test
    void testDelUser_itemEmpty() {

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(Map.of()).build());

        int result = dao.delUser(List.of("u1"));

        assertEquals(0, result);
    }

    // -----------------------------
    // トランザクション失敗（ConditionalCheckFailed）→ 0件
    // -----------------------------
    @Test
    void testDelUser_transactionConditionalFail() {

        Map<String, AttributeValue> item =
                Map.of("username", AttributeValue.fromS("taro"));

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        // トランザクション失敗（条件不一致）
        TransactionCanceledException ex =
                TransactionCanceledException.builder()
                        .cancellationReasons(
                                List.of(CancellationReason.builder()
                                        .code("ConditionalCheckFailed")
                                        .build()))
                        .build();

        doThrow(ex).when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        int result = dao.delUser(List.of("u1"));

        assertEquals(0, result);
    }

    // -----------------------------
    // トランザクション失敗（その他の例外）→ 0件
    // -----------------------------
    @Test
    void testDelUser_transactionOtherError() {

        Map<String, AttributeValue> item =
                Map.of("username", AttributeValue.fromS("taro"));

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        doThrow(DynamoDbException.builder().message("error").build())
                .when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        int result = dao.delUser(List.of("u1"));

        assertEquals(0, result);
    }
}