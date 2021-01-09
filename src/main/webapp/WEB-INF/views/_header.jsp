<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-05
  Time: 6:59 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="_menu.jsp"/>
<div>
    <c:if test="${!empty sessionScope.loggedInUser.username}">
        Hello <b>${sessionScope.loggedInUser.username}</b>!
    </c:if>
    <c:if test="${empty sessionScope.loggedInUser.username}">
        Hello <b>Guest</b>!
    </c:if>
</div>
