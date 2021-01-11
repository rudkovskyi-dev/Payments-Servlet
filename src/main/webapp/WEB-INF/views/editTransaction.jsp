<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-10
  Time: 10:55 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Transaction Page:</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>Transaction info:</p>
<p>
    <a href="/u/${transaction.source.owner.id}/${transaction.source.id}">
        Source id: ${transaction.source.id}
    </a>
</p>
<p>Transaction amount: <b>${transaction.doubleAmount}</b></p>
<p>
    <a href="/u/${transaction.destination.owner.id}/${transaction.destination.id}">
        Destination id: ${transaction.destination.id}
    </a>
</p>
<c:if test="${!transaction.isSent}">
<p><b>Transaction won't be complete if source is locked or doesn't have enough funds</b></p>
<p>Balance status: <b>
    <c:if test="${transaction.source.isLocked}">LOCKED</c:if>
    <c:if test="${!transaction.source.isLocked}">UNLOCKED</c:if>
</b></p>
<p>Source current balance: <b>${transaction.source.doubleAmount}</b></p>
<form action="/u/${transaction.source.owner.id}/${transaction.source.id}/${transaction.id}" method="POST">
    <input type="hidden" value="PUT" name="_method">
    <input type="submit" value="Approve Transaction" />
</form>
<form action="/u/${userId}/${balanceId}/${transactionId}" method="POST">
    <input type="hidden" value="DELETE" name="_method">
    <input type="submit" value="Delete Transaction" />
</form>
</c:if>
<br />
${message}
<br />
<c:if test="${transaction.isSent}">
<p><b>Transaction has been successful!</b></p>
</c:if>
</body>
</html>
