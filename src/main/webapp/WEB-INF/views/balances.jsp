<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-08
  Time: 7:02 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Balances Page</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>Balances:</p>
<p>
    <c:forEach var="balance" items="${balances}">
        <c:set var="ownerId" value="${balance.owner.id}"/>
        <c:set var="balanceId" value="${balance.id}"/>
        <c:set var="balanceName" value="${balance.name}"/>
        <c:set var="balanceDoubleAmount" value="${balance.doubleAmount}"/>
        <div>
            <b>${balanceId}</b>
            <span><a href="/u/${ownerId}/${balanceId}">${balanceName}</a></span>
            <i>${balanceDoubleAmount}</i>
            <span>
                <c:if test="${isAdmin}">
                    <a href="/edit/${ownerId}/${balanceId}">Edit</a>
                </c:if>
            </span>
            <strong>
                <c:choose>
                    <c:when test="${!balance.isLocked}">
                        <c:if test="${!isAdmin}">
                            <a href="/edit/${ownerId}/${balanceId}"><b>LOCK</b></a>
                            <a href="/pay/${ownerId}/${balanceId}">Make payment</a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <span>LOCKED</span>
                        <c:choose>
                            <c:when test="${balance.isRequested}">
                                <b>Unlock requested!</b>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${!isAdmin}">
                                    <a href="/edit/${ownerId}/${balanceId}">Request unlock</a>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </strong>
        </div>
    </c:forEach>
</p>
</body>
</html>
