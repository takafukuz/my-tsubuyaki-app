<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログイン</title>
</head>
<body>
<p>my-tsubuyaki-app 管理サイトへようこそ</p>

<c:if test="${not empty sessionScope.flashMsg }">
  <c:out value="${sessionScope.flashMsg }" />
  <c:remove var="flashMsg" scope="session" />
</c:if>
<p>ログインしてください</p>
	<c:if test="${not empty errorMsg }">
		<c:out value="${errorMsg }"></c:out>
	</c:if>
<form action="login" method="post">
<label>ユーザー名<input type="text" name="username" required></label><br>
<label>パスワード<input type="password" name="password" required></label><br>
<input type="submit" value="送信">
</form>
</body>
</html>