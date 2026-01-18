package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entity.NewUserForm;

/**
 * Servlet implementation class AdminAddUserServlet
 */
@WebServlet("/admin/add-user")
public class AdminAddUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/add-user.jsp");
		dispatcher.forward(request, response);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String userName = request.getParameter("userName");
		// チェックボックスにチェックが入っていない（null）であれば、0、そうでなければ1
		int adminPriv = request.getParameter("adminPriv") == null ? 0 : 1;
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		
		// 2つのパスワードが違っていたら、入力画面に戻して、エラーmsgを表示
		if (!password.equals(confirmPassword)) {
			request.setAttribute("errorMsg", "パスワードを正しく入力してください");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/add-user.jsp");
			dispatcher.forward(request, response);
			return;
		}
		
		// パスワードが一致していれば、newUserInfoをリクエストスコープに入れて、確認画面にフォワード
		NewUserForm newUserForm = new NewUserForm(userName, adminPriv, password);
		
		request.setAttribute("newUserForm", newUserForm);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/add-user-confirm.jsp");
		dispatcher.forward(request, response);
		
	}

}
