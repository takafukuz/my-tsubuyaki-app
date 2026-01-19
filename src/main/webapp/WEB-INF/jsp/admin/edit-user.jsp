<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ管理サイト</title>
</head>
<body>
<h1>ユーザー情報編集</h1>
<p>ユーザー情報を変更してください。</p>
<form action="${pageContext.request.contextPath }/admin/edit-user-confirm" method="POST">
<label>ユーザーID：<input type="text" name="userId" value="${userInfo.userId }" readonly></label><br>
<label>ユーザー名：<input type="text" name="userName" value="${userInfo.userName }"></label><br>
<label>管理者権限：<input type="checkbox" name="adminPriv" value="1" <c:if test="${userInfo.adminPriv  eq 1}">checked</c:if>></label><br>
<input type="submit" value="送信">
</form>

    <div>
        <a href="${pageContext.request.contextPath}/admin/main">戻る</a>
    </div>
    
</body>
</html>