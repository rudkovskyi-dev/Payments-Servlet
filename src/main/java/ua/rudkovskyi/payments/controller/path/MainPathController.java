package ua.rudkovskyi.payments.controller.path;

import ua.rudkovskyi.payments.util.PathUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "mainPathController",
        urlPatterns = {"/u/*"}
)
public class MainPathController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = PathUtil.pathToArray(request);
        if (path == null){
            request.getRequestDispatcher("/404").forward(request, response);
        } else {
            forward(path, request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = PathUtil.pathToArray(request);
        if (path == null){
            request.getRequestDispatcher("/404").forward(request, response);
        } else {
            forward(path, request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = PathUtil.pathToArray(request);
        if (path == null){
            request.getRequestDispatcher("/404").forward(request, response);
        } else {
            forward(path, request, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = PathUtil.pathToArray(request);
        if (path == null){
            request.getRequestDispatcher("/404").forward(request, response);
        } else {
            forward(path, request, response);
        }
    }

    public static void forward(List<Long> path,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (path.size()){
            case (1):
                request.setAttribute("userId", path.get(0));
                request.getRequestDispatcher("/WEB-INF/userController").forward(request, response);
                break;
            case (2):
                request.setAttribute("userId", path.get(0));
                request.setAttribute("balanceId", path.get(1));
                request.getRequestDispatcher("/WEB-INF/balanceController").forward(request, response);
                break;
            case (3):
                request.setAttribute("userId", path.get(0));
                request.setAttribute("balanceId", path.get(1));
                request.setAttribute("transactionId", path.get(2));
                request.getRequestDispatcher("/WEB-INF/transactionController").forward(request, response);
                break;
        }
    }
}
