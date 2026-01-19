<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-style.css">
</head>
<body>
<header>
  <h1>つぶやきアプリ管理サイト</h1>
  <nav>
    <div class="nav-buttons">
      <!-- ログアウトボタンはログインページでは不要 -->
    </div>
  </nav>
</header>

<div class="wrap">
  <div class="card card--login">

<!-- 
    <div class="header">
      <p class="lead">つぶやきアプリ管理サイトへようこそ</p>
    </div>
 -->

    <c:if test="${not empty sessionScope.flashMsg }">
      <div class="flash"><c:out value="${sessionScope.flashMsg }" /></div>
      <c:remove var="flashMsg" scope="session" />
    </c:if>

    <c:if test="${not empty errorMsg }">
      <div class="error"><c:out value="${errorMsg }" /></div>
    </c:if>

    <form action="login" method="post">
      <label>ユーザー名
        <input type="text" name="username" required autofocus>
      </label>

      <label>パスワード
        <input type="password" name="password" required>
      </label>

      <div class="actions">
        <input type="submit" value="ログイン">
      </div>
    </form>

    <div class="link-muted">管理者のみアクセス可能です。</div>
  </div>
</div>
</body>
</html>