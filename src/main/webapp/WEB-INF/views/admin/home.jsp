<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Admin dashboard</h1>
</section>

<div class="metric-grid">
    <div class="metric-tile">
        <a href="<c:url value='/admin/zones'/>" style="text-decoration:none;color:inherit">
            <div class="metric-label">Zones</div>
            <h3>Manage zones</h3>
        </a>
    </div>
    <div class="metric-tile">
        <a href="<c:url value='/admin/users'/>" style="text-decoration:none;color:inherit">
            <div class="metric-label">Users</div>
            <h3>Manage users</h3>
        </a>
    </div>
    <div class="metric-tile">
        <a href="<c:url value='/admin/escalations'/>" style="text-decoration:none;color:inherit">
            <div class="metric-label">Escalations</div>
            <h3>View escalations</h3>
        </a>
    </div>
    <div class="metric-tile">
        <a href="<c:url value='/admin/audit'/>" style="text-decoration:none;color:inherit">
            <div class="metric-label">Audit</div>
            <h3>View audit log</h3>
        </a>
    </div>
</div>

<div class="chart-container">
    <h3>Average Police Response Time (weekly)</h3>
    <canvas id="responseTimeChart"></canvas>
</div>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:var(--s-5)">
    <div class="chart-container">
        <h3>Reports by zone</h3>
        <canvas id="zoneChart"></canvas>
    </div>
    <div class="chart-container">
        <h3>Crime type distribution</h3>
        <canvas id="crimeTypeChart"></canvas>
    </div>
</div>

<div style="margin-top:var(--s-5)">
    <a href="<c:url value='/admin/export'/>" class="btn btn-secondary">Export all reports (CSV)</a>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script src="<c:url value='/js/stats-charts.js'/>"></script>
<script>
    initWeeklyResponseChart('responseTimeChart', ${weeklyResponseTimeJson != null ? weeklyResponseTimeJson : '[]'});
    <c:if test="${reportsByZone != null}">
    initReportsByZoneChart('zoneChart',
        [<c:forEach var="e" items="${reportsByZone}" varStatus="s">'${e.key}'<c:if test="${!s.last}">,</c:if></c:forEach>],
        [<c:forEach var="e" items="${reportsByZone}" varStatus="s">${e.value}<c:if test="${!s.last}">,</c:if></c:forEach>]);
    </c:if>
    <c:if test="${crimeTypeDistribution != null}">
    initCrimeTypeChart('crimeTypeChart',
        [<c:forEach var="e" items="${crimeTypeDistribution}" varStatus="s">'${e.key}'<c:if test="${!s.last}">,</c:if></c:forEach>],
        [<c:forEach var="e" items="${crimeTypeDistribution}" varStatus="s">${e.value}<c:if test="${!s.last}">,</c:if></c:forEach>]);
    </c:if>
</script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
