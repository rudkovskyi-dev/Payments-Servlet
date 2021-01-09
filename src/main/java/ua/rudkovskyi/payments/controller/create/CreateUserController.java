package ua.rudkovskyi.payments.controller.create;

import ua.rudkovskyi.payments.util.AuthUtil;
import ua.rudkovskyi.payments.util.PathUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "createUserController",
        urlPatterns = "/WEB-INF/createUserController"
)
public class CreateUserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public CreateUserController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (PathUtil.isUserDNEWithRedirect404(request, response)){
            return;
        }
        boolean isAdmin = AuthUtil.checkAdminAuthority(request);
        if (!(isAdmin)){
            request.getRequestDispatcher("/404").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/createBalance.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
