<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<div class="success-box">
    <div class="check-icon">&#10003;</div>
    <h1>Report submitted</h1>
    <p>Your report has been received and will appear on the heatmap shortly.</p>
    <p style="color:var(--ink-tertiary);font-size:var(--fs-small);">Report ID: <strong><c:out value="${reportId}"/></strong></p>
    <hr class="rule">
    <div class="hero-actions">
        <a href="<c:url value='/report/new'/>" class="btn btn-primary">File another report</a>
        <a href="<c:url value='/map'/>" class="btn btn-ghost">View heatmap</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
