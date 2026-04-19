<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Служащий — ${employee.fullName}" scope="request"/>
<%@ include file="header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<h1>Служащий: ${employee.fullName}</h1>

<div class="card mb-4">
    <div class="card-body">
        <dl class="row mb-0">
            <dt class="col-sm-3">ID</dt>
            <dd class="col-sm-9">${employee.id}</dd>
            <dt class="col-sm-3">ФИО</dt>
            <dd class="col-sm-9">${employee.fullName}</dd>
            <dt class="col-sm-3">Должность</dt>
            <dd class="col-sm-9">${employee.position}</dd>
            <dt class="col-sm-3">Образование</dt>
            <dd class="col-sm-9">${empty employee.education ? '—' : employee.education}</dd>
            <dt class="col-sm-3">Домашний адрес</dt>
            <dd class="col-sm-9">${empty employee.homeAddress ? '—' : employee.homeAddress}</dd>
            <dt class="col-sm-3">Примечание</dt>
            <dd class="col-sm-9">${empty employee.note ? '—' : employee.note}</dd>
        </dl>
    </div>
</div>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/employees/${employee.id}/edit" class="btn btn-primary">Редактировать</a>
    <form method="post" action="${pageContext.request.contextPath}/employees/${employee.id}/delete"
          class="d-inline">
        <button type="submit" class="btn btn-danger">Удалить</button>
    </form>
</div>

<h3>Контактная информация</h3>
<c:choose>
    <c:when test="${empty employee.contactMethods}">
        <p class="text-muted">Нет контактных данных</p>
    </c:when>
    <c:otherwise>
        <ul class="list-group mb-4" style="max-width: 500px;">
            <c:forEach var="method" items="${employee.contactMethods}">
                <li class="list-group-item">
                    <c:choose>
                        <c:when test="${method.methodType == 'PHONE'}">Телефон</c:when>
                        <c:when test="${method.methodType == 'EMAIL'}">Email</c:when>
                        <c:otherwise>Адрес</c:otherwise>
                    </c:choose>:
                    ${method.value}
                    <c:if test="${method.primary}">
                        <span class="badge bg-primary">Основной</span>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<h3>Участие в договорах</h3>
<c:choose>
    <c:when test="${empty employee.contracts}">
        <p class="text-muted">Нет участия в договорах</p>
    </c:when>
    <c:otherwise>
        <table class="table table-sm table-striped">
            <thead>
                <tr>
                    <th>№ договора</th>
                    <th>Клиент</th>
                    <th>Услуга</th>
                    <th>Период</th>
                    <th>Роль</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ce" items="${employee.contracts}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/contracts/${ce.contract.id}">${ce.contract.contractNumber}</a></td>
                        <td><a href="${pageContext.request.contextPath}/clients/${ce.contract.client.id}">${ce.contract.client.displayName}</a></td>
                        <td><a href="${pageContext.request.contextPath}/services/${ce.contract.service.id}">${ce.contract.service.name}</a></td>
                        <td>${ce.contract.serviceStart} — ${empty ce.contract.serviceEnd ? 'н.в.' : ce.contract.serviceEnd}</td>
                        <td>${empty ce.role ? '—' : ce.role}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jsp" %>
