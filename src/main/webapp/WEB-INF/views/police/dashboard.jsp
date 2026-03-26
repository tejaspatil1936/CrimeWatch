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
    <div class="card card-stream">
        <header class="card-head">
            <h2>Incoming alerts</h2>
            <span class="badge badge-live">LIVE</span>
        </header>
        <ul id="alert-list" class="alert-list"></ul>
    </div>

    <div class="card card-map">
        <header class="card-head"><h2>Heatmap</h2></header>
        <div id="map" class="map-surface"></div>
    </div>

    <div class="card card-pending">
        <header class="card-head">
            <h2>Pending reports</h2>
            <span class="count-pill"><c:out value="${pending.size()}"/></span>
        </header>
        <table class="data-table">
            <thead>
                <tr><th>Title</th><th>Severity</th><th>Time</th><th>Action</th></tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${pending}">
                    <tr>
                        <td><c:out value="${r.title}"/></td>
                        <td><span class="sev sev-${r.severity}"><c:out value="${r.severity}"/></span></td>
                        <td><fmt:formatDate value="${r.reportedAtDate}" pattern="HH:mm dd MMM"/></td>
                        <td>
                            <form method="post" action="<c:url value='/dashboard/reports/${r.reportId}/assign'/>">
                                <sec:csrfInput/>
                                <button class="btn btn-sm">Assign to me</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script>
    window.FIREBASE_CONFIG = {
        databaseURL: "<c:out value='${firebaseDatabaseUrl}'/>",
        projectId: "<c:out value='${firebaseProjectId}'/>"
    };
</script>
<script type="module" src="<c:url value='/js/dashboard.js'/>"></script>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet.heat@0.2.0/dist/leaflet-heat.js"></script>
<script src="<c:url value='/js/heatmap.js'/>"></script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
