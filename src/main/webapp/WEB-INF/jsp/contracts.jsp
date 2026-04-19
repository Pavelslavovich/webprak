<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Договоры" scope="request"/>
<%@ include file="header.jsp" %>

<h1>Договоры</h1>

<div class="card mb-4">
    <div class="card-body">
        <h5 class="card-title">Фильтры</h5>
        <form method="get" action="${pageContext.request.contextPath}/contracts">
            <div class="row g-3">
                <div class="col-md-2">
                    <label class="form-label">Клиент (id)</label>
                    <input type="number" name="clientId" class="form-control" id="clientId_inp"
                           value="${param.clientId}"/>
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
                <div class="col-md-3">
                    <label class="form-label">Статус</label>
                    <select name="status" class="form-select" id="status_inp">
                        <option value="">Все</option>
                        <option value="DRAFT" ${param.status == 'DRAFT' ? 'selected' : ''}>Черновик</option>
                        <option value="ACTIVE" ${param.status == 'ACTIVE' ? 'selected' : ''}>Активен</option>
                        <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Завершён</option>
                        <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Отменён</option>
                    </select>
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
                    <a href="${pageContext.request.contextPath}/contracts" class="btn btn-outline-secondary ms-2">Сбросить</a>
                </div>
            </div>
        </form>
    </div>
</div>

<a href="${pageContext.request.contextPath}/contracts/new" class="btn btn-success mb-3">Зарегистрировать договор</a>

<table class="table table-striped table-hover">
    <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>№ договора</th>
            <th>Клиент</th>
            <th>Услуга</th>
            <th>Период</th>
            <th>Статус</th>
            <th>Стоимость</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="sc" items="${contracts}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/contracts/${sc.id}">${sc.id}</a></td>
                <td>${sc.contractNumber}</td>
                <td><a href="${pageContext.request.contextPath}/clients/${sc.client.id}">${sc.client.displayName}</a></td>
                <td><a href="${pageContext.request.contextPath}/services/${sc.service.id}">${sc.service.name}</a></td>
                <td>${sc.serviceStart} — ${empty sc.serviceEnd ? 'н.в.' : sc.serviceEnd}</td>
                <td>
                    <c:choose>
                        <c:when test="${sc.status == 'DRAFT'}"><span class="badge bg-secondary">Черновик</span></c:when>
                        <c:when test="${sc.status == 'ACTIVE'}"><span class="badge bg-success">Активен</span></c:when>
                        <c:when test="${sc.status == 'COMPLETED'}"><span class="badge bg-primary">Завершён</span></c:when>
                        <c:when test="${sc.status == 'CANCELLED'}"><span class="badge bg-danger">Отменён</span></c:when>
                    </c:choose>
                </td>
                <td>${sc.agreedCost}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty contracts}">
            <tr><td colspan="7" class="text-center text-muted">Нет данных</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
