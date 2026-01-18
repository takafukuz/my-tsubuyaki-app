package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entity.UserInfo;

/**
 * Servlet implementation class AdminEditUserConfirmServlet
 */
@WebServlet("/admin/edit-user-confirm")
public class AdminEditUserConfirmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		// POSTパラメータの取得
		int userId = Integer.parseInt(request.getParameter("userId"));
		String userName = request.getParameter("userName");
		
		// adminPrivはチェックボックスなので、チェックされなければ値がこない
		// 値がない場合は、0、ある場合は1とする（3項演算子）
		int adminPriv = request.getParameter("adminPriv") == null ? 0 : 1;

		// ユーザー情報をリクエストスコープに入れて、表示
		UserInfo userInfo = new UserInfo(userId, userName, adminPriv);
		request.setAttribute("userInfo", userInfo);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/edit-user-confirm.jsp");
		dispatcher.forward(request, response);
		

	}

}
