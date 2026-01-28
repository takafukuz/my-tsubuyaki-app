<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-style.css">
<!-- 追加: ユーザーID列を広げ、長いIDを折り返すスタイル -->
<style>
  .user-table { table-layout: fixed; width: 100%; }
  /* ユーザーID列幅（36桁のIDに十分な幅） */
  .col-userid { width: 420px; /* 調整可 */ word-break: break-all; white-space: normal; }
  /* チェックボックス列は小さめに */
  .col-check { width: 64px; }
</style>
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
      <h1>ユーザー一覧</h1>
    </div>

    <c:if test="${not empty sessionScope.flashMsg  }">
      <div class="flash"><c:out value="${sessionScope.flashMsg }"/></div>
      <c:remove var="flashMsg" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMsg  }">
      <div class="error"><c:out value="${sessionScope.errorMsg }"/></div>
      <c:remove var="errorMsg" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath }/admin/del-user" method="POST">
      <!-- buttons shown under 'ユーザー一覧' -->
      <div class="top-actions">
        <div class="inner inner--tight">
          <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/add-user">追加</a>
          <input type="submit" value="削除" class="btn btn-delete">
        </div>
      </div>
      <div class="table-wrap">
      <table class="user-table" border="0">
      <thead>
      <tr>
      <th class="col-check">選択</th>
      <th class="col-userid">ユーザーID</th>
      <th>ユーザー名</th>
      <th>管理者権限</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="user" items="${userList }">
      <tr onclick="location.href='${pageContext.request.contextPath }/admin/edit-user?userid=${user.userId}'">
      <td class="col-check"><input type="checkbox" name="selectedUsers" value="${user.userId}" onclick="event.stopPropagation();"></td>
      <td class="col-userid"><c:out value="${user.userId } "/></td>
      <td><c:out value="${user.userName }"/></td>
      <td><c:if test="${user.adminPriv == 1}">✔</c:if></td>
      </tr>
      </c:forEach>
      </tbody>
      </table>
      </div>
    </form>
  </div>
</div>
</body>
</html>
