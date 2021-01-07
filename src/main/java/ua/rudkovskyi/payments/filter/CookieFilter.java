package ua.rudkovskyi.payments.filter;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.UserDAO;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
@WebFilter(
        filterName = "cookieFilter",
        urlPatterns = {"/u/*", "/users", "/main"}
)
public class CookieFilter implements Filter {
    public CookieFilter(){
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        User sessionUser = WebAppUtil.getUserFromSession(session);

        if (sessionUser != null) {
            session.setAttribute("COOKIE_CHECKED", "CHECKED");
            chain.doFilter(request, response);
            return;
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect("/login");
        }
        /*
        Connection conn = WebAppUtil.getConnection(request);

        String checked = (String) session.getAttribute("COOKIE_CHECKED");
        if (checked == null && conn != null) {
            String username = WebAppUtil.getUserCookie(req);
            try {
                User user = UserDAO.findUserByUsername(conn, username);
                WebAppUtil.setUserInSession(session, user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            session.setAttribute("COOKIE_CHECKED", "CHECKED");
        }
        chain.doFilter(request, response);
         */
    }

    @Override
    public void destroy() {
    }
}
