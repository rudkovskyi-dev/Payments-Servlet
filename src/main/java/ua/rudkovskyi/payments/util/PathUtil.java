package ua.rudkovskyi.payments.util;

import ua.rudkovskyi.payments.dao.BalanceDAO;
import ua.rudkovskyi.payments.dao.TransactionDAO;
import ua.rudkovskyi.payments.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {
    private static final int MAX_PATH_LENGTH = 4;
    private static final int MIN_PATH_LENGTH = 1;

    private PathUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static long numToLong(String strNum) {
        if (strNum == null) {
            return -1L;
        }
        long num = -1L;
        if (strNum.matches("^\\d{1,19}$"))
            try {
                num = Long.parseLong(strNum);
            }
            catch (NumberFormatException e) {
                return -1L;
            }
        if (num < 0) {
            return -1L;
        } else {
            return num;
        }
    }

    public static List<Long> pathToArray(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        String[] path = pathInfo.split("/");
        List<Long> pathLong = new ArrayList<>();
        if (path.length < MIN_PATH_LENGTH || path.length > MAX_PATH_LENGTH) {
            return null;
        }
        for (int i = MIN_PATH_LENGTH; i < path.length && i < MAX_PATH_LENGTH; i++) {
            long number = numToLong(path[i]);
            if (number != -1L) {
                pathLong.add(numToLong(path[i]));
            } else {
                return null;
            }
        }
        return pathLong;
    }

    public static boolean isUserDNEWithRedirect404(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        boolean isUserPresent = false;
        try {
            isUserPresent = UserDAO.findIfUserExistsById(WebAppUtil.getConnection(request), requestedUserId);
            if (isUserPresent){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/404").forward(request, response);
        return true;
    }

    public static boolean isBalanceDNEWithRedirect404(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        boolean isBalancePresent = false;
        try {
            isBalancePresent = BalanceDAO.findIfBalanceExistsByUserIdAndBalanceId(WebAppUtil.getConnection(request),
                    requestedUserId, requestedBalanceId);
            if (isBalancePresent){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/404").forward(request, response);
        return true;
    }

    public static boolean isTransactionDNEWithRedirect404(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        long requestedBalanceId = Long.parseLong(request.getAttribute("balanceId").toString());
        long requestedTransactionId = Long.parseLong(request.getAttribute("transactionId").toString());
        boolean isTransactionPresent = false;
        try {
            isTransactionPresent = TransactionDAO.findIfTransactionExistsByUserIdAndBalanceIdAndTransactionId(
                    WebAppUtil.getConnection(request), requestedUserId, requestedBalanceId, requestedTransactionId);
            if (isTransactionPresent){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.getRequestDispatcher("/404").forward(request, response);
        return true;
    }
}
