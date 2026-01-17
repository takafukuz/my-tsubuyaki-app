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

import entity.LoginUser;


@WebFilter("/*")
public class LoginStateFilter extends HttpFilter implements Filter {
       

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String uri = req.getRequestURI();
//		System.out.println(uri.toString());
//		
		// 除外するURIを指定
		if (uri.startsWith(req.getContextPath() + "/css/")
		    || uri.startsWith(req.getContextPath() + "/images/")
		    || uri.startsWith(req.getContextPath() + "/js/")
		    || uri.equals(req.getContextPath() + "/login")
		    || uri.startsWith(req.getContextPath() + "/admin/")) {
		    chain.doFilter(request, response);
		    return;
		}
//		
//        // ログインしていなければ、login画面に転送する
		HttpSession session = req.getSession();
		LoginUser loginUser = (LoginUser)session.getAttribute("loginUser");
		
		if (loginUser == null) {
			res.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		
		// これより以前に書いたコードが適用される
		chain.doFilter(request, response);
	}


}
