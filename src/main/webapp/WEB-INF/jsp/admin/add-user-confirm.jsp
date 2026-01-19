<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト - ユーザー追加確認</title>
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
      <h1>ユーザー追加 確認</h1>
      <p class="lead">下記のユーザーを追加します。よろしいですか？</p>
    </div>

    <c:if test="${not empty sessionScope.flashMsg }">
      <div class="flash"><c:out value="${sessionScope.flashMsg }"/></div>
      <c:remove var="flashMsg" scope="session" />
    </c:if>

    <c:if test="${not empty errorMsg }">
      <div class="error"><c:out value="${errorMsg }"/></div>
    </c:if>

    <div class="confirm-block" style="text-align:center; margin:12px 0;">
      <div>ユーザー名：<strong><c:out value="${newUserForm.userName}"/></strong></div>
      <div>パスワード：<strong>●●●●●●</strong></div>
      <div>管理者権限：<strong><c:choose><c:when test="${newUserForm.adminPriv eq 1 }">あり</c:when><c:otherwise>なし</c:otherwise></c:choose></strong></div>
    </div>

    <form action="${pageContext.request.contextPath}/admin/add-user-exec" method="POST">
      <input type="hidden" name="userName" value="${newUserForm.userName}">
      <input type="hidden" name="adminPriv" value="${newUserForm.adminPriv}">
      <input type="hidden" name="password" value="${newUserForm.password}">

      <div class="actions">
        <input type="submit" value="追加" class="btn btn-danger btn-submit">
        <a href="${pageContext.request.contextPath}/admin/add-user" class="btn">戻る</a>
      </div>
    </form>

  </div>
</div>
</body>
</html>