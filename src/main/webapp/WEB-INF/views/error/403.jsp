<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>
<div class="error-page">
    <div class="error-code">403</div>
    <h1>Access denied</h1>
    <p>You do not have permission to view this page.</p>
    <a href="<c:url value='/'/>" class="btn btn-primary">Go home</a>
</div>
<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
