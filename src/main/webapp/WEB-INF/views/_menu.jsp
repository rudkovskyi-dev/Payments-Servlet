<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-05
  Time: 6:59 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:choose>
    <c:when test="${!empty sessionScope.loggedInUser.username}">
        <a href="/logout">Sign out</a>
        <br />
        <a href="/u/${sessionScope.loggedInUser.id}">Main page</a>
    </c:when>
    <c:otherwise>
        <a href="/login">Sign in</a>
        <a href="/registration">Sign up</a>
        <br />
        <a href="/login">Main page</a>
    </c:otherwise>
</c:choose>

