<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト</title>
<style>
    tbody tr:hover {
        background-color: #f0f8ff;
        cursor: pointer;
    }
</style>
</head>
<body>
<h1>メイン画面</h1>
<c:if test="${not empty sessionScope.flashMsg  }">
    <c:out value="${sessionScope.flashMsg }"/>
    <c:remove var="flashMsg" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.errorMsg  }">
    <c:out value="${sessionScope.errorMsg }"/>
    <c:remove var="errorMsg" scope="session"/>
</c:if>
<form action="${pageContext.request.contextPath }/admin/del-user" method="POST">
<table border="1">
<thead>
<tr>
<th>選択</th>
<th>ユーザーID</th>
<th>ユーザー名</th>
<th>管理者権限</th>
</tr>
</thead>
<tbody>
<c:forEach var="user" items="${userList }">
<tr onclick="location.href='${pageContext.request.contextPath }/admin/edit-user?userid=${user.userId}'" style="cursor:pointer;">
<td><input type="checkbox" name="selectedUsers" value="${user.userId}" onclick="event.stopPropagation();"></td>
<td><c:out value="${user.userId } "/></td>
<td><c:out value="${user.userName }"/></td>
<td><c:if test="${user.adminPriv == 1}">✔</c:if></td>
</tr>
</c:forEach>
</tbody>
</table>
<input type="submit" value="削除">
</form>
<div><a href="${pageContext.request.contextPath}/admin/logout">ログアウト</a></div>
<div><a href="${pageContext.request.contextPath}/admin/add-user">ユーザーの追加</a></div>
</body>
</html>