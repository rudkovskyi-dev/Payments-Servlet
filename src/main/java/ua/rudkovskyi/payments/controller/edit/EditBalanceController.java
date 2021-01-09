package ua.rudkovskyi.payments.controller.edit;

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

@WebServlet(
        name = "editBalanceController",
        urlPatterns = "/WEB-INF/editBalanceController"
)
public class EditBalanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public EditBalanceController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        request.setAttribute("isAdmin", isAdmin);
        if (!(isAdmin || AuthUtil.checkUserAuthority(requestedUserId, request))) {
            response.sendRedirect("/u/" + WebAppUtil.getUserFromSession(request.getSession()).getId());
        }
        Balance balance = null;
        try {
            balance = BalanceDAO.findBalanceByIdAndOwnerId(
                    WebAppUtil.getConnection(request),
                    requestedBalanceId,
                    requestedUserId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (balance == null) {
            request.getRequestDispatcher("/404").forward(request, response);
        } else {
            if (!isAdmin) {
                if (balance.getOwner().getId().equals(requestedUserId)) {
                    if (!balance.isLocked()) {
                        balance.setLocked(true);
                        try {
                            BalanceDAO.updateBalance(
                                    WebAppUtil.getConnection(request),
                                    balance);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!balance.isRequested()) {
                            balance.setRequested(true);
                            try {
                                BalanceDAO.updateBalance(
                                        WebAppUtil.getConnection(request),
                                        balance);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                response.sendRedirect("/u/" + WebAppUtil.getUserFromSession(request.getSession()).getId());
                return;
            }
            request.setAttribute("isAdmin", isAdmin);
            request.setAttribute("balance", balance);
        }
        request.getRequestDispatcher("/WEB-INF/views/editBalance.jsp").forward(request, response);
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        if (!isAdmin) {
            response.sendRedirect("/u/" + WebAppUtil.getUserFromSession(request.getSession()).getId());
        }
        Balance balance = null;
        try {
            balance = BalanceDAO.findBalanceByIdWithoutOwner(
                    WebAppUtil.getConnection(request),
                    requestedBalanceId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (balance == null) {
            request.getRequestDispatcher("/404").forward(request, response);
        } else {
            String balanceName = request.getParameter("name");
            Double doubleAmount = Double.valueOf(request.getParameter("doubleAmount"));
            boolean isLocked = Boolean.parseBoolean(request.getParameter("isLocked"));
            doubleAmount *= 100;
            long amount = doubleAmount.longValue();
            if (!isLocked) {
                balance.setRequested(false);
            }
            balance.setName(balanceName);
            balance.setAmount(amount);
            balance.setLocked(isLocked);
            try {
                BalanceDAO.updateBalance(WebAppUtil.getConnection(request), balance);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        request.setAttribute("isAdmin", isAdmin);
        response.sendRedirect("/u/" + requestedUserId);
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
