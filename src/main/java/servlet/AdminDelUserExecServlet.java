package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.AdminUserLogic;

/**
 * Servlet implementation class AdminDelUserExec
 */
@WebServlet("/admin/del-user-exec")
public class AdminDelUserExecServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		
		// セッションスコープの取得
		HttpSession session = request.getSession();
		
		// 選択ユーザーは同名で入ってくるので、配列に入れる
		String[] selectedUsers = request.getParameterValues("selectedUsers");

		if ( selectedUsers == null ) {
			session.setAttribute("errorMsg", "削除するユーザーが選択されていません");
			response.sendRedirect(request.getContextPath() + "/admin/main");
			return;
		}
		
		// 選択ユーザーをリストに入れ替え
		List<Integer> userIds = new ArrayList<>();
		for (String user: selectedUsers) {
			userIds.add(Integer.parseInt(user));
		}
		
		// DBからユーザーの削除の実施
		AdminUserLogic logic = new AdminUserLogic();
		
		int result = logic.delUser(userIds);
		
		// 結果に応じて、セッションスコープにメッセージを入れて、main画面にリダイレクト
		if (result > 0 ) {
			session.setAttribute("flashMsg", result + "件のユーザーを削除しました。");
		} else {
			session.setAttribute("errorMsg","ユーザー情報の削除時にエラーが発生しました。");
		}

		response.sendRedirect(request.getContextPath()+"/admin/main");
	}

}
