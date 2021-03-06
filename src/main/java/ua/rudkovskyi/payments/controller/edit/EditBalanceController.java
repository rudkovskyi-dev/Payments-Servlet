package ua.rudkovskyi.payments.controller.edit;

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
        if (PathUtil.isBalanceDNEWithRedirect404(request, response)){
            return;
        }
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        request.setAttribute("isAdmin", isAdmin);
        if (!(isAdmin || AuthUtil.checkUserAuthority(requestedUserId, request))) {
            request.getRequestDispatcher("/404").forward(request, response);
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
        doGet(request, response);
    }
}
