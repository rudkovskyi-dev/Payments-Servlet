package ua.rudkovskyi.payments.controller.auth;

import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.UserDAO;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(
        name = "RegistrationController",
        urlPatterns = "/registration"
)
public class RegistrationController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RegistrationController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user;
        boolean hasError = false;
        String message = null;

        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            hasError = true;
            message = "Required username and password!";
        } else {
            Connection conn = WebAppUtil.getConnection(request);
            try {
                user = UserDAO.findUserByUsername(conn, username);
                if (user != null) {
                    hasError = true;
                    message = "User exists!";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                hasError = true;
                message = "DB Error occurred (#1)";
            }
        }
        if (hasError) {
            request.setAttribute("message", message);
            doGet(request, response);
        }
        else {
            Connection conn = WebAppUtil.getConnection(request);
            user = new User(username, password);
            try {
                UserDAO.addUser(conn, user);
            } catch (SQLException e){
                e.printStackTrace();
                message = "DB Error occurred (#2)";
                request.setAttribute("message", message);
                doGet(request, response);
            }
            response.sendRedirect("/login");
        }
    }
}
