<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>
<div class="error-page">
    <div class="error-code">404</div>
    <h1>Page not found</h1>
    <p>The page you requested could not be found.</p>
    <a href="<c:url value='/'/>" class="btn btn-primary">Go home</a>
</div>
<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
