<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>どこつぶ - パスワード変更</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>どこつぶ</h1>
</header>

<main class="login-container">
    <p class="login-title">新しいパスワードを入力してください</p>

    <c:if test="${not empty errorMsg}">
        <p class="msg-error"><c:out value="${errorMsg}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/change-password" method="post" class="login-form">
        <label for="userId">ユーザーID</label>
        <input type="text" id="userId" name="userId" value="${loginUser.userId}" readonly>

        <label for="userName">ユーザー名</label>
        <input type="text" id="userName" name="userName" value="${loginUser.userName}" readonly>

        <label for="password">新しいパスワード</label>
        <input type="password" id="password" name="password" required>

        <label for="confirmPassword">新しいパスワード（確認）</label>
        <input type="password" id="confirmPassword" name="confirmPassword" required>

        <button type="submit" class="btn-small">送信</button>
    </form>

    <!-- 戻るボタンを追加 -->
    <div class="links">
        <a href="${pageContext.request.contextPath}/change-user-info" class="link-btn">ユーザー情報変更に戻る</a>
    </div>
</main>
</body>
</html>