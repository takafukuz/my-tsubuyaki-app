package dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import common.DbOpeResult;
import entity.Mutter;
import infrastructure.AppConfig;
import infrastructure.DynamoDbClientHolder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

class MuttersDAOTest {

    @Mock
    DynamoDbClient mockDynamo;

    @Mock
    AppConfig mockConfig;

    private MuttersDAO dao;

    private MockedStatic<DynamoDbClientHolder> dynamoHolderMock;
    private MockedStatic<AppConfig> appConfigMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // static メソッドのモック
        dynamoHolderMock = mockStatic(DynamoDbClientHolder.class);
        appConfigMock = mockStatic(AppConfig.class);

        dynamoHolderMock.when(DynamoDbClientHolder::getInstance).thenReturn(mockDynamo);
        appConfigMock.when(AppConfig::getInstance).thenReturn(mockConfig);

        when(mockConfig.getUsersTable()).thenReturn("UsersTable");
        when(mockConfig.getMuttersTable()).thenReturn("MuttersTable");

        dao = new MuttersDAO();
    }

    @AfterEach
    void tearDown() {
        dynamoHolderMock.close();
        appConfigMock.close();
    }

    // ---------------------------------------------------------
    // selectAllMutters()
    // ---------------------------------------------------------
    @Test
    void testSelectAllMutters() {

        Map<String, AttributeValue> item = Map.of(
                "mutter_id", AttributeValue.fromS("m1"),
                "userid", AttributeValue.fromS("u1"),
                "username", AttributeValue.fromS("Alice"),
                "mutter", AttributeValue.fromS("Hello"),
                "created_at", AttributeValue.fromS("2024-01-01T10:00:00Z")
        );

        QueryResponse response = QueryResponse.builder()
                .items(item)
                .build();

        when(mockDynamo.query(any(QueryRequest.class))).thenReturn(response);

        List<Mutter> result = dao.selectAllMutters();

        assertEquals(1, result.size());
        Mutter m = result.get(0);

        assertEquals("m1", m.getMutterId());
        assertEquals("u1", m.getUserId());
        assertEquals("Alice", m.getUserName());
        assertEquals("Hello", m.getMutter());
        assertEquals(Timestamp.from(Instant.parse("2024-01-01T10:00:00Z")), m.getCreatedAt());
    }

    // ---------------------------------------------------------
    // addMutter() SUCCESS
    // ---------------------------------------------------------
    @Test
    void testAddMutterSuccess() {

        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("Alice")
        );

        GetItemResponse getItemResponse = GetItemResponse.builder()
                .item(userItem)
                .build();

        when(mockDynamo.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);

        DbOpeResult result = dao.addMutter("u1", "Hello");

        assertEquals(DbOpeResult.SUCCESS, result);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(mockDynamo).putItem(captor.capture());

        PutItemRequest req = captor.getValue();
        assertEquals("MuttersTable", req.tableName());
        assertEquals("u1", req.item().get("userid").s());
        assertEquals("Alice", req.item().get("username").s());
        assertEquals("Hello", req.item().get("mutter").s());
        assertEquals("MUTTER", req.item().get("entity_type").s());
    }

    // ---------------------------------------------------------
    // addMutter() ERROR
    // ---------------------------------------------------------
    @Test
    void testAddMutterError() {

        Map<String, AttributeValue> userItem = Map.of(
                "username", AttributeValue.fromS("Alice")
        );

        when(mockDynamo.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(userItem).build());

        doThrow(DynamoDbException.builder().build())
                .when(mockDynamo).putItem(any(PutItemRequest.class));

        DbOpeResult result = dao.addMutter("u1", "Hello");

        assertEquals(DbOpeResult.ERROR, result);
    }

    // ---------------------------------------------------------
    // delMutter() SUCCESS
    // ---------------------------------------------------------
    @Test
    void testDelMutterSuccess() {

        DbOpeResult result = dao.delMutter("u1", "m1");

        assertEquals(DbOpeResult.SUCCESS, result);
        verify(mockDynamo).deleteItem(any(DeleteItemRequest.class));
    }

    // ---------------------------------------------------------
    // delMutter() NOT_FOUND
    // ---------------------------------------------------------
    @Test
    void testDelMutterNotFound() {

        doThrow(ConditionalCheckFailedException.builder().build())
                .when(mockDynamo).deleteItem(any(DeleteItemRequest.class));

        DbOpeResult result = dao.delMutter("u1", "m1");

        assertEquals(DbOpeResult.NOT_FOUND, result);
    }

    // ---------------------------------------------------------
    // delMutter() ERROR
    // ---------------------------------------------------------
    @Test
    void testDelMutterError() {

        doThrow(DynamoDbException.builder().build())
                .when(mockDynamo).deleteItem(any(DeleteItemRequest.class));

        DbOpeResult result = dao.delMutter("u1", "m1");

        assertEquals(DbOpeResult.ERROR, result);
    }
}