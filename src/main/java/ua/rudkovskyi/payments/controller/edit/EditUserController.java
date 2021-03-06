package ua.rudkovskyi.payments.controller.edit;

import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.dao.UserDAO;
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
import java.util.EnumSet;

@WebServlet(
        name = "editUserController",
        urlPatterns = "/WEB-INF/editUserController"
)
public class EditUserController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (PathUtil.isUserDNEWithRedirect404(request, response)) {
            return;
        }
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        request.setAttribute("isAdmin", isAdmin);
        if (!isAdmin ) {
            request.getRequestDispatcher("/404").forward(request, response);
        }
        long requestedUserId = Long.parseLong(request.getAttribute("userId").toString());
        try {
            User user = UserDAO.findUserById(WebAppUtil.getConnection(request), requestedUserId);
            request.setAttribute("user", user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        request.setAttribute("allRoles", EnumSet.allOf(Role.class));
        request.getRequestDispatcher("/WEB-INF/views/editUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
