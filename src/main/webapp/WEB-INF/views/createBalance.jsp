<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-09
  Time: 5:29 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Balance Page</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>Create Balance:</p>
<form action="/u/${userId}" method="POST">
    <label>Enter balance name:
        <input type="text" name="name" required/>
    </label>
    <br/>
    <label>Enter balance amount:
        <input type="number" step="0.01" name="doubleAmount" required/>
    </label>
    <br/>
    <label>
        <input type="radio" name="isLocked" value="true" />
        : Locked
    </label>
    <br/>
    <label>
        <input type="radio" name="isLocked" value="false" checked />
        : Unlocked
    </label>
    <br/>
    <input type="submit" value="Save"/>
</form>
</body>
</html>
