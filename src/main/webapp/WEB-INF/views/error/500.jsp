<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>
<div class="error-page">
    <div class="error-code">500</div>
    <h1>Internal error</h1>
    <p>Something went wrong. Please try again later.</p>
    <a href="<c:url value='/'/>" class="btn btn-primary">Go home</a>
</div>
<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
