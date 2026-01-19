<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト - ユーザー削除 確認</title>
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
      <h1>ユーザー削除 確認</h1>
      <p class="lead">下記のユーザーを削除します。</p>
    </div>

    <c:if test="${not empty sessionScope.flashMsg }">
      <div class="flash"><c:out value="${sessionScope.flashMsg }"/></div>
      <c:remove var="flashMsg" scope="session" />
    </c:if>

    <c:if test="${not empty errorMsg }">
      <div class="error"><c:out value="${errorMsg }"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath }/admin/del-user-exec" method="POST">
      <div class="table-wrap">
        <table class="user-table">
          <thead>
            <tr>
              <th>ユーザーID</th>
              <th>ユーザー名</th>
              <th>管理者権限</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="user" items="${userList }">
              <tr>
                <td><c:out value="${user.userId }"/></td>
                <td><c:out value="${user.userName }"/></td>
                <td style="text-align:center;"><c:if test="${user.adminPriv == 1}">✔</c:if></td>
                <input type="hidden" name="selectedUsers" value="${user.userId }">
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <div class="actions">
        <input type="submit" value="削除" class="btn btn-danger btn-submit">
        <a href="${pageContext.request.contextPath}/admin/main" class="btn">戻る</a>
      </div>
    </form>

  </div>
</div>
</body>
</html>