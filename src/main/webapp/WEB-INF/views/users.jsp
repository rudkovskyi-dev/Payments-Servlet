<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-09
  Time: 6:55 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Users Page</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>List of users:</p>

<table>
    <thead>
    <tr>
        <th>Name</th>
        <th>Role</th>
        <th></th>
        <th></th>
    </tr>
    </thead>
    <tbody>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.username}</td>
                <td>
                    <c:forEach items="${user.roles}" var="role" varStatus="roleLoop">
                    <span> ${role}<c:if test="${!roleLoop.last}">,</c:if></span>
                    </c:forEach>
                </td>
                <td><a href="edit/${user.id}">Edit Profile</a></td>
                <td><a href="/u/${user.id}">Balances</a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
</body>
</html>
