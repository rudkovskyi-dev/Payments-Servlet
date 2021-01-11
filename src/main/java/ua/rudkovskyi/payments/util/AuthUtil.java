package ua.rudkovskyi.payments.util;

import ua.rudkovskyi.payments.bean.User;

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
