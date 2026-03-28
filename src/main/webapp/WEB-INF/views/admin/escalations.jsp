<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Escalations</h1>
</section>

<table class="data-table">
    <thead>
        <tr><th>Zone</th><th>Level</th><th>Reports</th><th>Triggered</th><th>Resolved</th></tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty escalations}">
                <tr><td colspan="5" class="empty">No escalations recorded.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="e" items="${escalations}">
                    <tr>
                        <td><c:out value="${e.zoneId}"/></td>
                        <td><span class="sev sev-${e.escalationLevel}"><c:out value="${e.escalationLevel}"/></span></td>
                        <td><c:out value="${e.reportCount}"/></td>
                        <td><c:out value="${e.triggeredAt}"/></td>
                        <td><c:out value="${e.resolved}"/></td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
