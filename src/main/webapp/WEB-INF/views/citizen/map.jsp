<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Live data</p>
    <h1>Crime heatmap</h1>
    <p class="lede">Visualise reported incidents across the city. Denser areas indicate higher crime activity.</p>
</section>

<div class="card">
    <div id="map" class="map-surface" style="height:560px;"></div>
</div>

<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet.heat@0.2.0/dist/leaflet-heat.js"></script>
<script src="<c:url value='/js/heatmap.js'/>"></script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
