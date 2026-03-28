<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Audit log</h1>
</section>

<table class="data-table">
    <thead>
        <tr>
            <th>Time</th>
            <th>User</th>
            <th>Action</th>
            <th>Entity</th>
            <th>IP</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty logs}">
                <tr><td colspan="5" class="empty">No audit entries yet.</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="l" items="${logs}">
                    <tr>
                        <td><fmt:formatDate value="${l.timestampDate}" pattern="dd MMM yyyy HH:mm:ss"/></td>
                        <td><c:out value="${l.userId != null ? l.userId : 'anonymous'}"/></td>
                        <td><c:out value="${l.action}"/></td>
                        <td><c:out value="${l.entityType}"/> &middot; <c:out value="${l.entityId}"/></td>
                        <td><c:out value="${l.ipAddress}"/></td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
