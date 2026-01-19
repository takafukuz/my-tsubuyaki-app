<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ ログイン</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>つぶやきアプリ</h1>
</header>

<main class="login-container">
    <p class="welcome">つぶやきアプリ へようこそ</p>

    <c:if test="${not empty errorMsg}">
        <p class="msg-error"><c:out value="${errorMsg}"/></p>
    </c:if>
    <c:if test="${not empty sessionScope.flashMsg}">
        <p class="msg-error"><c:out value="${sessionScope.flashMsg}"/></p>
        <c:remove var="flashMsg" scope="session"/>
    </c:if>

    <p class="login-title">ログインしてください</p>
    <form action="${pageContext.request.contextPath}/login" method="post" class="login-form">
        <label for="userName">ユーザー名</label>
        <input type="text" id="userName" name="userName" required>

        <label for="password">パスワード</label>
        <input type="password" id="password" name="password" required>

        <button type="submit" class="btn-small">送信</button>
    </form>
</main>
</body>
</html>