<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Officer view</p>
    <h1>Pending reports</h1>
</section>

<table class="data-table">
    <thead>
        <tr><th>Title</th><th>Type</th><th>Severity</th><th>Zone</th><th>Time</th><th>Action</th></tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty pending}">
                <tr><td colspan="6" class="empty">No pending reports.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="r" items="${pending}">
                    <tr>
                        <td><c:out value="${r.title}"/></td>
                        <td><c:out value="${r.crimeType}"/></td>
                        <td><span class="sev sev-${r.severity}"><c:out value="${r.severity}"/></span></td>
                        <td><c:out value="${r.zoneId}"/></td>
                        <td><fmt:formatDate value="${r.reportedAtDate}" pattern="dd MMM HH:mm"/></td>
                        <td>
                            <form method="post" action="<c:url value='/dashboard/reports/${r.reportId}/assign'/>">
                                <sec:csrfInput/>
                                <button class="btn btn-sm">Assign</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
