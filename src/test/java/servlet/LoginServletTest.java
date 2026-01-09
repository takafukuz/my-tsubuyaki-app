package servlet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entity.LoginUser;
import model.LoginLogic;

public class LoginServletTest {

    private LoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        servlet = new LoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
    }

    // -------------------------
    // 認証成功パターン
    // -------------------------
    @Test
    void testDoPost_LoginSuccess() throws Exception {

        // LoginLogic をモック化して注入
        LoginLogic loginLogicMock = mock(LoginLogic.class);
        servlet.setLoginLogic(loginLogicMock);

        // 入力値
        when(request.getParameter("userName")).thenReturn("taro");
        when(request.getParameter("password")).thenReturn("1234");

        // ★ あなたのアプリのコンテキストパスに合わせる
        when(request.getContextPath()).thenReturn("/my-tsubuyaki-app");

        // ログイン成功を想定
        when(loginLogicMock.canLogin("taro", "1234")).thenReturn(1);

        // 実行
        servlet.doPost(request, response);

        // セッションに loginUser がセットされる
        verify(session).setAttribute(eq("loginUser"), any(LoginUser.class));

        // 正しい URL にリダイレクトされる
        verify(response).sendRedirect("/my-tsubuyaki-app/main");
    }

    // -------------------------
    // 認証失敗パターン
    // -------------------------
    @Test
    void testDoPost_LoginFail() throws Exception {

        LoginLogic loginLogicMock = mock(LoginLogic.class);
        servlet.setLoginLogic(loginLogicMock);

        when(request.getParameter("userName")).thenReturn("taro");
        when(request.getParameter("password")).thenReturn("wrong");

        // ログイン失敗
        when(loginLogicMock.canLogin("taro", "wrong")).thenReturn(null);

        // JSP へのフォワード設定
        when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp"))
                .thenReturn(dispatcher);

        servlet.doPost(request, response);

        // エラーメッセージがセットされる
        verify(request).setAttribute(eq("errorMsg"), anyString());

        // JSP にフォワードされる
        verify(dispatcher).forward(request, response);
    }
}