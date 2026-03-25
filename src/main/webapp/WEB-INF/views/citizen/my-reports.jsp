<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">My activity</p>
    <h1>My reports</h1>
</section>

<table class="data-table">
    <thead>
        <tr><th>Title</th><th>Type</th><th>Severity</th><th>Status</th><th>Submitted</th></tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty reports}">
                <tr><td colspan="5" class="empty">You have not submitted any reports yet.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="r" items="${reports}">
                    <tr>
                        <td><c:out value="${r.title}"/></td>
                        <td><c:out value="${r.crimeType}"/></td>
                        <td><span class="sev sev-${r.severity}"><c:out value="${r.severity}"/></span></td>
                        <td><c:out value="${r.status}"/></td>
                        <td><fmt:formatDate value="${r.reportedAtDate}" pattern="dd MMM yyyy HH:mm"/></td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
