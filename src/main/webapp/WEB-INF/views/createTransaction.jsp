<%--
  Created by IntelliJ IDEA.
  User: NULL
  Date: 2021-01-10
  Time: 10:02 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Transaction Page</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<p>Your current balance: ${balance.doubleAmount}</p>
<form action="/u/${userId}/${balanceId}" method="POST">
    <label>Payment destination:
        <input type="number" min="1" step="1" name="destinationId" required="required"/>
    </label>
    <br/>
    <label>Amount:
        <input type="number" min="0.01" max="${balance.doubleAmount}" step="0.01" name="doubleAmount" required="required"/>
    </label>
    <br/>
    <input type="submit" value="Send Payment" />
</form>
</body>
</html>
