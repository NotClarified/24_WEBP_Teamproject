<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BmbxuPwQa2lc/FVzBcNJ7UAyJxM6wuqIj61tLrc4wSX0szH/Ev+nYRRuWlolflfl" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/js/bootstrap.bundle.min.js" integrity="sha384-b5kHyXgcpbZJO/tY9Ul7kGkf1S0CWuKcCD38l8YkeH8z8QjE0GmW1gYU5S9FOnJ0" crossorigin="anonymous"></script>
<title>뉴스 수정</title>
</head>
<body>
<div class="container w-75 mt-5 mx-auto">

    <form action="news.nhn" method="post" enctype="multipart/form-data">
        <!-- 수정 요청을 위한 action 파라미터 설정 -->
        <input type="hidden" name="action" value="updateNews">
        <input type="hidden" name="aid" value="${news.aid}">

        <!-- 제목 필드 -->
        <label class="form-label">제목</label> 
        <input type="text" name="title" class="form-control" value="${news.title}" required>

        <!-- 이미지 필드 -->
        <label class="form-label">이미지</label> 
        <input type="file" name="file" class="form-control">
        <input type="hidden" name="img" value="${news.img}"> <!-- 기존 이미지 경로 유지 -->

        <!-- 기사 내용 필드 -->
        <label class="form-label">기사내용</label>
        <textarea cols="50" rows="5" name="content" class="form-control" required>${news.content}</textarea>

        <!-- 수정 완료 버튼 -->
        <input type="submit" value="수정 완료" class="btn btn-primary mt-3">
    </form>
</div>
</body>
</html>
