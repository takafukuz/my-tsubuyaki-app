<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>edit-user-complete</title>
</head>
<body>
    <h1>ユーザー情報編集</h1>
    <p>下記の情報で更新しました</p>

    <div>ユーザーID：<c:out value="${userInfo.userId}"/></div>

    <div>ユーザー名：<c:out value="${userInfo.userName}"/></div>

    <div>管理者権限：
            <c:choose>
                <c:when test="${userInfo.adminPriv == 1}">あり</c:when>
                <c:otherwise>なし</c:otherwise>
            </c:choose>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/main">メイン画面へ</a>
    </div>
    
</body>
</html>