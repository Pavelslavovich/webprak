<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Договор — ${contract.contractNumber}" scope="request"/>
<%@ include file="header.jsp" %>

<h1>Договор ${contract.contractNumber}</h1>

<div class="card mb-4">
    <div class="card-body">
        <dl class="row mb-0">
            <dt class="col-sm-3">ID</dt>
            <dd class="col-sm-9">${contract.id}</dd>
            <dt class="col-sm-3">Номер договора</dt>
            <dd class="col-sm-9">${contract.contractNumber}</dd>
            <dt class="col-sm-3">Клиент</dt>
            <dd class="col-sm-9">
                <a href="${pageContext.request.contextPath}/clients/${contract.client.id}">${contract.client.displayName}</a>
            </dd>
            <dt class="col-sm-3">Услуга</dt>
            <dd class="col-sm-9">
                <a href="${pageContext.request.contextPath}/services/${contract.service.id}">${contract.service.name}</a>
            </dd>
            <dt class="col-sm-3">Дата подписания</dt>
            <dd class="col-sm-9">${contract.signedOn}</dd>
            <dt class="col-sm-3">Начало оказания</dt>
            <dd class="col-sm-9">${contract.serviceStart}</dd>
            <dt class="col-sm-3">Окончание оказания</dt>
            <dd class="col-sm-9">${empty contract.serviceEnd ? 'Не указано (текущий)' : contract.serviceEnd}</dd>
            <dt class="col-sm-3">Статус</dt>
            <dd class="col-sm-9">
                <c:choose>
                    <c:when test="${contract.status == 'DRAFT'}"><span class="badge bg-secondary">Черновик</span></c:when>
                    <c:when test="${contract.status == 'ACTIVE'}"><span class="badge bg-success">Активен</span></c:when>
                    <c:when test="${contract.status == 'COMPLETED'}"><span class="badge bg-primary">Завершён</span></c:when>
                    <c:when test="${contract.status == 'CANCELLED'}"><span class="badge bg-danger">Отменён</span></c:when>
                </c:choose>
            </dd>
            <dt class="col-sm-3">Стоимость</dt>
            <dd class="col-sm-9">${contract.agreedCost}</dd>
            <dt class="col-sm-3">Комментарий</dt>
            <dd class="col-sm-9">${empty contract.comment ? '—' : contract.comment}</dd>
        </dl>
    </div>
</div>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/contracts/${contract.id}/edit" class="btn btn-primary">Редактировать</a>
    <form method="post" action="${pageContext.request.contextPath}/contracts/${contract.id}/delete"
          class="d-inline">
        <button type="submit" class="btn btn-danger">Удалить</button>
    </form>
</div>

<h3>Задействованные служащие</h3>
<c:choose>
    <c:when test="${empty contract.employees}">
        <p class="text-muted">Нет задействованных служащих</p>
    </c:when>
    <c:otherwise>
        <table class="table table-sm table-striped" style="max-width: 600px;">
            <thead>
                <tr>
                    <th>Служащий</th>
                    <th>Роль</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ce" items="${contract.employees}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/employees/${ce.employee.id}">${ce.employee.fullName}</a>
                        </td>
                        <td>${empty ce.role ? '—' : ce.role}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jsp" %>
