<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>user-add</title>
</head>
<body>
<h1>ユーザー追加</h1>

<p>ユーザー情報を入力してください。</p>

<!-- errorMsgがあれば表示 -->
<c:if test="${not empty errorMsg }">
  <c:out value="${errorMsg }"/>
</c:if>

<form action="${pageContext.request.contextPath }/admin/add-user" method="POST">
<label>ユーザー名：<input type="text" name="userName" required></label><br>
<label>パスワード：<input type="password" name="password" required></label><br>
<label>パスワード（確認）：<input type="password" name="confirmPassword" required></label><br>
<label>管理者権限：<input type="checkbox" name="adminPriv" value="1" required></label><br>
<input type="submit" value="送信">
</form>

</body>
</html>