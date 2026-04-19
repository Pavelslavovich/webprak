<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${isNew ? 'Новая услуга' : 'Редактирование услуги'}" scope="request"/>
<%@ include file="header.jsp" %>

<h1>${isNew ? 'Новая услуга' : 'Редактирование услуги'}</h1>

<form method="post"
      action="${pageContext.request.contextPath}/services${isNew ? '' : '/'.concat(service.id)}">
    <div class="card mb-3">
        <div class="card-body">
            <div class="mb-3">
                <label class="form-label">Название</label>
                <input type="text" name="name" class="form-control" required
                       value="${not isNew ? service.name : ''}" id="name_inp"/>
            </div>
            <div class="mb-3">
                <label class="form-label">Базовая стоимость</label>
                <input type="number" step="0.01" name="baseCost" class="form-control" required
                       value="${not isNew ? service.baseCost : ''}" id="baseCost_inp"/>
            </div>
        </div>
    </div>
    <button type="submit" class="btn btn-primary">Сохранить</button>
    <a href="${pageContext.request.contextPath}/services${not isNew ? '/'.concat(service.id) : ''}"
       class="btn btn-outline-secondary">Отмена</a>
</form>

<%@ include file="footer.jsp" %>
