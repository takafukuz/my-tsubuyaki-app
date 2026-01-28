package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.UserInfo;
import model.AdminUserLogic;

/**
 * Servlet implementation class AdminDelUserServlet
 */
@WebServlet("/admin/del-user")
public class AdminDelUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 選択されたユーザーは、同名で入ってくるので、getParameterValuesを使って配列に入れる
		String[] selectedUsers = request.getParameterValues("selectedUsers");
		
		if ( selectedUsers == null ) {
			HttpSession session = request.getSession();
			session.setAttribute("errorMsg", "削除するユーザーが選択されていません");
			response.sendRedirect(request.getContextPath() + "/admin/main");
			return;
		}
		
		// Stringの配列をStringのリストに入れ直す
		List<String> userIds = new ArrayList<>();
		for (String user: selectedUsers) {
			userIds.add(user);
		}
		
		// userIdsをAdminUserLogicに引き渡す
		// 結果はUserInfoのリストでもらう
		
		AdminUserLogic logic = new AdminUserLogic();
		List<UserInfo> userList = logic.findUsersByIds(userIds);
		
		// UserinfoのリストをJSPに渡して、一覧表を表示する
		request.setAttribute("userList", userList);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/del-user-confirm.jsp");
		dispatcher.forward(request, response);
	}

}
