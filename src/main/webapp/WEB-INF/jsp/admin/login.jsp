<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<p>my-tsubuyaki-app 管理サイトへようこそ
<p>ログインしてください</p>
<form action="login" method="post">
<label>ユーザー名<input type="text" name="username" required></label><br>
<label>パスワード<input type="password" name="password" required></label><br>
<input type="submit" value="送信">
</form>
</body>
</html>