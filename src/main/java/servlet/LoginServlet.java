package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.LoginUser;
import model.LoginLogic;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// テスト用にloginLogicが差し替えられるようにする
	private LoginLogic loginLogic = new LoginLogic();
	
	void setLoginLogic(LoginLogic loginLogic) {
	    this.loginLogic = loginLogic;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
		disp.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		
		// LoginLogic loginLogic = new LoginLogic();
		String userId = loginLogic.canLogin(userName, password);
		
		if (userId == null) {
			// 認証失敗。ログイン画面に戻す
			request.setAttribute("errorMsg", "ログイン認証に失敗しました");
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
			disp.forward(request, response);
			return;
		}
		
		LoginUser loginUser = new LoginUser(userId,userName);
		
		HttpSession session = request.getSession();
		session.setAttribute("loginUser", loginUser);
		
		response.sendRedirect(request.getContextPath()+"/main");
		
	}

}
