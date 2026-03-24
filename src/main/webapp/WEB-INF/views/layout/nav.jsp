<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<nav class="site-nav">
    <div class="nav-inner">
        <a class="brand" href="<c:url value='/'/>">
            <span class="brand-mark"></span>
            <span class="brand-text">CrimeWatch</span>
        </a>
        <ul class="nav-links">
            <li><a href="<c:url value='/map'/>">Heatmap</a></li>
            <li><a href="<c:url value='/report/new'/>">Report</a></li>
            <sec:authorize access="hasRole('OFFICER')">
                <li><a href="<c:url value='/dashboard'/>">Dashboard</a></li>
            </sec:authorize>
            <sec:authorize access="hasRole('ADMIN')">
                <li><a href="<c:url value='/admin'/>">Admin</a></li>
            </sec:authorize>
        </ul>
        <div class="nav-actions">
            <sec:authorize access="!isAuthenticated()">
                <a class="btn btn-ghost" href="<c:url value='/login'/>">Sign in</a>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <span class="nav-user"><sec:authentication property="name"/></span>
                <form action="<c:url value='/logout'/>" method="post" class="inline-form">
                    <sec:csrfInput/>
                    <button class="btn btn-ghost" type="submit">Sign out</button>
                </form>
            </sec:authorize>
        </div>
    </div>
</nav>
