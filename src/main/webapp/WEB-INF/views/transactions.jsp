<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-10
  Time: 9:27 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Transactions Page</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>Transactions:</p>
<table>
    <thead>
    <tr>
        <th>Transaction Id</th>
        <th>Source</th>
        <th>Destination</th>
        <th>Amount</th>
        <th>Status</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="transaction" items="${transactions}">
    <tr>
        <td><b>${transaction.id}</b></td>
        <td><span>${transaction.source.id}</span></td>
        <td><span>${transaction.destination.id}</span></td>
        <td><i>${transaction.doubleAmount}</i></td>
        <td><b>
        <c:if test="${transaction.isSent}">
            <c:if test="${isAdmin}">
                <a href="${balanceId}/${transaction.id}">
            </c:if>
            Sent
            <c:if test="${isAdmin}">
                </a>
            </c:if>
        </c:if>
        <c:if test="${!transaction.isSent}">
            <c:if test="${isAdmin}">
                <a href="${balanceId}/${transaction.id}">
            </c:if>
            Prepared
            <c:if test="${isAdmin}">
                </a>
            </c:if>
        </c:if>
        </b></td>
    </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
