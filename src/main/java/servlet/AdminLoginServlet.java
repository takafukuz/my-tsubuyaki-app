package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.AdminLoginUser;
import model.AdminLoginLogic;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/login.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		// AdminLoginLogicにログイン判定を投げる
		AdminLoginLogic loginLogic = new AdminLoginLogic();
		Integer userId = loginLogic.canLogin(username, password);
		
		// ログイン失敗の場合、エラーmsgをセットして、login.jspにフォワード
		if ( userId == null ) {
			request.setAttribute("errorMsg", "ログイン認証に失敗しました");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/login.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		// ログイン成功の場合、セッションスコープにユーザー情報を入れて、AdminMainServletにリダイレクト
		AdminLoginUser adminLoginUser = new AdminLoginUser(userId, username);
		HttpSession session = request.getSession();
		session.setAttribute("adminLoginUser", adminLoginUser);

		response.sendRedirect(request.getContextPath() + "/admin/main");

	}

}
