<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Клиенты" scope="request"/>
<%@ include file="header.jsp" %>

<h1>Клиенты</h1>

<div class="card mb-4">
    <div class="card-body">
        <h5 class="card-title">Фильтры</h5>
        <form method="get" action="${pageContext.request.contextPath}/clients">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Наименование / ФИО</label>
                    <input type="text" name="nameQuery" class="form-control" id="nameQuery_inp"
                           value="${param.nameQuery}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Тип клиента</label>
                    <select name="type" class="form-select" id="type_inp">
                        <option value="">Все</option>
                        <option value="INDIVIDUAL" ${param.type == 'INDIVIDUAL' ? 'selected' : ''}>Физ. лицо</option>
                        <option value="ORGANIZATION" ${param.type == 'ORGANIZATION' ? 'selected' : ''}>Организация</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Услуга (id)</label>
                    <input type="number" name="serviceId" class="form-control" id="serviceId_inp"
                           value="${param.serviceId}"/>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Служащий (id)</label>
                    <input type="number" name="employeeId" class="form-control" id="employeeId_inp"
                           value="${param.employeeId}"/>
                </div>
            </div>
            <div class="row g-3 mt-1">
                <div class="col-md-3">
                    <label class="form-label">Период от</label>
                    <input type="date" name="fromDate" class="form-control" id="fromDate_inp"
                           value="${param.fromDate}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Период до</label>
                    <input type="date" name="toDate" class="form-control" id="toDate_inp"
                           value="${param.toDate}"/>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary">Фильтровать</button>
                    <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary ms-2">Сбросить</a>
                </div>
            </div>
        </form>
    </div>
</div>

<a href="${pageContext.request.contextPath}/clients/new" class="btn btn-success mb-3">Добавить клиента</a>

<table class="table table-striped table-hover">
    <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Тип</th>
            <th>Наименование / ФИО</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="client" items="${clients}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/clients/${client.id}">${client.id}</a></td>
                <td>${client.clientType == 'INDIVIDUAL' ? 'Физ. лицо' : 'Организация'}</td>
                <td>${client.displayName}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty clients}">
            <tr><td colspan="3" class="text-center text-muted">Нет данных</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
