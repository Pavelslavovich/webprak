<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${isNew ? 'Новый клиент' : 'Редактирование клиента'}" scope="request"/>
<%@ include file="header.jsp" %>

<h1>${isNew ? 'Новый клиент' : 'Редактирование клиента'}</h1>

<form method="post"
      action="${pageContext.request.contextPath}/clients${isNew ? '' : '/'.concat(client.id)}">

    <div class="card mb-3">
        <div class="card-body">
            <div class="mb-3">
                <label class="form-label">Тип клиента</label>
                <select name="clientType" class="form-select" required>
                    <option value="INDIVIDUAL" ${not isNew && client.clientType == 'INDIVIDUAL' ? 'selected' : ''}>
                        Физ. лицо</option>
                    <option value="ORGANIZATION" ${not isNew && client.clientType == 'ORGANIZATION' ? 'selected' : ''}>
                        Организация</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Наименование / ФИО</label>
                <input type="text" name="displayName" class="form-control" required
                       value="${not isNew ? client.displayName : ''}"/>
            </div>
            <div class="mb-3">
                <label class="form-label">Заметка</label>
                <textarea name="note" class="form-control" rows="3">${not isNew ? client.note : ''}</textarea>
            </div>
        </div>
    </div>

    <h3>Контактные лица</h3>
    <div id="contacts">
        <c:if test="${not isNew}">
            <c:forEach var="contact" items="${client.contacts}" varStatus="cs">
                <div class="card mb-3 contact-block" data-index="${cs.index}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between">
                            <h5>Контактное лицо #<span class="contact-num">${cs.index + 1}</span></h5>
                            <button type="button" class="btn btn-sm btn-outline-danger"
                                    onclick="this.closest('.contact-block').remove()">Удалить</button>
                        </div>
                        <div class="mb-2">
                            <label class="form-label">ФИО</label>
                            <input name="contact_name_${cs.index}" class="form-control" required
                                   value="${contact.fullName}"/>
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Роль</label>
                            <input name="contact_role_${cs.index}" class="form-control"
                                   value="${contact.role}"/>
                        </div>
                        <div class="mb-2">
                            <label class="form-label">Комментарий</label>
                            <input name="contact_comment_${cs.index}" class="form-control"
                                   value="${contact.comment}"/>
                        </div>
                        <h6>Контакты</h6>
                        <div class="methods-container">
                            <c:forEach var="method" items="${contact.methods}" varStatus="ms">
                                <div class="row mb-2 method-row">
                                    <div class="col-3">
                                        <select name="contact_${cs.index}_mtype_${ms.index}" class="form-select">
                                            <option value="PHONE" ${method.methodType == 'PHONE' ? 'selected' : ''}>Телефон</option>
                                            <option value="EMAIL" ${method.methodType == 'EMAIL' ? 'selected' : ''}>Email</option>
                                            <option value="ADDRESS" ${method.methodType == 'ADDRESS' ? 'selected' : ''}>Адрес</option>
                                        </select>
                                    </div>
                                    <div class="col-5">
                                        <input name="contact_${cs.index}_mvalue_${ms.index}" class="form-control"
                                               value="${method.value}"/>
                                    </div>
                                    <div class="col-2">
                                        <div class="form-check mt-2">
                                            <input type="checkbox" name="contact_${cs.index}_mprimary_${ms.index}"
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
                        </div>
                        <input type="hidden" name="contact_${cs.index}_methodCount"
                               class="method-count" value="${contact.methods.size()}"/>
                        <button type="button" class="btn btn-sm btn-outline-secondary"
                                onclick="addMethod(this.closest('.contact-block'))">Добавить контакт</button>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
    <input type="hidden" name="contactCount" id="contactCount"
           value="${not isNew ? client.contacts.size() : 0}"/>
    <button type="button" class="btn btn-secondary mb-3" onclick="addContactBlock()">Добавить контактное лицо</button>

    <div>
        <button type="submit" class="btn btn-primary">Сохранить</button>
        <a href="${pageContext.request.contextPath}/clients${not isNew ? '/'.concat(client.id) : ''}"
           class="btn btn-outline-secondary">Отмена</a>
    </div>
</form>

<script>
let contactCounter = parseInt(document.getElementById('contactCount').value) || 0;

function addContactBlock() {
    const idx = contactCounter++;
    document.getElementById('contactCount').value = contactCounter;
    const div = document.createElement('div');
    div.className = 'card mb-3 contact-block';
    div.dataset.index = idx;
    div.innerHTML = `
        <div class="card-body">
            <div class="d-flex justify-content-between">
                <h5>Контактное лицо #\${idx + 1}</h5>
                <button type="button" class="btn btn-sm btn-outline-danger"
                        onclick="this.closest('.contact-block').remove()">Удалить</button>
            </div>
            <div class="mb-2">
                <label class="form-label">ФИО</label>
                <input name="contact_name_\${idx}" class="form-control" required/>
            </div>
            <div class="mb-2">
                <label class="form-label">Роль</label>
                <input name="contact_role_\${idx}" class="form-control"/>
            </div>
            <div class="mb-2">
                <label class="form-label">Комментарий</label>
                <input name="contact_comment_\${idx}" class="form-control"/>
            </div>
            <h6>Контакты</h6>
            <div class="methods-container"></div>
            <input type="hidden" name="contact_\${idx}_methodCount" class="method-count" value="0"/>
            <button type="button" class="btn btn-sm btn-outline-secondary"
                    onclick="addMethod(this.closest('.contact-block'))">Добавить контакт</button>
        </div>`;
    document.getElementById('contacts').appendChild(div);
}

function addMethod(contactBlock) {
    const idx = contactBlock.dataset.index;
    const mcInput = contactBlock.querySelector('.method-count');
    let mc = parseInt(mcInput.value) || 0;
    const container = contactBlock.querySelector('.methods-container');
    const row = document.createElement('div');
    row.className = 'row mb-2 method-row';
    row.innerHTML = `
        <div class="col-3">
            <select name="contact_\${idx}_mtype_\${mc}" class="form-select">
                <option value="PHONE">Телефон</option>
                <option value="EMAIL">Email</option>
                <option value="ADDRESS">Адрес</option>
            </select>
        </div>
        <div class="col-5">
            <input name="contact_\${idx}_mvalue_\${mc}" class="form-control"/>
        </div>
        <div class="col-2">
            <div class="form-check mt-2">
                <input type="checkbox" name="contact_\${idx}_mprimary_\${mc}" class="form-check-input"/>
                <label class="form-check-label">Осн.</label>
            </div>
        </div>
        <div class="col-2">
            <button type="button" class="btn btn-sm btn-outline-danger"
                    onclick="this.closest('.method-row').remove()">×</button>
        </div>`;
    container.appendChild(row);
    mcInput.value = mc + 1;
}
</script>

<%@ include file="footer.jsp" %>
