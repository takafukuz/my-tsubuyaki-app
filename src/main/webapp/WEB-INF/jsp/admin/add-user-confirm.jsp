<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>add-user-confirm</title>
</head>
<body>


    <h1>ユーザー追加確認</h1>

    <p>下記のユーザーを追加します。よろしいですか？</p>

    <div>
        ユーザー名：<c:out value="${newUserForm.userName}" /><br>
        パスワード：<c:out value="${newUserForm.password}" /><br>
        管理者権限：<c:choose><c:when test="${newUserForm.adminPriv eq 1 }">あり</c:when><c:otherwise>なし</c:otherwise></c:choose><br>
    </div>

    <form action="${pageContext.request.contextPath}/admin/add-user-exec" method="POST">
        <input type="hidden" name="userName" value="${newUserForm.userName}">
        <input type="hidden" name="adminPriv" value="${newUserForm.adminPriv}">
        <input type="hidden" name="password" value="${newUserForm.password}">
        <input type="submit" value="追加">
    </form>

    <div>
        <a href="${pageContext.request.contextPath}/admin/add-user">戻る</a>
    </div>


</body>
</html>