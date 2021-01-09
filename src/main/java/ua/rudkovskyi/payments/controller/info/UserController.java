package ua.rudkovskyi.payments.controller.info;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.dao.BalanceDAO;
import ua.rudkovskyi.payments.util.AuthUtil;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(
        name = "userController",
        urlPatterns = "/WEB-INF/userController"
)
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public UserController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        if (!(isAdmin || AuthUtil.checkUserAuthority(requestedUserId, request))){
            response.sendRedirect("/u/" + WebAppUtil.getUserFromSession(request.getSession()).getId());
        }
        try {
            List<Balance> balances = BalanceDAO.findBalancesByOwnerId(
                    WebAppUtil.getConnection(request),
                    requestedUserId
            );
            request.setAttribute("balances", balances);
            request.setAttribute("isAdmin", isAdmin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/WEB-INF/views/balances.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!AuthUtil.checkAdminAuthority(request)) {
            doGet(request, response);
            return;
        }
        if (selectMethod(request, response)) {
            return;
        }
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is POST</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is PUT</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is DELETE</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
    }

    public boolean selectMethod(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getParameter("_method");
        if (method != null) {
            if (method.equals("PUT")) {
                doPut(request, response);
                return true;
            }
            if (method.equals("DELETE")) {
                doDelete(request, response);
                return true;
            }
        }
        return false;
    }
}
