<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${isNew ? 'Регистрация договора' : 'Редактирование договора'}" scope="request"/>
<%@ include file="header.jsp" %>

<h1>${isNew ? 'Регистрация договора' : 'Редактирование договора'}</h1>

<form method="post"
      action="${pageContext.request.contextPath}/contracts${isNew ? '' : '/'.concat(contract.id)}">

    <div class="card mb-3">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Номер договора</label>
                    <input type="text" name="contractNumber" class="form-control" required
                           value="${not isNew ? contract.contractNumber : ''}" id="contractNumber_inp"/>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Клиент</label>
                    <select name="clientId" class="form-select" required id="clientId_sel">
                        <option value="">-- Выберите --</option>
                        <c:forEach var="c" items="${clients}">
                            <option value="${c.id}"
                                ${not isNew && contract.client.id == c.id ? 'selected' : ''}
                                ${param.clientId == c.id ? 'selected' : ''}>
                                [${c.id}] ${c.displayName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Услуга</label>
                    <select name="serviceId" class="form-select" required id="serviceId_sel">
                        <option value="">-- Выберите --</option>
                        <c:forEach var="s" items="${services}">
                            <option value="${s.id}" data-cost="${s.baseCost}"
                                ${not isNew && contract.service.id == s.id ? 'selected' : ''}>
                                [${s.id}] ${s.name} (${s.baseCost})
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="row g-3 mt-1">
                <div class="col-md-3">
                    <label class="form-label">Дата подписания</label>
                    <input type="date" name="signedOn" class="form-control" required
                           value="${not isNew ? contract.signedOn : ''}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Начало оказания</label>
                    <input type="date" name="serviceStart" class="form-control" required
                           value="${not isNew ? contract.serviceStart : ''}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Окончание оказания</label>
                    <input type="date" name="serviceEnd" class="form-control"
                           value="${not isNew ? contract.serviceEnd : ''}"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Статус</label>
                    <select name="status" class="form-select" required>
                        <c:forEach var="st" items="${statuses}">
                            <option value="${st}" ${not isNew && contract.status == st ? 'selected' : ''}>
                                <c:choose>
                                    <c:when test="${st == 'DRAFT'}">Черновик</c:when>
                                    <c:when test="${st == 'ACTIVE'}">Активен</c:when>
                                    <c:when test="${st == 'COMPLETED'}">Завершён</c:when>
                                    <c:when test="${st == 'CANCELLED'}">Отменён</c:when>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="row g-3 mt-1">
                <div class="col-md-4">
                    <label class="form-label">Стоимость по договору</label>
                    <input type="number" step="0.01" name="agreedCost" class="form-control" required
                           value="${not isNew ? contract.agreedCost : ''}" id="agreedCost_inp"/>
                </div>
                <div class="col-md-8">
                    <label class="form-label">Комментарий</label>
                    <input type="text" name="comment" class="form-control"
                           value="${not isNew ? contract.comment : ''}"/>
                </div>
            </div>
        </div>
    </div>

    <h3>Участники (служащие)</h3>
    <div id="empRows">
        <c:if test="${not isNew}">
            <c:forEach var="ce" items="${contract.employees}" varStatus="es">
                <div class="row mb-2 emp-row">
                    <div class="col-6">
                        <select name="employee_id_${es.index}" class="form-select">
                            <c:forEach var="emp" items="${employees}">
                                <option value="${emp.id}" ${ce.employee.id == emp.id ? 'selected' : ''}>
                                    [${emp.id}] ${emp.fullName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-4">
                        <input name="employee_role_${es.index}" class="form-control"
                               placeholder="Роль" value="${ce.role}"/>
                    </div>
                    <div class="col-2">
                        <button type="button" class="btn btn-sm btn-outline-danger"
                                onclick="this.closest('.emp-row').remove()">×</button>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
    <input type="hidden" name="employeeCount" id="employeeCount"
           value="${not isNew ? contract.employees.size() : 0}"/>
    <button type="button" class="btn btn-secondary mb-3" onclick="addEmployeeRow()">Добавить служащего</button>

    <div>
        <button type="submit" class="btn btn-primary">Сохранить</button>
        <a href="${pageContext.request.contextPath}/contracts${not isNew ? '/'.concat(contract.id) : ''}"
           class="btn btn-outline-secondary">Отмена</a>
    </div>
</form>

<script>
let empCounter = parseInt(document.getElementById('employeeCount').value) || 0;
const employeeOptions = `<c:forEach var="emp" items="${employees}"><option value="${emp.id}">[${emp.id}] ${emp.fullName}</option></c:forEach>`;

function addEmployeeRow() {
    const idx = empCounter++;
    document.getElementById('employeeCount').value = empCounter;
    const container = document.getElementById('empRows');
    const row = document.createElement('div');
    row.className = 'row mb-2 emp-row';
    row.innerHTML = `
        <div class="col-6">
            <select name="employee_id_\${idx}" class="form-select">` + employeeOptions + `</select>
        </div>
        <div class="col-4">
            <input name="employee_role_\${idx}" class="form-control" placeholder="Роль"/>
        </div>
        <div class="col-2">
            <button type="button" class="btn btn-sm btn-outline-danger"
                    onclick="this.closest('.emp-row').remove()">×</button>
        </div>`;
    container.appendChild(row);
}

document.getElementById('serviceId_sel').addEventListener('change', function() {
    const selected = this.options[this.selectedIndex];
    const cost = selected.getAttribute('data-cost');
    if (cost) document.getElementById('agreedCost_inp').value = cost;
});
</script>

<%@ include file="footer.jsp" %>
