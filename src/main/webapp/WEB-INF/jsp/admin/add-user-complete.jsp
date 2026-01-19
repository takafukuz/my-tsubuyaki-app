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

    <h1>ユーザー追加</h1>


    <div >
        <c:out value="${newUserForm.userName}" /> の追加が完了しました。
    </div>

    <div>
        <a href="${pageContext.request.contextPath }/admin/add-user">ユーザー追加画面へ</a>
    </div>

    <div>
        <a href="${pageContext.request.contextPath }/admin/main">メイン画面へ</a>
    </div>

</body>
</html>