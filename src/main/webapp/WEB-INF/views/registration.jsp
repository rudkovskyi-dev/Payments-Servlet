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
    <title>Registration Page</title>
</head>
<body>
Sign up:
<br/>
${message}
<form action="/registration" method="POST">
    <input type="text" name="username" placeholder="Username"/>
    <br/>
    <input type="password" name="password" placeholder="Password"/>
    <br/>
    <input type="submit" value="Register"/>
</form>
</body>
</html>
