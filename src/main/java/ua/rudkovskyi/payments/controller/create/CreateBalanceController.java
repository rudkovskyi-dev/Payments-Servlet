package ua.rudkovskyi.payments.controller.create;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "createBalanceController",
        urlPatterns = "/WEB-INF/createBalanceController"
)
public class CreateBalanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public CreateBalanceController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }
}
