package ua.rudkovskyi.payments.controller.info;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.BalanceDAO;
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

@WebServlet(
        name = "balanceController",
        urlPatterns = "/WEB-INF/balanceController"
)
public class BalanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public BalanceController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (PathUtil.isBalanceDNEWithRedirect404(request, response)){
            return;
        }
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is GET</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
        pw.println("<p>Balance " + request.getAttribute("balanceId") + "</p>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!AuthUtil.checkAdminAuthority(request)) {
            doGet(request, response);
            return;
        }
        if (PathUtil.isBalanceDNEWithRedirect404(request, response) ||
                selectMethod(request, response)) {
            return;
        }

        PrintWriter pw = response.getWriter();
        pw.println("<p>This is POST</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
        pw.println("<p>Balance " + request.getAttribute("balanceId") + "</p>");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
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
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        try {
            BalanceDAO.deleteBalanceById(
                    WebAppUtil.getConnection(request),
                    requestedBalanceId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/u/" + requestedUserId);
    }

    public boolean selectMethod(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String method = request.getParameter("_method");
        if (method != null){
            if (method.equals("PUT")){
                doPut(request, response);
                return true;
            }
            if (method.equals("DELETE")){
                doDelete(request, response);
                return true;
            }
        }
        return false;
    }
}
