package ua.rudkovskyi.payments.controller.info;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.BalanceDAO;
import ua.rudkovskyi.payments.dao.UserDAO;
import ua.rudkovskyi.payments.util.AuthUtil;
import ua.rudkovskyi.payments.util.PathUtil;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
        if (PathUtil.isUserDNEWithRedirect404(request, response)) {
            return;
        }
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        if (!(isAdmin || AuthUtil.checkUserAuthority(requestedUserId, request))) {
            request.getRequestDispatcher("/404").forward(request, response);
            return;
        }
        try {
            List<Balance> balances = BalanceDAO.findBalancesByOwnerId(
                    WebAppUtil.getConnection(request),
                    requestedUserId
            );
            request.setAttribute("balances", balances);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("isAdmin", isAdmin);
        request.getRequestDispatcher("/WEB-INF/views/balances.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!AuthUtil.checkAdminAuthority(request)) {
            doGet(request, response);
            return;
        }
        if (PathUtil.isUserDNEWithRedirect404(request, response) || selectMethod(request, response)) {
            return;
        }
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        Double doubleAmount = Double.valueOf(request.getParameter("doubleAmount"));
        doubleAmount *= 100;
        boolean isLocked = Boolean.parseBoolean(request.getParameter("isLocked"));
        Balance balance = new Balance(
                request.getParameter("name"),
                doubleAmount.longValue(),
                isLocked);
        try {
            BalanceDAO.createBalanceWithUserId(
                    WebAppUtil.getConnection(request),
                    balance,
                    requestedUserId
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/u/" + requestedUserId);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        String username = request.getParameter("username");
        User user = null;
        try {
            user = UserDAO.findUserByUsername(WebAppUtil.getConnection(request), username);
            if (user != null) {
                long dbid = user.getId();
                user = UserDAO.findUserById(
                        WebAppUtil.getConnection(request),
                        requestedUserId);
                if (!user.getId().equals(dbid)){
                    request.setAttribute("message", "Username already taken!");
                    request.getRequestDispatcher("/edit/" + requestedUserId).forward(request, response);
                    return;
                }
            } else {
                user = UserDAO.findUserById(
                        WebAppUtil.getConnection(request),
                        requestedUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (user == null) {
            request.getRequestDispatcher("/404").forward(request, response);
            return;
        } else {
            String[] stringRoles = request.getParameterValues("role");
            boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));
            Set<Role> roles = EnumSet.noneOf(Role.class);
            for (String sR : stringRoles) {
                roles.add(Role.valueOf(sR));
            }
            user.setUsername(username);
            user.setActive(isActive);
            user.setRoles(roles);
            try {
                UserDAO.updateUser(WebAppUtil.getConnection(request), user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        request.setAttribute("isAdmin", isAdmin);
        response.sendRedirect("/users");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is DELETE</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
    }

    public boolean selectMethod(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
