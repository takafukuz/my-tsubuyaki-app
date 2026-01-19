<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト - ユーザー編集</title>
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
      <h1>ユーザー情報編集</h1>
      <p class="lead">ユーザー情報を変更してください。</p>
    </div>

    <c:if test="${not empty sessionScope.flashMsg }">
      <div class="flash"><c:out value="${sessionScope.flashMsg }"/></div>
      <c:remove var="flashMsg" scope="session" />
    </c:if>

    <c:if test="${not empty sessionScope.errorMsg }">
      <div class="error"><c:out value="${sessionScope.errorMsg }"/></div>
      <c:remove var="errorMsg" scope="session" />
    </c:if>

    <form action="${pageContext.request.contextPath }/admin/edit-user-confirm" method="POST">
      <label>ユーザーID：
        <input type="text" name="userId" value="${userInfo.userId }" readonly>
      </label>

      <label>ユーザー名：
        <input type="text" name="userName" value="${userInfo.userName }">
      </label>

      <label class="form-checkbox">
        <input type="checkbox" name="adminPriv" value="1" <c:if test="${userInfo.adminPriv  eq 1}">checked</c:if>>
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