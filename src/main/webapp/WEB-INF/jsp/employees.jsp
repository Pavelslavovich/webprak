<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Служащие" scope="request"/>
<%@ include file="header.jsp" %>

<h1>Служащие</h1>

<div class="card mb-4">
    <div class="card-body">
        <h5 class="card-title">Фильтры</h5>
        <form method="get" action="${pageContext.request.contextPath}/employees">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Поиск (ФИО / должность / образование)</label>
                    <input type="text" name="query" class="form-control" id="query_inp"
                           value="${param.query}"/>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary">Фильтровать</button>
                    <a href="${pageContext.request.contextPath}/employees" class="btn btn-outline-secondary ms-2">Сбросить</a>
                </div>
            </div>
        </form>
    </div>
</div>

<a href="${pageContext.request.contextPath}/employees/new" class="btn btn-success mb-3">Добавить служащего</a>

<table class="table table-striped table-hover">
    <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>ФИО</th>
            <th>Должность</th>
            <th>Образование</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="emp" items="${employees}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/employees/${emp.id}">${emp.id}</a></td>
                <td>${emp.fullName}</td>
                <td>${emp.position}</td>
                <td>${empty emp.education ? '—' : emp.education}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty employees}">
            <tr><td colspan="4" class="text-center text-muted">Нет данных</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
