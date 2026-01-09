package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import common.DbOpeResult;
import entity.Mutter;
import model.MutterLogic;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/main")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// メイン画面表示
		// つぶやきリストを取得して、リクエストスコープへいれる
		MutterLogic mutterLogic = new MutterLogic();
		List<Mutter> mutterList = mutterLogic.getAllMutters();
		
		request.setAttribute("mutterList", mutterList);
		
		RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/main.jsp");
		disp.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// POSTパラメータを取得
		request.setCharacterEncoding("UTF-8");
		int userId = Integer.parseInt(request.getParameter("userId"));
		String text = request.getParameter("text");
		
		MutterLogic mutterLogic = new MutterLogic();
		// TEXTの値がnullか空であれば、エラーmsgをリクエストスコープに入れる
		if (text == null || text.isEmpty()) {
			request.setAttribute("errorMsg", "つぶやきを入力してください");
		//そうでなければ以下を実行
		} else { 
			// つぶやきをDBに書き込む
			DbOpeResult result = mutterLogic.addMutter(userId, text);
			// 書き込みエラー時にはエラーmsgをリクエストスコープに入れる
			if (result != DbOpeResult.SUCCESS) {
				request.setAttribute("errorMsg", "つぶやきを投稿できませんでした");
			}
		}
		// つぶやき一覧を取得して、リクエストスコープに入れる
		List<Mutter> mutterList = mutterLogic.getAllMutters();
		
		request.setAttribute("mutterList", mutterList);
		
		RequestDispatcher disp = request.getRequestDispatcher("/WEB-INF/jsp/main.jsp");
		disp.forward(request, response);
	}

}
