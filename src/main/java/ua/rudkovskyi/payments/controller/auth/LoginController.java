package ua.rudkovskyi.payments.controller.auth;

import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.UserDAO;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(
        name = "LoginController",
        urlPatterns = "/login"
)
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (WebAppUtil.getUserFromSession(request.getSession())==null) {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/logout").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = null;
        boolean hasError = false;
        String errorString = null;

        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            hasError = true;
            errorString = "Required username and password!";
        } else {
            Connection conn = WebAppUtil.getConnection(request);
            try {
                user = UserDAO.findUserByUsernameAndPassword(conn, username, password);

                if (user == null) {
                    hasError = true;
                    errorString = "User Name or password invalid";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                hasError = true;
                errorString = e.getMessage();
            }
        }
        if (hasError) {
            request.setAttribute("errorString", errorString);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
        else {
            HttpSession session = request.getSession();
            WebAppUtil.setUserInSession(session, user);
            WebAppUtil.setUserCookie(response, user);
            response.sendRedirect(request.getContextPath() + "/u/" + user.getId());
        }
    }
}
