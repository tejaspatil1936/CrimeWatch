<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Report an incident</p>
    <h1>File a crime report</h1>
    <p class="lede">Your report is anonymous by default. Geo-coordinates are captured from your browser and stored alongside the report to place it on the public heatmap.</p>
</section>

<form:form method="post" action="/report/submit" modelAttribute="reportDto" cssClass="form-stack">
    <sec:csrfInput/>

    <div class="field">
        <form:label path="title">Title</form:label>
        <form:input path="title" placeholder="e.g., Suspicious figure near school gate"/>
        <form:errors path="title" cssClass="field-error"/>
    </div>

    <div class="field">
        <form:label path="crimeType">Type</form:label>
        <form:select path="crimeType">
            <form:option value="" label="— select —"/>
            <form:option value="THEFT">Theft</form:option>
            <form:option value="ASSAULT">Assault</form:option>
            <form:option value="VANDALISM">Vandalism</form:option>
            <form:option value="SUSPICIOUS">Suspicious Activity</form:option>
            <form:option value="OTHER">Other</form:option>
        </form:select>
        <form:errors path="crimeType" cssClass="field-error"/>
    </div>

    <div class="field">
        <form:label path="severity">Severity</form:label>
        <form:select path="severity">
            <form:option value="LOW">Low</form:option>
            <form:option value="MEDIUM">Medium</form:option>
            <form:option value="HIGH">High</form:option>
            <form:option value="CRITICAL">Critical</form:option>
        </form:select>
    </div>

    <div class="field">
        <form:label path="description">What happened?</form:label>
        <form:textarea path="description" rows="5" placeholder="Describe the incident in your own words."/>
        <form:errors path="description" cssClass="field-error"/>
    </div>

    <div class="field-row">
        <div class="field">
            <form:label path="latitude">Latitude</form:label>
            <form:input path="latitude" type="number" step="0.000001" readonly="true"/>
        </div>
        <div class="field">
            <form:label path="longitude">Longitude</form:label>
            <form:input path="longitude" type="number" step="0.000001" readonly="true"/>
        </div>
        <button type="button" id="capture-loc" class="btn btn-secondary">Capture my location</button>
    </div>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">Submit report</button>
        <a href="<c:url value='/'/>" class="btn btn-ghost">Cancel</a>
    </div>
</form:form>

<script src="<c:url value='/js/report-form.js'/>"></script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
