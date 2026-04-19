<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Клиент — ${client.displayName}" scope="request"/>
<%@ include file="header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<h1>Клиент: ${client.displayName}</h1>

<div class="card mb-4">
    <div class="card-body">
        <dl class="row mb-0">
            <dt class="col-sm-3">ID</dt>
            <dd class="col-sm-9">${client.id}</dd>
            <dt class="col-sm-3">Тип</dt>
            <dd class="col-sm-9">${client.clientType == 'INDIVIDUAL' ? 'Физ. лицо' : 'Организация'}</dd>
            <dt class="col-sm-3">Наименование</dt>
            <dd class="col-sm-9">${client.displayName}</dd>
            <dt class="col-sm-3">Дата создания</dt>
            <dd class="col-sm-9">${client.createdAt}</dd>
            <dt class="col-sm-3">Заметка</dt>
            <dd class="col-sm-9">${empty client.note ? '—' : client.note}</dd>
        </dl>
    </div>
</div>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/clients/${client.id}/edit" class="btn btn-primary">Редактировать</a>
    <form method="post" action="${pageContext.request.contextPath}/clients/${client.id}/delete"
          class="d-inline">
        <button type="submit" class="btn btn-danger">Удалить</button>
    </form>
    <a href="${pageContext.request.contextPath}/contracts/new?clientId=${client.id}" class="btn btn-outline-success">
        Зарегистрировать договор
    </a>
</div>

<h3>Контактные лица</h3>
<c:choose>
    <c:when test="${empty client.contacts}">
        <p class="text-muted">Нет контактных лиц</p>
    </c:when>
    <c:otherwise>
        <c:forEach var="contact" items="${client.contacts}">
            <div class="card mb-2">
                <div class="card-body">
                    <h5 class="card-title">${contact.fullName}</h5>
                    <c:if test="${not empty contact.role}">
                        <p class="mb-1"><strong>Роль:</strong> ${contact.role}</p>
                    </c:if>
                    <c:if test="${not empty contact.comment}">
                        <p class="mb-1"><strong>Комментарий:</strong> ${contact.comment}</p>
                    </c:if>
                    <c:if test="${not empty contact.methods}">
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="method" items="${contact.methods}">
                                <li>
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
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<h3 class="mt-4">История услуг (договоры)</h3>
<c:choose>
    <c:when test="${empty client.contracts}">
        <p class="text-muted">Нет договоров</p>
    </c:when>
    <c:otherwise>
        <table class="table table-sm table-striped">
            <thead>
                <tr>
                    <th>№ договора</th>
                    <th>Услуга</th>
                    <th>Период</th>
                    <th>Статус</th>
                    <th>Стоимость</th>
                    <th>Служащие</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="sc" items="${client.contracts}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/contracts/${sc.id}">${sc.contractNumber}</a></td>
                        <td><a href="${pageContext.request.contextPath}/services/${sc.service.id}">${sc.service.name}</a></td>
                        <td>${sc.serviceStart} — ${empty sc.serviceEnd ? 'н.в.' : sc.serviceEnd}</td>
                        <td>
                            <c:choose>
                                <c:when test="${sc.status == 'DRAFT'}">Черновик</c:when>
                                <c:when test="${sc.status == 'ACTIVE'}">Активен</c:when>
                                <c:when test="${sc.status == 'COMPLETED'}">Завершён</c:when>
                                <c:when test="${sc.status == 'CANCELLED'}">Отменён</c:when>
                            </c:choose>
                        </td>
                        <td>${sc.agreedCost}</td>
                        <td>
                            <c:forEach var="ce" items="${sc.employees}" varStatus="st">
                                <a href="${pageContext.request.contextPath}/employees/${ce.employee.id}">${ce.employee.fullName}</a>
                                <c:if test="${not empty ce.role}"> (${ce.role})</c:if>
                                <c:if test="${!st.last}">, </c:if>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jsp" %>
