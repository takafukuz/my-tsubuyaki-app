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
import entity.NewUserForm;
import model.AdminUserLogic;

@WebServlet("/admin/add-user-exec")
public class AdminAddUserExecServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String userName = request.getParameter("userName");
		int adminPriv = Integer.parseInt(request.getParameter("adminPriv"));
		String password = request.getParameter("password");
		
		NewUserForm newUserForm = new NewUserForm(userName, adminPriv, password);
		
		AdminUserLogic logic = new AdminUserLogic();
		
		DbOpeResult result = logic.addUser(newUserForm);
		
		if (result == DbOpeResult.SUCCESS) {
			// 完了画面にフォワード
			request.setAttribute("newUserForm", newUserForm);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/add-user-complete.jsp");
			dispatcher.forward(request, response);
			
		} else if (result == DbOpeResult.DUPLICATE) {
			// セッションスコープにエラーmsgを入れて、/admin/add-userにリダイレクト
			HttpSession session = request.getSession();
			session.setAttribute("errorMsg", "すでに存在するユーザー名です。");
			response.sendRedirect(request.getContextPath()+"/admin/add-user");
			return;
			
		} else {
			HttpSession session = request.getSession();
			session.setAttribute("errorMsg", "ユーザーの新規追加時にエラーが発生しました。");
			response.sendRedirect(request.getContextPath()+"/admin/add-user");
			return;
		}
		
	}

}
