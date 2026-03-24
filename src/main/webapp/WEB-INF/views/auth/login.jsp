<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<div class="auth-card">
    <h1>Sign in</h1>
    <c:if test="${param.error != null}">
        <div class="auth-error">Invalid username or password.</div>
    </c:if>
    <c:if test="${param.registered != null}">
        <div class="auth-success">Account created. Please sign in.</div>
    </c:if>
    <form method="post" action="<c:url value='/login'/>" class="form-stack">
        <sec:csrfInput/>
        <div class="field">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required autofocus>
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Sign in</button>
            <a href="<c:url value='/register'/>" class="btn btn-ghost">Create account</a>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
