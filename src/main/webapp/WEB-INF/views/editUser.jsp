<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-09
  Time: 7:27 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit User Page</title>
</head>
<body>
<p>Edit users:</p>
<jsp:include page="_header.jsp"/>
<form action="/u/${userId}" method="POST">
    <input type="hidden" name="_method" value="PUT">
    <label>Change username:
        <input type="text" name="username" value="${user.username}" required/>
    </label>
    <span>Current roles:
        <c:forEach items="${user.roles}" var="role" varStatus="roleLoop">
            <span> ${role}<c:if test="${!roleLoop.last}">,</c:if></span>
        </c:forEach>
    </span>
    <br/>
    <span>Select new roles: </span>
    <c:forEach items="${allRoles}" var="role" varStatus="roleLoop">
        <label><input type="checkbox" name="role" value="${role}" >${role}</label>
    </c:forEach>
    <br />
    <label>
        <input type="radio" name="isActive" value="true" <c:if test="${user.isActive}">checked</c:if>/>
        : Activated
    </label>
    <br/>
    <label>
        <input type="radio" name="isActive" value="false" <c:if test="${!user.isActive}">checked</c:if>/>
        : Deactivated
    </label>
    <br />
    <input type="submit" value="Save" />
    <br />
    ${message}
</form></body>
</html>
