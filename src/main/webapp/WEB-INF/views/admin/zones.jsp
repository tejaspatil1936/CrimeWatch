<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Zones</h1>
</section>

<div class="card" style="margin-bottom:var(--s-6)">
    <h3>Add new zone</h3>
    <form:form method="post" action="/admin/zones/add" modelAttribute="zone" cssClass="form-stack">
        <sec:csrfInput/>
        <div class="field-row">
            <div class="field">
                <form:label path="zoneName">Zone name</form:label>
                <form:input path="zoneName" required="true"/>
            </div>
            <div class="field">
                <form:label path="city">City</form:label>
                <form:input path="city" required="true"/>
            </div>
        </div>
        <div class="field-row">
            <div class="field">
                <form:label path="latCenter">Latitude</form:label>
                <form:input path="latCenter" type="number" step="0.0001" required="true"/>
            </div>
            <div class="field">
                <form:label path="lngCenter">Longitude</form:label>
                <form:input path="lngCenter" type="number" step="0.0001" required="true"/>
            </div>
            <div class="field">
                <form:label path="escalationThreshold">Threshold</form:label>
                <form:input path="escalationThreshold" type="number" min="1" max="100"/>
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Add zone</button>
    </form:form>
</div>

<table class="data-table">
    <thead>
        <tr><th>Zone</th><th>City</th><th>Lat</th><th>Lng</th><th>Threshold</th><th>Action</th></tr>
    </thead>
    <tbody>
        <c:forEach var="z" items="${zones}">
            <tr>
                <td><c:out value="${z.zoneName}"/></td>
                <td><c:out value="${z.city}"/></td>
                <td><c:out value="${z.latCenter}"/></td>
                <td><c:out value="${z.lngCenter}"/></td>
                <td>
                    <form method="post" action="<c:url value='/admin/zones/${z.zoneId}/threshold'/>" class="threshold-form">
                        <sec:csrfInput/>
                        <input type="number" name="threshold" value="${z.escalationThreshold}"
                               min="1" max="100" class="threshold-input"
                               title="Press Enter to save"/>
                        <button type="submit" class="btn btn-sm btn-primary">Save</button>
                    </form>
                </td>
                <td>
                    <form method="post" action="<c:url value='/admin/zones/${z.zoneId}/delete'/>">
                        <sec:csrfInput/>
                        <button class="btn btn-sm" style="color:var(--status-error)">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<style>
.threshold-form { display: flex; align-items: center; gap: var(--s-2); }
.threshold-input {
    width: 70px;
    font-family: var(--font-sans);
    font-size: var(--fs-small);
    padding: 5px 8px;
    border: 1px solid var(--rule-hair);
    border-radius: var(--radius-2);
    background: var(--bg-surface);
    color: var(--ink-primary);
    text-align: center;
}
.threshold-input:focus {
    outline: none;
    border-color: var(--amber-600);
    box-shadow: 0 0 0 3px var(--amber-100);
}
</style>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
