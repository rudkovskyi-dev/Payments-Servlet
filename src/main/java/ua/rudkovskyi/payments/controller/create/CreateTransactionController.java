package ua.rudkovskyi.payments.controller.create;

import ua.rudkovskyi.payments.bean.Balance;
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
import java.sql.SQLException;

@WebServlet(
        name = "createTransactionController",
        urlPatterns = "/WEB-INF/createTransactionController"
)
public class CreateTransactionController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public CreateTransactionController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (PathUtil.isBalanceDNEWithRedirect404(request, response)){
            return;
        }
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        if (isAdmin && !AuthUtil.checkUserAuthority(requestedUserId, request)){
            request.getRequestDispatcher("/404").forward(request, response);
            return;
        }
        try {
            Balance balance = BalanceDAO.findBalanceByIdWithoutOwner(
                    WebAppUtil.getConnection(request),
                    requestedBalanceId);
            request.setAttribute("balance", balance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/WEB-INF/views/createTransaction.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
