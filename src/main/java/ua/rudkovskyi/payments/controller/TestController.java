package ua.rudkovskyi.payments.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "testController",
        urlPatterns = {"/test"}
)
public class TestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.println("<p>" +
                "USER" +
                "<form action='/u/1' method='GET'>" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='GET' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1' method='POST'>" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='POST' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1' method='POST'>" +
                "<input type='hidden' name='_method' value='PUT' />" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='PUT' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1' method='POST'>" +
                "<input type='hidden' name='_method' value='DELETE' />" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='DELETE' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "BALANCE" +
                "<form action='/u/1/2' method='GET'>" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='GET' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1/2' method='POST'>" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='POST' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1/2' method='POST'>" +
                "<input type='hidden' name='_method' value='PUT' />" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='PUT' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1/2' method='POST'>" +
                "<input type='hidden' name='_method' value='DELETE' />" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='DELETE' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "TRANSACTION" +
                "<form action='/u/1/2/3' method='GET'>" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='GET' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1/2/3' method='POST'>" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='POST' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1/2/3' method='POST'>" +
                "<input type='hidden' name='_method' value='PUT' />" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='PUT' />" +
                "</form>" +
                "</p>");
        out.println("<p>" +
                "<form action='/u/1/2/3' method='POST'>" +
                "<input type='hidden' name='_method' value='DELETE' />" +
                "<input type='text' name='name' />" +
                "<br />" +
                "<input type='submit' value='DELETE' />" +
                "</form>" +
                "</p>");
    }
}