package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import entity.AdminLoginUser;
import entity.LoginUser;


@WebFilter("/*")
public class LoginStateFilter extends HttpFilter implements Filter {
       

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String uri = req.getRequestURI();
		String ctx = req.getContextPath();

//		
		// 転送しないページの設定
		if (uri.startsWith(ctx + "/css/")
		    || uri.startsWith(ctx + "/images/")
		    || uri.startsWith(ctx + "/js/")
		    || uri.equals(ctx + "/login")
		    || uri.equals(ctx + "/admin/login")) {
		    chain.doFilter(request, response);
		    return;
		}
		
		// ログイン状態のチェック
		// falseを入れると、セッションがない場合nullで返す。
		// ()のままだと、セッションが新しく作られる
		HttpSession session = req.getSession(false);
		
		// 管理サイトについて
		if (uri.startsWith(ctx + "/admin/")) {
			
			// 管理サイトへのアクセスでログインされていなければ、管理サイトlogin画面に転送
			AdminLoginUser adminLoginUser = (AdminLoginUser)session.getAttribute("adminLoginUser");
			if (adminLoginUser == null) {
				res.sendRedirect(ctx + "/admin/login");
				return;
			}
			
			// 管理サイトでログインされていれば、フィルターをここで終了する
			// （ユーザーサイトのチェックに進まない）
			chain.doFilter(request, response);
			return;
			
		}
		
		// その他（ユーザーサイト）について
		// ログインしていなければ、login画面に転送する

		LoginUser loginUser = (LoginUser)session.getAttribute("loginUser");
		
		if (loginUser == null) {
			res.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		
		// これより以前に書いたコードが適用される
		chain.doFilter(request, response);
	}


}
