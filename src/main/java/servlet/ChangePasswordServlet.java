package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import common.DbOpeResult;
import model.UpdateUserInfoLogic;

/**
 * Servlet implementation class ChangePassword
 */
@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// フォームを表示
		RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-password.jsp");
		disp.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// フォームから値を取得
		request.setCharacterEncoding("UTF-8");
		int userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		
		// 確認パスワードが食い違う場合
		if (!password.equals(confirmPassword)) {
			request.setAttribute("errorMsg","入力されたパスワードが一致しません");
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-password.jsp");
			disp.forward(request, response);
			return;
		}
		
		UpdateUserInfoLogic logic = new UpdateUserInfoLogic();
		DbOpeResult result = logic.changePassword(userId, password);
		
		System.out.println(result);
		
		if (result == DbOpeResult.SUCCESS) {
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-password-complete.jsp");
			disp.forward(request, response);
					
		} else {
			request.setAttribute("errorMsg","パスワード変更に失敗しました");
			RequestDispatcher disp = request.getRequestDispatcher(request.getContextPath()+"/change-password");
			disp.forward(request, response);
		}
		
	}

}
