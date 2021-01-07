package ua.rudkovskyi.payments.filter;

import ua.rudkovskyi.payments.util.ConnectionUtil;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

@WebFilter(
        filterName = "JDBCFilter",
        urlPatterns = "/*"
)
public class JDBCFilter implements Filter {
    public JDBCFilter(){
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    private boolean JDBCRequired(HttpServletRequest request) {
        String urlPattern = request.getServletPath();
        String pathInfo = request.getPathInfo();

        if (pathInfo != null) {
            urlPattern += "/*";
        }

        Map<String, ? extends ServletRegistration> servletRegistrations =
                request.getServletContext().getServletRegistrations();

        Collection<? extends ServletRegistration> values = servletRegistrations.values();
        for (ServletRegistration sr : values) {
            Collection<String> mappings = sr.getMappings();
            if (mappings.contains(urlPattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (this.JDBCRequired(req)) {
            System.out.println("Open Connection for: " + req.getServletPath());
            Connection conn = null;
            try {
                conn = ConnectionUtil.getConnection();
                conn.setAutoCommit(false);
                WebAppUtil.setConnection(request, conn);
                chain.doFilter(request, response);
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
                ConnectionUtil.rollbackQuietly(conn);
                throw new ServletException();
            } finally {
                ConnectionUtil.closeQuietly(conn);
                System.out.println("Close Connection for: " + req.getServletPath());
            }
        }
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
