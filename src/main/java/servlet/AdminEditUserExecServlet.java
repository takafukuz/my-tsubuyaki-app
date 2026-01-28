package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import common.DbOpeResult;
import entity.UserInfo;
import model.AdminUserLogic;

/**
 * Servlet implementation class AdminEditUserExecServlet
 */
@WebServlet("/admin/edit-user-exec")
public class AdminEditUserExecServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String userId = request.getParameter("userId");
		String userName = request.getParameter("userName");
		// 確認画面からPOSTされるadminPrivはチェックボックスではない(hidden)
		int adminPriv = Integer.parseInt(request.getParameter("adminPriv"));
		
		UserInfo userInfo = new UserInfo(userId, userName, adminPriv);
		
		AdminUserLogic logic = new AdminUserLogic();
		DbOpeResult result = logic.updateUserInfo(userInfo);
		
		if (result != DbOpeResult.SUCCESS) {
			
			// リダイレクトだから、エラーmsgはセッションスコープに入れる。表示後、JSPで消すこと。
			HttpSession session = request.getSession();
			session.setAttribute("errorMsg","ユーザー情報の更新でエラーが発生しました。");
			response.sendRedirect(request.getContextPath() + "/admin/main");
			return;
		}
		
		request.setAttribute("userInfo", userInfo);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/edit-user-complete.jsp");
		dispatcher.forward(request, response);
		
			
	}

}
