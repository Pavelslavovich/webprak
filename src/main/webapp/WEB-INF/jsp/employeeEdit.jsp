<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${isNew ? 'Новый служащий' : 'Редактирование служащего'}" scope="request"/>
<%@ include file="header.jsp" %>

<h1>${isNew ? 'Новый служащий' : 'Редактирование служащего'}</h1>

<form method="post"
      action="${pageContext.request.contextPath}/employees${isNew ? '' : '/'.concat(employee.id)}">

    <div class="card mb-3">
        <div class="card-body">
            <div class="mb-3">
                <label class="form-label">ФИО</label>
                <input type="text" name="fullName" class="form-control" required
                       value="${not isNew ? employee.fullName : ''}"/>
            </div>
            <div class="mb-3">
                <label class="form-label">Должность</label>
                <input type="text" name="position" class="form-control" required
                       value="${not isNew ? employee.position : ''}"/>
            </div>
            <div class="mb-3">
                <label class="form-label">Образование</label>
                <input type="text" name="education" class="form-control"
                       value="${not isNew ? employee.education : ''}"/>
            </div>
            <div class="mb-3">
                <label class="form-label">Домашний адрес</label>
                <input type="text" name="homeAddress" class="form-control"
                       value="${not isNew ? employee.homeAddress : ''}"/>
            </div>
            <div class="mb-3">
                <label class="form-label">Примечание</label>
                <textarea name="note" class="form-control" rows="3">${not isNew ? employee.note : ''}</textarea>
            </div>
        </div>
    </div>

    <h3>Контакты</h3>
    <div id="methods">
        <c:if test="${not isNew}">
            <c:forEach var="method" items="${employee.contactMethods}" varStatus="ms">
                <div class="row mb-2 method-row">
                    <div class="col-3">
                        <select name="method_type_${ms.index}" class="form-select">
                            <option value="PHONE" ${method.methodType == 'PHONE' ? 'selected' : ''}>Телефон</option>
                            <option value="EMAIL" ${method.methodType == 'EMAIL' ? 'selected' : ''}>Email</option>
                            <option value="ADDRESS" ${method.methodType == 'ADDRESS' ? 'selected' : ''}>Адрес</option>
                        </select>
                    </div>
                    <div class="col-5">
                        <input name="method_value_${ms.index}" class="form-control" value="${method.value}"/>
                    </div>
                    <div class="col-2">
                        <div class="form-check mt-2">
                            <input type="checkbox" name="method_primary_${ms.index}"
                                   class="form-check-input" ${method.primary ? 'checked' : ''}/>
                            <label class="form-check-label">Осн.</label>
                        </div>
                    </div>
                    <div class="col-2">
                        <button type="button" class="btn btn-sm btn-outline-danger"
                                onclick="this.closest('.method-row').remove()">×</button>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
    <input type="hidden" name="methodCount" id="methodCount"
           value="${not isNew ? employee.contactMethods.size() : 0}"/>
    <button type="button" class="btn btn-secondary mb-3" onclick="addMethodRow()">Добавить контакт</button>

    <div>
        <button type="submit" class="btn btn-primary">Сохранить</button>
        <a href="${pageContext.request.contextPath}/employees${not isNew ? '/'.concat(employee.id) : ''}"
           class="btn btn-outline-secondary">Отмена</a>
    </div>
</form>

<script>
let methodCounter = parseInt(document.getElementById('methodCount').value) || 0;

function addMethodRow() {
    const idx = methodCounter++;
    document.getElementById('methodCount').value = methodCounter;
    const container = document.getElementById('methods');
    const row = document.createElement('div');
    row.className = 'row mb-2 method-row';
    row.innerHTML = `
        <div class="col-3">
            <select name="method_type_\${idx}" class="form-select">
                <option value="PHONE">Телефон</option>
                <option value="EMAIL">Email</option>
                <option value="ADDRESS">Адрес</option>
            </select>
        </div>
        <div class="col-5">
            <input name="method_value_\${idx}" class="form-control"/>
        </div>
        <div class="col-2">
            <div class="form-check mt-2">
                <input type="checkbox" name="method_primary_\${idx}" class="form-check-input"/>
                <label class="form-check-label">Осн.</label>
            </div>
        </div>
        <div class="col-2">
            <button type="button" class="btn btn-sm btn-outline-danger"
                    onclick="this.closest('.method-row').remove()">×</button>
        </div>`;
    container.appendChild(row);
}
</script>

<%@ include file="footer.jsp" %>
