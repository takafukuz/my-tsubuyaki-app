<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>del-user-confirm</title>
</head>
<body>
    <p>下記のユーザーを削除します。</p>

<form action="${pageContext.request.contextPath }/admin/del-user-exec" method="POST">
<table border="1">
<thead>
<tr>
<th>ユーザーID</th>
<th>ユーザー名</th>
<th>管理者権限</th>
</tr>
</thead>
<c:forEach var="user" items="${userList }">
<tr>
<td><c:out value="${user.userId } "/></td>
<td><c:out value="${user.userName }"/></td>
<td><c:if test="${user.adminPriv == 1}">✔</c:if></td>
<input type="hidden" name="selectedUsers" value="${user.userId }">
</tr>
</c:forEach>
</table>
<input type="submit" value="削除">
</form>
</body>
</html>