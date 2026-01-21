package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import common.DbOpeResult;
import entity.LoginUser;
import model.MutterLogic;

/**
 * Servlet implementation class DelMutter
 */
@WebServlet("/del-mutter")
public class DelMutterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String mutterIdStr = request.getParameter("targetMutterId");
		String userIdStr = request.getParameter("targetUserId");
		int mutterId;
		int userId;
		
		HttpSession session = request.getSession();
		
		// 値がnullの場合、エラーmsgを設定して、リダイレクト
		if (mutterIdStr== null || userIdStr == null) {
			session.setAttribute("errorMsg", "mutterIdまたはuserIdがNULLです。");
			response.sendRedirect(request.getContextPath() + "/main");
			return;
		}
		
		// 値が数値でない場合、エラーmsgを設定して、リダイレクト
		try {
			mutterId = Integer.parseInt(mutterIdStr);
			userId = Integer.parseInt(userIdStr);
		} catch (NumberFormatException e) {
			session.setAttribute("errorMsg", "mutterIdまたはuserIdが数値ではありません。");
			response.sendRedirect(request.getContextPath() + "/main");
			return;
		}
		
		// userIdがログインしているユーザー出ない場合、エラーmsgを設定して、リダイレクト

		LoginUser loginUser = (LoginUser)session.getAttribute("loginUser");
		
		if ( loginUser.getUserId() != userId ) {
			session.setAttribute("errorMsg", "他のユーザーのつぶやきを削除することはできません。");
			response.sendRedirect(request.getContextPath() + "/main");
			return;
		}
		
		// つぶやきの削除を実行
		MutterLogic logic = new MutterLogic();
		DbOpeResult result = logic.delMutter(userId, mutterId);
		
		// 削除エラーの場合、エラーmsgを設定して、リダイレクト
		if (result == DbOpeResult.ERROR) {
			session.setAttribute("errorMsg", "つぶやきの削除でエラーが発生しました。");
			response.sendRedirect(request.getContextPath() + "/main");
			return;
		}
		
		// 成功時も　リダイレクト
		response.sendRedirect(request.getContextPath() + "/main");
		
	}

}
