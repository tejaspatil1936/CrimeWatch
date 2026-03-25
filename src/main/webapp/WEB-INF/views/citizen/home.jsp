<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<div class="hero">
    <h1>CrimeWatch</h1>
    <p>Crowdsourced crime reporting for safer communities. Report incidents anonymously, view the live heatmap, and help law enforcement respond faster.</p>
    <div class="hero-actions">
        <a href="<c:url value='/report/new'/>" class="btn btn-primary">Report an incident</a>
        <a href="<c:url value='/map'/>" class="btn btn-ghost" style="color:var(--ink-inverse)">View heatmap</a>
    </div>
</div>

<section class="page-head">
    <p class="eyebrow">How it works</p>
    <h2>Three steps to a safer city</h2>
</section>

<div class="metric-grid">
    <div class="metric-tile">
        <div class="metric-label">Step 1</div>
        <h3>Report</h3>
        <p>Submit a crime report with your location. No account required.</p>
    </div>
    <div class="metric-tile">
        <div class="metric-label">Step 2</div>
        <h3>Visualise</h3>
        <p>Reports appear on the live heatmap, showing crime density across zones.</p>
    </div>
    <div class="metric-tile">
        <div class="metric-label">Step 3</div>
        <h3>Respond</h3>
        <p>Officers receive real-time alerts and assign themselves to incidents.</p>
    </div>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
