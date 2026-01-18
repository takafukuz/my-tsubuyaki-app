package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/admin/logout")
public class AdminLogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		session.invalidate();
		System.out.println("Session invalidated:" + session.getId());
		
		// フラッシュメッセージ用にセッションを作る
		HttpSession newSession = request.getSession();
		newSession.setAttribute("flashMsg","ログアウトしました");
		response.sendRedirect(request.getContextPath()+"/admin/login");
		
	}

}
