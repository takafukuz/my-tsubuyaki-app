<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>どこつぶ - ユーザー情報変更</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>どこつぶ</h1>
</header>

<main class="login-container">
    <p class="login-title">ユーザー情報を変更してください</p>

    <c:if test="${not empty errorMsg}">
        <p class="msg-error"><c:out value="${errorMsg}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/change-user-info" method="post" class="login-form">
        <label for="userId">ユーザーID</label>
        <input type="text" id="userId" name="userId" value="${loginUser.userId}" readonly>

        <label for="newUserName">新しいユーザー名</label>
        <input type="text" id="newUserName" name="newUserName" value="${loginUser.userName}" required>

        <button type="submit" class="btn-small">送信</button>
    </form>

    <div class="links">
        <a href="${pageContext.request.contextPath}/change-password" class="link-btn">パスワード変更</a>
        <a href="${pageContext.request.contextPath}/main" class="link-btn">戻る</a>
    </div>
</main>
</body>
</html>