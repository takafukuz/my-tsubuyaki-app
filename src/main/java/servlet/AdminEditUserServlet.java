package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entity.UserInfo;
import model.AdminUserLogic;

/**
 * Servlet implementation class AdminEditUserServlet
 */
@WebServlet("/admin/edit-user")
public class AdminEditUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// GETパラメータの取得
		String userId = request.getParameter("userid");
		
		// パラメータがなければ、メイン画面（ユーザー一覧）に戻す
		if (userId == null || userId.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/admin/main");
			return;
		}
		
		// パラメータをuserIdに代入
		// 数値でない値が来たときの処理を考慮する必要があるかどうか
//		int userId = Integer.parseInt(userIdStr);
		
		// パラメータをもとに当該userIdのユーザー情報を取得して、表示する
		AdminUserLogic logic = new AdminUserLogic();
		UserInfo userInfo = logic.getUserInfo(userId);
		
		request.setAttribute("userInfo", userInfo);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/edit-user.jsp");
		dispatcher.forward(request, response);
		
	}

}
