<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-02
  Time: 4:28 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login Page</title>
</head>
<body>
Sign in:
<br />
${message}
<form action="/login" method="POST">
    <input type="text" name="username" placeholder="Username"/>
    <br/>
    <input type="password" name="password" placeholder="Password"/>
    <br/>
    <input type="submit" value="Sign In"/>
</form>
<a href="/registration">Sign up</a>
<a href="/">Main page</a>
</body>
</html>
