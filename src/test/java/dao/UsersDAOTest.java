package dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import common.DbOpeResult;
import entity.AuthInfo;
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
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

class UsersDAOTest {

    @Mock
    DynamoDbClient mockDynamo;

    @Mock
    AppConfig mockConfig;

    private UsersDAO dao;

    private MockedStatic<DynamoDbClientHolder> dynamoHolderMock;
    private MockedStatic<AppConfig> appConfigMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dynamoHolderMock = mockStatic(DynamoDbClientHolder.class);
        appConfigMock = mockStatic(AppConfig.class);

        dynamoHolderMock.when(DynamoDbClientHolder::getInstance).thenReturn(mockDynamo);
        appConfigMock.when(AppConfig::getInstance).thenReturn(mockConfig);

        when(mockConfig.getUsersTable()).thenReturn("UsersTable");
        when(mockConfig.getUsernamesTable()).thenReturn("UsernamesTable");

        dao = new UsersDAO();
    }

    @AfterEach
    void tearDown() {
        dynamoHolderMock.close();
        appConfigMock.close();
    }

    // ---------------------------------------------------------
    // getPassword()
    // ---------------------------------------------------------
    @Test
    void testGetPasswordSuccess() {

        Map<String, AttributeValue> item = Map.of(
                "password_hash", AttributeValue.fromS("hash123"),
                "salt", AttributeValue.fromS("salt123"),
                "userid", AttributeValue.fromS("u1")
        );

        QueryResponse response = QueryResponse.builder()
                .items(item)
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        AuthInfo auth = dao.getPassword("alice");

        assertNotNull(auth);
        assertEquals("hash123", auth.getPassword());
        assertEquals("salt123", auth.getSalt());
        assertEquals("u1", auth.getUserId());
    }

    @Test
    void testGetPasswordNotFound() {

        QueryResponse response = QueryResponse.builder()
                .items(List.of()) // 空リスト
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        AuthInfo auth = dao.getPassword("unknown");

        assertNull(auth);
    }

    @Test
    void testGetPasswordError() {

        when(mockDynamo.query(any(QueryRequest.class)))
                .thenThrow(DynamoDbException.builder().build());

        AuthInfo auth = dao.getPassword("error");

        assertNull(auth);
    }

    // ---------------------------------------------------------
    // updatePassword()
    // ---------------------------------------------------------
    @Test
    void testUpdatePasswordSuccess() {

        DbOpeResult result = dao.updatePassword("u1", "newHash", "newSalt");

        assertEquals(DbOpeResult.SUCCESS, result);
        verify(mockDynamo).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void testUpdatePasswordError() {

        doThrow(DynamoDbException.builder().build())
                .when(mockDynamo).updateItem(any(UpdateItemRequest.class));

        DbOpeResult result = dao.updatePassword("u1", "newHash", "newSalt");

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------------------------------------
    // updateUserName()
    // ---------------------------------------------------------
    @Test
    void testUpdateUserNameSuccess() {

        // getItem → 現在のユーザー名を返す
        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("oldName")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(userItem).build());

        // transactWriteItems → 成功
        DbOpeResult result = dao.updateUserName("u1", "newName");

        assertEquals(DbOpeResult.SUCCESS, result);
        verify(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));
    }

    @Test
    void testUpdateUserNameUserNotFound() {

        // username が null → ERROR
        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(userItem).build());

        DbOpeResult result = dao.updateUserName("u1", "newName");

        assertEquals(DbOpeResult.ERROR, result);
    }

    @Test
    void testUpdateUserNameSameName() {

        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("sameName")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(userItem).build());

        DbOpeResult result = dao.updateUserName("u1", "sameName");

        assertEquals(DbOpeResult.ERROR, result);
    }

    @Test
    void testUpdateUserNameDuplicate() {

        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("oldName")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(userItem).build());

        // トランザクション失敗（重複）
        TransactionCanceledException ex = TransactionCanceledException.builder()
                .cancellationReasons(
                        List.of(CancellationReason.builder().code("ConditionalCheckFailed").build())
                )
                .build();

        doThrow(ex).when(mockDynamo).transactWriteItems(any(TransactWriteItemsRequest.class));

        DbOpeResult result = dao.updateUserName("u1", "newName");

        assertEquals(DbOpeResult.DUPLICATE, result);
    }
    
    @Test
    void testUpdateUserName_UserNotFound() {

        // getItem の返却値に username が無い（＝ユーザーが存在しない扱い）
        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("")   // 空文字
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(userItem).build());

        DbOpeResult result = dao.updateUserName("u1", "newName");

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------------------------------------
    // findUserByIds()
    // ---------------------------------------------------------
    @Test
    void testFindUserByIds_success_noRetry() {

        // 入力
        var ids = Set.of("u1", "u2");

        // DynamoDB が返すレスポンス（retry なし）
        Map<String, AttributeValue> item1 = Map.of(
                "userid", AttributeValue.fromS("u1"),
                "username", AttributeValue.fromS("Alice")
        );

        Map<String, AttributeValue> item2 = Map.of(
                "userid", AttributeValue.fromS("u2"),
                "username", AttributeValue.fromS("Bob")
        );

        Map<String, List<Map<String, AttributeValue>>> responses =
                Map.of("UsersTable", List.of(item1, item2));

        var mockResponse = software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse
                .builder()
                .responses(responses)
                .unprocessedKeys(Map.of())
                .build();

        when(mockDynamo.batchGetItem(any(BatchGetItemRequest.class)))
        .thenReturn(mockResponse);


        // 実行
        Map<String, String> result = dao.findUserByIds(ids);

        // 検証
        assertEquals(2, result.size());
        assertEquals("Alice", result.get("u1"));
        assertEquals("Bob", result.get("u2"));
    }

    //20260128追記
    @Test
    void testFindUserByIds_withRetry() {

        // 対象ID
        var ids = Set.of("u1", "u2");

        // --- 1回目のレスポンス（u1 だけ返る） ---
        Map<String, AttributeValue> item1 = Map.of(
                "userid", AttributeValue.fromS("u1"),
                "username", AttributeValue.fromS("Alice")
        );

        Map<String, List<Map<String, AttributeValue>>> firstResponse =
                Map.of("UsersTable", List.of(item1));

        // u2 が未処理
        Map<String, KeysAndAttributes> unprocessed =
                Map.of("UsersTable",
                        KeysAndAttributes.builder()
                                .keys(List.of(Map.of("userid", AttributeValue.fromS("u2"))))
                                .build()
                );

        BatchGetItemResponse mockResponse1 = BatchGetItemResponse.builder()
                .responses(firstResponse)
                .unprocessedKeys(unprocessed)
                .build();


        // --- 2回目のレスポンス（u2 が返る） ---
        Map<String, AttributeValue> item2 = Map.of(
                "userid", AttributeValue.fromS("u2"),
                "username", AttributeValue.fromS("Bob")
        );

        Map<String, List<Map<String, AttributeValue>>> secondResponse =
                Map.of("UsersTable", List.of(item2));

        BatchGetItemResponse mockResponse2 = BatchGetItemResponse.builder()
                .responses(secondResponse)
                .unprocessedKeys(Map.of())   // 未処理なし
                .build();


        // --- DynamoDB のモック設定 ---
        when(mockDynamo.batchGetItem(any(BatchGetItemRequest.class)))
                .thenReturn(mockResponse1)   // 1回目
                .thenReturn(mockResponse2);  // retry


        // --- 実行 ---
        Map<String, String> result = dao.findUserByIds(ids);


        // --- 検証 ---
        assertEquals(2, result.size());
        assertEquals("Alice", result.get("u1"));
        assertEquals("Bob", result.get("u2"));

        // retry が 1 回だけ呼ばれたことも確認
        verify(mockDynamo, times(2)).batchGetItem(any(BatchGetItemRequest.class));
    }
    
    @Test
    void testFindUserByIds_emptyInput() {

        Map<String, String> result = dao.findUserByIds(Set.of());

        assertTrue(result.isEmpty());
        verify(mockDynamo, never()).batchGetItem(any(BatchGetItemRequest.class));
    }


    @Test
    void testFindUserByIds_dynamoException() {

        var ids = Set.of("u1");

        when(mockDynamo.batchGetItem(any(BatchGetItemRequest.class)))
        	.thenThrow(DynamoDbException.builder().message("error").build());

        assertThrows(DynamoDbException.class, () -> dao.findUserByIds(ids));
    }
    
    
}