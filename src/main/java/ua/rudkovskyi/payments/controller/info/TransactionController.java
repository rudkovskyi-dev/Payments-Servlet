package ua.rudkovskyi.payments.controller.info;

import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.Transaction;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.TransactionDAO;
import ua.rudkovskyi.payments.util.AuthUtil;
import ua.rudkovskyi.payments.util.DAOUtil;
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
import java.util.List;

@WebServlet(
        name = "transactionController",
        urlPatterns = "/WEB-INF/transactionController"
)
public class TransactionController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public TransactionController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (PathUtil.isTransactionDNEWithRedirect404(request, response)) {
            return;
        }
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        if (!isAdmin) {
            request.getRequestDispatcher("/404").forward(request, response);
            return;
        }
        long requestedTransactionId = Long.parseLong(request.getAttribute("transactionId").toString());
        try {
            Transaction transaction = TransactionDAO.findTransactionById(
                    WebAppUtil.getConnection(request),
                    requestedTransactionId
            );
            request.setAttribute("transaction", transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("isAdmin", isAdmin);
        request.getRequestDispatcher("/WEB-INF/views/editTransaction.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (PathUtil.isTransactionDNEWithRedirect404(request, response) || selectMethod(request, response)) {
            return;
        }
        doGet(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        long requestedTransactionId = Long.parseLong(request.getAttribute("transactionId").toString());
        Transaction transaction = null;
        boolean error = false;
        String errorMessage = "";
        try {
            transaction = TransactionDAO.findTransactionById(
                    WebAppUtil.getConnection(request),
                    requestedTransactionId);
        } catch (SQLException throwables) {
            error = true;
            errorMessage = "DB Error!";
            throwables.printStackTrace();
        }
        if (transaction != null) {
            long sourceAmount = transaction.getSource().getAmount();
            long transactionAmount = transaction.getAmount();
            if (sourceAmount < transactionAmount) {
                error = true;
                errorMessage = "Not enough funds!";
            }
            if (transaction.getSource().isLocked()) {
                error = true;
                errorMessage = "Source balance is locked!";
            }
            if (transaction.isSent()){
                error = true;
                errorMessage = "Transaction is already approved!";
            }
        }
        if (error) {
            request.setAttribute("message", errorMessage);
            doGet(request, response);
            return;
        }
        try {
            if (!TransactionDAO.completeTransaction(
                WebAppUtil.getConnection(request), transaction)){
                errorMessage = "Transaction Error!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage = "Transaction Exception!";
        }
        request.setAttribute("message", errorMessage);
        doGet(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        long requestedTransactionId = Long.parseLong(request.getAttribute("transactionId").toString());
        try {
            TransactionDAO.deleteTransactionById(
                    WebAppUtil.getConnection(request),
                    requestedTransactionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/u/" + requestedUserId + "/" + requestedBalanceId);
    }

    public boolean selectMethod(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String method = request.getParameter("_method");
        if (method != null) {
            boolean ifAuthorized = AuthUtil.checkAdminAuthority(request);
            if (ifAuthorized && method.equals("PUT")) {
                doPut(request, response);
                return true;
            }
            if (ifAuthorized && method.equals("DELETE")) {
                doDelete(request, response);
                return true;
            }
        }
        return false;
    }
}
