<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト - ユーザー追加</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-style.css">
</head>
<body>
<header>
  <h1>つぶやきアプリ管理サイト</h1>
  <nav>
    <div class="nav-buttons">
      <a href="${pageContext.request.contextPath}/admin/logout" class="btn">ログアウト</a>
    </div>
  </nav>
</header>

<div class="wrap">
  <div class="card card--wide">
    <div class="header">
      <h1>ユーザー追加</h1>
      <p class="lead">新しいユーザーを登録します</p>
    </div>

    <c:if test="${not empty sessionScope.flashMsg }">
      <div class="flash"><c:out value="${sessionScope.flashMsg }"/></div>
      <c:remove var="flashMsg" scope="session" />
    </c:if>

    <c:if test="${not empty errorMsg }">
      <div class="error"><c:out value="${errorMsg }"/></div>
      <c:remove var="errorMsg" scope="session" />
    </c:if>

    <form action="${pageContext.request.contextPath }/admin/add-user" method="POST">
      <label>ユーザー名
        <input type="text" name="userName" required>
      </label>

      <label>パスワード
        <input type="password" name="password" required>
      </label>

      <label>パスワード（確認）
        <input type="password" name="confirmPassword" required>
      </label>

      <label class="form-checkbox">
        <input type="checkbox" name="adminPriv" value="1">
        管理者権限を付与
      </label>

      <div class="actions">
        <input type="submit" value="送信" class="btn btn-danger btn-submit">
        <a href="${pageContext.request.contextPath}/admin/main" class="btn">戻る</a>
      </div>
    </form>

  </div>
</div>
</body>
</html>