package ua.rudkovskyi.payments.controller.info;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Transaction;
import ua.rudkovskyi.payments.dao.BalanceDAO;
import ua.rudkovskyi.payments.dao.TransactionDAO;
import ua.rudkovskyi.payments.util.AuthUtil;
import ua.rudkovskyi.payments.util.PathUtil;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(
        name = "balancePathController",
        urlPatterns = "/WEB-INF/balancePathController"
)
public class BalancePathController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public BalancePathController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (PathUtil.isBalanceDNEWithRedirect404(request, response)) {
            return;
        }
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        if (!(isAdmin || AuthUtil.checkUserAuthority(requestedUserId, request))) {
            request.getRequestDispatcher("/404").forward(request, response);
            return;
        }
        try {
            List<Transaction> transactions = TransactionDAO.findTransactionsByBalanceId(
                    WebAppUtil.getConnection(request),
                    requestedBalanceId
            );
            request.setAttribute("transactions", transactions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("isAdmin", isAdmin);
        request.getRequestDispatcher("/WEB-INF/views/transactions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (PathUtil.isBalanceDNEWithRedirect404(request, response) || selectMethod(request, response)) {
            return;
        }
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        long destinationId = Long.parseLong(request.getParameter("destinationId"));
        Double doubleAmount = Double.parseDouble(request.getParameter("doubleAmount"));
        doubleAmount *= 100;
        long amount = doubleAmount.longValue();

        Transaction transaction = new Transaction(amount);
        try {
            if (amount <= 0 ||
                    !BalanceDAO.findIfBalanceExistsByBalanceId(WebAppUtil.getConnection(request), destinationId)){
                response.sendRedirect("/create/" + requestedUserId + "/" + requestedBalanceId);
                return;
            }
            TransactionDAO.createTransactionByBalanceSourceIdAndDestinationBalanceId(
                    WebAppUtil.getConnection(request),
                    transaction,
                    requestedBalanceId,
                    destinationId
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/u/" + requestedUserId + "/" + requestedBalanceId);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!AuthUtil.checkAdminAuthority(request)) {
            doGet(request, response);
            return;
        }
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!AuthUtil.checkAdminAuthority(request)) {
            doGet(request, response);
            return;
        }
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
