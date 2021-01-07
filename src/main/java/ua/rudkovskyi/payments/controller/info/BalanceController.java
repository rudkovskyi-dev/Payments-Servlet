package ua.rudkovskyi.payments.controller.info;

import ua.rudkovskyi.payments.bean.Role;
import ua.rudkovskyi.payments.bean.User;
import ua.rudkovskyi.payments.util.WebAppUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "balanceController",
        urlPatterns = "/WEB-INF/balanceController"
)
public class BalanceController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public BalanceController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is GET</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
        pw.println("<p>Balance " + request.getAttribute("balanceId") + "</p>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!checkAuthority(request)){
            doGet(request, response);
            return;
        }
        if (selectMethod(request, response)){
            return;
        }

        PrintWriter pw = response.getWriter();
        pw.println("<p>This is POST</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
        pw.println("<p>Balance " + request.getAttribute("balanceId") + "</p>");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is PUT</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
        pw.println("<p>Balance " + request.getAttribute("balanceId") + "</p>");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw = response.getWriter();
        pw.println("<p>This is DELETE</p>");
        pw.println("<p>User " + request.getAttribute("userId") + "</p>");
        pw.println("<p>Balance " + request.getAttribute("balanceId") + "</p>");
    }

    public boolean selectMethod(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getParameter("_method");
        if (method != null){
            if (method.equals("PUT")){
                doPut(request, response);
                return true;
            }
            if (method.equals("DELETE")){
                doDelete(request, response);
                return true;
            }
        }
        return false;
    }

    public boolean checkAuthority(HttpServletRequest request){
        User user = WebAppUtil.getUserFromSession(request.getSession());
        return user.getRoles().contains(Role.ADMIN);
    }
}
