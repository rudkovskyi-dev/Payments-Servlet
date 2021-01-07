package ua.rudkovskyi.payments.util;

import ua.rudkovskyi.payments.bean.User;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;

public class WebAppUtil {
    public static final String ATT_CONNECTION = "ATTRIBUTE_CONNECTION";
    private static final String ATT_USER = "ATTRIBUTE_USER_COOKIE";

    public static void setConnection(ServletRequest request, Connection conn) {
        request.setAttribute(ATT_CONNECTION, conn);
    }

    public static Connection getConnection(ServletRequest request) {
        return (Connection) request.getAttribute(ATT_CONNECTION);
    }

    public static void setUserInSession(HttpSession session, User loggedInUser) {
        session.setAttribute("loggedInUser", loggedInUser);
    }

    public static User getUserFromSession(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    public static void setUserCookie(HttpServletResponse response, User user) {
        Cookie cookieUser = new Cookie(ATT_USER, user.getUsername());
        cookieUser.setMaxAge(24 * 60 * 60);
        response.addCookie(cookieUser);
    }

    public static String getUserCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ATT_USER.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteUserCookie(HttpServletResponse response) {
        Cookie cookieUser = new Cookie(ATT_USER, null);
        cookieUser.setMaxAge(0);
        response.addCookie(cookieUser);
    }
}
