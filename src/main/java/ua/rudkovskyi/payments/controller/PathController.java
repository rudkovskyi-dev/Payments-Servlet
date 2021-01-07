package ua.rudkovskyi.payments.controller;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "pathController",
        urlPatterns = {"/u/*"}
)
public class PathController extends HttpServlet {
    private static final int MAX_PATH_LENGTH = 4;
    private static final int MIN_PATH_LENGTH = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        List<Long> path = pathToArray(request, response);
        pw.println("<p>Path</p>");
        pw.println("<p>Request context path:" + request.getContextPath() + "</p>");
        pw.println("<p>Request path info:" + request.getPathInfo() + "</p>");
        pw.println("<p>Path length: " + path.size() + "</p>");
        for (long p : path) {
            pw.println("<p>Path " + p + " </p>");
        }
        forward(path, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = pathToArray(request, response);

        forward(path, request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = pathToArray(request, response);

        forward(path, request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> path = pathToArray(request, response);

        forward(path, request, response);
    }

    public static long numToLong(String strNum) {
        if (strNum == null) {
            return -1L;
        }
        long num = -1L;
        if (strNum.matches("^\\d{1,19}$"))
            try {
                num = Long.parseLong(strNum);
            }
            catch (NumberFormatException e) {
                return -1L;
            }
        if (num < 0) {
            return -1L;
        } else {
            return num;
        }
    }

    public List<Long> pathToArray(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        String[] path = pathInfo.split("/");
        List<Long> pathLong = new ArrayList<>();
        if (path.length < MIN_PATH_LENGTH || path.length > MAX_PATH_LENGTH) {
            getServletContext().getRequestDispatcher("/404").forward(request, response);
        }
        for (int i = MIN_PATH_LENGTH; i < path.length && i < MAX_PATH_LENGTH; i++) {
            long number = numToLong(path[i]);
            if (number != -1L) {
                pathLong.add(numToLong(path[i]));
            } else {
                getServletContext().getRequestDispatcher("/404").forward(request, response);
            }
        }
        return pathLong;
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
