<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-09
  Time: 4:10 a.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Balance Page</title>
    <c:set var="ownerId" value="${balance.owner.id}"/>
    <c:set var="balanceId" value="${balance.id}"/>
    <c:set var="balanceName" value="${balance.name}"/>
    <c:set var="balanceDoubleAmount" value="${balance.doubleAmount}"/>
    <c:set var="balanceIsLocked" value="${balance.isLocked}"/>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>Edit Balance:</p>
<form action="/u/${userId}/${balanceId}" method="POST">
    <input type="hidden" name="_method" value="PUT">
    <label>Change balance name:
        <input type="text" name="name" value="${balanceName}" required/>
    </label>
    <br/>
    <label>Change balance amount:
        <input type="number" step="0.01" name="doubleAmount" value="${balanceDoubleAmount}" required/>
    </label>
    <br/>
    <label>
        <input type="radio" name="isLocked" value="true" <c:if test="${balanceIsLocked}">checked</c:if>/>
        : Locked
    </label>
    <br/>
    <label>
        <input type="radio" name="isLocked" value="false" <c:if test="${!balanceIsLocked}">checked</c:if>/>
        : Unlocked
    </label>
    <br/>
    <input type="submit" value="Save"/>
</form>
<form action="/u/${userId}/${balanceId}" method="POST">
    <input type="hidden" name="_method" value="DELETE">
    <input type="submit" value="Delete Balance"/>
</form>
</body>
</html>
