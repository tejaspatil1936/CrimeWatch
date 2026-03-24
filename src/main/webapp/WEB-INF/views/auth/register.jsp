<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<div class="auth-card">
    <h1>Create account</h1>
    <c:if test="${error != null}">
        <div class="auth-error"><c:out value="${error}"/></div>
    </c:if>
    <form method="post" action="<c:url value='/register'/>" class="form-stack">
        <sec:csrfInput/>
        <div class="field">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required minlength="3" maxlength="60">
        </div>
        <div class="field">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required minlength="8">
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Register</button>
            <a href="<c:url value='/login'/>" class="btn btn-ghost">Already have an account?</a>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
