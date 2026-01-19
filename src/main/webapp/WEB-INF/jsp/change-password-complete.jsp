<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ パスワード変更完了</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>つぶやきアプリ</h1>
</header>

<main class="login-container">
    <p class="login-title">パスワード変更が完了しました</p>

    <div class="user-info">
        <p><strong>ユーザーID：</strong> <c:out value="${loginUser.userId}" /></p>
        <p><strong>ユーザー名：</strong> <c:out value="${loginUser.userName}" /></p>
    </div>

    <div class="links">
        <a href="${pageContext.request.contextPath}/main" class="link-btn">戻る</a>
    </div>
</main>
</body>
</html>