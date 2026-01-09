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
import dao.UsersDAO;
import entity.LoginUser;

/**
 * Servlet implementation class ChangeUserInfo
 */
@WebServlet("/change-user-info")
public class ChangeUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 入力フォームを表示
		RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-user-info.jsp");
		disp.forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// POSTパラメータを取得
		request.setCharacterEncoding("UTF-8");
		int userId = Integer.parseInt(request.getParameter("userId"));
		String newUserName = request.getParameter("newUserName");
		
		// 名前が変更されていなければ、画面を戻して、エラーmsgを表示
		HttpSession session = request.getSession();
		LoginUser loginUser = (LoginUser)session.getAttribute("loginUser");
		
		if (newUserName.equals(loginUser.getUserName())) {
			request.setAttribute("errorMsg", "情報が変更されていません");
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-user-info.jsp");
			disp.forward(request, response);
			return;
		}
		
		UsersDAO dao = new UsersDAO();
		DbOpeResult result = dao.updateUserName(userId,newUserName);
		
		if (result == DbOpeResult.SUCCESS) {
			// LoginUserインスタンスの情報を更新して、セッションスコープに入れ直す
			loginUser.setUserName(newUserName);
			session.setAttribute("loginUser", loginUser);
			// 完了画面へ
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-user-info-complete.jsp");
			disp.forward(request, response);
		} else if (result == DbOpeResult.DUPLICATE){
			request.setAttribute("errorMsg", "すでに使われているユーザー名には変更できません");
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-user-info.jsp");
			disp.forward(request, response);
		} else {
			request.setAttribute("errorMsg", "ユーザー情報の変更に失敗しました");
			RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/change-user-info.jsp");
			disp.forward(request, response);
		}
			
		
	}

}
