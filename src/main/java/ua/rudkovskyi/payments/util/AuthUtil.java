package ua.rudkovskyi.payments.util;

import ua.rudkovskyi.payments.bean.Balance;
import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.BalanceDAO;

import javax.servlet.http.HttpServletRequest;

public class AuthUtil {
    private AuthUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean checkAdminAuthority(HttpServletRequest request) {
        User user = WebAppUtil.getUserFromSession(request.getSession());
        return user.isAdmin();
    }

    public static boolean checkUserAuthority(long id, HttpServletRequest request) {
        User user = WebAppUtil.getUserFromSession(request.getSession());
        return user.getId().equals(id);
    }
}
