<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>メイン</title>
<style>
    tbody tr:hover {
        background-color: #f0f8ff;
        cursor: pointer;
    }
</style>
</head>
<body>
<h1>管理サイトメイン</h1>
<c:if test="${not empty sessionScope.errorMsg  }">
    <c:out value="${sessionScope.errorMsg }"/>
    <c:remove var="errorMsg" scope="session"/>
</c:if>
<table border="1">
<thead>
<tr>
<th>ユーザーID</th>
<th>ユーザー名</th>
<th>管理者権限</th>
</tr>
</thead>
<tbody>
<c:forEach var="user" items="${userList }">
<tr onclick="location.href='${pageContext.request.contextPath }/admin/edit-user?userid=${user.userId}'" style="cursor:pointer;">
<td><c:out value="${user.userId } "/></td>
<td><c:out value="${user.userName }"/></td>
<td><c:if test="${user.adminPriv == 1}">✔</c:if></td>
</tr>
</c:forEach>
</tbody>
</table>
<div><a href="${pageContext.request.contextPath}/admin/logout">ログアウト</a></div>
<div><a href="${pageContext.request.contextPath}/admin/add-user">ユーザーの追加</a></div>
</body>
</html>