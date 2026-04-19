<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Услуги" scope="request"/>
<%@ include file="header.jsp" %>

<h1>Услуги</h1>

<div class="card mb-4">
    <div class="card-body">
        <h5 class="card-title">Фильтры</h5>
        <form method="get" action="${pageContext.request.contextPath}/services">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Название</label>
                    <input type="text" name="nameQuery" class="form-control" id="nameQuery_inp"
                           value="${param.nameQuery}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Мин. стоимость</label>
                    <input type="number" step="0.01" name="minCost" class="form-control" id="minCost_inp"
                           value="${param.minCost}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Макс. стоимость</label>
                    <input type="number" step="0.01" name="maxCost" class="form-control" id="maxCost_inp"
                           value="${param.maxCost}"/>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary">Фильтровать</button>
                </div>
            </div>
        </form>
    </div>
</div>

<a href="${pageContext.request.contextPath}/services/new" class="btn btn-success mb-3">Добавить услугу</a>

<table class="table table-striped table-hover">
    <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Название</th>
            <th>Базовая стоимость</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="service" items="${services}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/services/${service.id}">${service.id}</a></td>
                <td>${service.name}</td>
                <td>${service.baseCost}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty services}">
            <tr><td colspan="3" class="text-center text-muted">Нет данных</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="footer.jsp" %>
