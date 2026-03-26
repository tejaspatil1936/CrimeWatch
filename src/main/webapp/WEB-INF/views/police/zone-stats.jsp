<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Zone statistics</p>
    <h1><c:out value="${stats.zoneName}"/></h1>
</section>

<div class="metric-grid">
    <div class="metric-tile">
        <div class="metric-label">Total reports</div>
        <div class="metric-value"><c:out value="${stats.totalReports}"/></div>
    </div>
    <div class="metric-tile">
        <div class="metric-label">Pending</div>
        <div class="metric-value"><c:out value="${stats.pending}"/></div>
    </div>
    <div class="metric-tile">
        <div class="metric-label">Resolved</div>
        <div class="metric-value"><c:out value="${stats.resolved}"/></div>
    </div>
    <div class="metric-tile">
        <div class="metric-label">Avg response time</div>
        <div class="metric-value"><c:out value="${stats.avgResponseMinutes}"/> min</div>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
