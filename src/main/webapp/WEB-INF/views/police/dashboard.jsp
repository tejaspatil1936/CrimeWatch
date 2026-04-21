<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="dashboard-head">
    <p class="eyebrow">Officer dashboard</p>
    <h1>Live incident feed</h1>
    <p class="lede">New reports stream here in real time. Click any alert to review details and assign yourself.</p>
</section>

<div class="dashboard-grid">

    <%-- ── Incoming alerts ── --%>
    <div class="card card-stream">
        <header class="card-head">
            <h2>Incoming alerts</h2>
            <span class="badge badge-live">LIVE</span>
        </header>
        <ul id="alert-list" class="alert-list">
            <li style="padding:1rem;color:var(--ink-tertiary);font-size:0.85rem">Loading…</li>
        </ul>
    </div>

    <%-- ── Heatmap ── --%>
    <div class="card card-map">
        <header class="card-head"><h2>Heatmap</h2></header>
        <div id="map" class="map-surface"></div>
    </div>

    <%-- ── Pending reports with status update ── --%>
    <div class="card card-pending">
        <header class="card-head">
            <h2>Pending reports</h2>
            <span class="count-pill" id="pendingCount"><c:out value="${pending.size()}"/></span>
        </header>
        <table class="data-table">
            <thead>
                <tr><th>Title</th><th>Severity</th><th>Time</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${pending}">
                    <tr id="row-${r.reportId}">
                        <td><c:out value="${r.title}"/></td>
                        <td><span class="sev sev-${r.severity}"><c:out value="${r.severity}"/></span></td>
                        <td><fmt:formatDate value="${r.reportedAtDate}" pattern="HH:mm dd MMM"/></td>
                        <td><span class="sev sev-${r.severity}" id="status-${r.reportId}"><c:out value="${r.status}"/></span></td>
                        <td class="action-cell">
                            <form method="post" action="<c:url value='/dashboard/reports/${r.reportId}/assign'/>" style="display:inline">
                                <sec:csrfInput/>
                                <button class="btn btn-sm btn-secondary">Assign to me</button>
                            </form>
                            <form method="post" action="<c:url value='/dashboard/reports/${r.reportId}/status'/>" style="display:inline;margin-left:4px">
                                <sec:csrfInput/>
                                <select name="status" class="status-select" onchange="this.form.submit()">
                                    <option value="">Update status…</option>
                                    <option value="PENDING">Pending</option>
                                    <option value="ASSIGNED">Assigned</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="RESOLVED">Resolved</option>
                                </select>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty pending}">
                    <tr><td colspan="5" class="empty">No pending reports.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<style>
.action-cell { white-space: nowrap; }
.status-select {
    font-family: var(--font-sans);
    font-size: var(--fs-xs);
    padding: 4px 8px;
    border: 1px solid var(--rule-hair);
    border-radius: var(--radius-2);
    background: var(--bg-surface);
    color: var(--ink-primary);
    cursor: pointer;
}
.status-select:focus { outline: none; border-color: var(--ink-primary); }
</style>

<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet.heat@0.2.0/dist/leaflet-heat.js"></script>
<script>
// init dashboard map
window.dashMap = L.map('map').setView([18.5204, 73.8567], 12);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap', maxZoom: 19
}).addTo(window.dashMap);
window.heatLayer = L.heatLayer([], {
    radius: 30, blur: 20, maxZoom: 17,
    gradient: { 0.2:'#6b7c5b', 0.4:'#8a7730', 0.6:'#a85a2b', 0.9:'#8a2b2b' }
}).addTo(window.dashMap);
</script>
<script src="<c:url value='/js/dashboard.js'/>"></script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
