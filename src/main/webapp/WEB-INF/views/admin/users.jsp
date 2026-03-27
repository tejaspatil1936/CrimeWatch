<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Users</h1>
</section>

<table class="data-table">
    <thead>
        <tr><th>Username</th><th>Email</th><th>Role</th><th>Enabled</th><th>Change role</th></tr>
    </thead>
    <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><c:out value="${u.username}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td><c:out value="${u.role}"/></td>
                <td><c:out value="${u.enabled}"/></td>
                <td>
                    <form method="post" action="<c:url value='/admin/users/${u.userId}/role'/>" style="display:flex;gap:var(--s-2)">
                        <sec:csrfInput/>
                        <select name="role" style="padding:4px 8px;font-size:var(--fs-xs)">
                            <option value="CITIZEN">CITIZEN</option>
                            <option value="OFFICER">OFFICER</option>
                            <option value="ADMIN">ADMIN</option>
                        </select>
                        <button class="btn btn-sm">Update</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
