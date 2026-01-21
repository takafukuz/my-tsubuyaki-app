<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>つぶやきアプリ メイン画面</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>つぶやきアプリ</h1>
    <nav>
        <span><c:out value="${loginUser.userName}"/> さんがログイン中</span>
        <!-- ボタン風に配置 -->
        <div class="nav-buttons">
            <a href="${pageContext.request.contextPath}/logout" class="btn">ログアウト</a>
            <a href="${pageContext.request.contextPath}/change-user-info" class="btn">ユーザー情報変更</a>
        </div>
    </nav>
</header>

<main>
    <c:if test="${not empty errorMsg}">
        <div class="error"><c:out value="${errorMsg}"/></div>
        <c:remove var="errorMsg" scope="session"/>
    </c:if>

    <section class="post-form">
        <form action="${pageContext.request.contextPath}/main" method="post">
            <input type="text" name="text" placeholder="なにかつぶやいて">
            <input type="hidden" name="userId" value="${loginUser.userId}">
        	<!-- 投稿ボタン -->
       		<button type="submit" class="btn-primary">投稿</button>
        	<!-- 更新ボタンも同じスタイル -->
        	<a href="${pageContext.request.contextPath}/main" class="btn-primary">更新</a>
        </form>
    </section>

    <section class="timeline">
        <c:forEach var="mutter" items="${mutterList}">
            <div class="mutter">
                <span class="user"><c:out value="${mutter.userName}"/></span>
                <span class="text"><c:out value="${mutter.mutter}"/></span>
                <span class="date">
                    <fmt:formatDate value="${mutter.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="Asia/Tokyo"/>
                </span>
                <c:if test="${loginUser.userId == mutter.userId }"><span class="deleteBtn" data-mutter-id="${mutter.mutterId}" 
                data-mutter="${mutter.mutter }" data-user-id="${mutter.userId }">🗑️</span></c:if>
            </div>
        </c:forEach>
    </section>
    
    <!-- つぶやき削除用フォーム -->
	<form id="delMutterForm" action="${pageContext.request.contextPath}/del-mutter" method="post">
	    <input type="hidden" name="targetMutterId" id="targetMutterId">
	    <input type="hidden" name="targetUserId" id="targetUserId">
	</form>
    
</main>
<script>
	document.querySelectorAll(".deleteBtn").forEach(function(element){
	    element.addEventListener("click", function() {
	    	const targetMutterId = this.dataset.mutterId;  // ← 削除対象ID
	    	const targetUserId = this.dataset.userId;  // ← 削除対象ID
	    	const targetMutter = this.dataset.mutter;  // ← 削除対象ID
	        if (confirm("つぶやき\n「" + targetMutter + "」\nを削除しますか？")) {
		        // 削除対象IDをフォームの値に入れる
	            document.getElementById("targetMutterId").value = targetMutterId;
	            document.getElementById("targetUserId").value = targetUserId;
	            // フォーム送信（POST）
	            document.getElementById("delMutterForm").submit();
	        }
	    });
	});
</script>
</body>
</html>