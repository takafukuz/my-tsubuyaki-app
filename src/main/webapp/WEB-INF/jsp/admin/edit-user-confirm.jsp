<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>edit-user-confirm</title>
</head>
<body>

    <h1>ユーザー情報編集</h1>
    <p>下記の情報に更新します</p>

    <div>ユーザーID：<c:out value="${userInfo.userId}"/></div>
    
    <div>ユーザー名：<c:out value="${userInfo.userName}"/></div>

    <div> 管理者権限：
    <c:choose>
        <c:when test="${userInfo.adminPriv == 1}">あり</c:when>
        <c:otherwise>なし</c:otherwise>
    </c:choose>
    </div>

    <form action="${pageContext.request.contextPath}/admin/edit-user-exec" method="POST">
        <input type="hidden" name="userId" value="${userInfo.userId}">
        <input type="hidden" name="userName" value="${userInfo.userName}">
        <input type="hidden" name="adminPriv" value="${userInfo.adminPriv}">
        <input type="submit" value="更新">
    </form>
    
    <div>
        <a href="${pageContext.request.contextPath}/admin/edit-user?userid=${userInfo.userId}">戻る</a>
    </div>

</body>
</html>