<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Услуга — ${service.name}" scope="request"/>
<%@ include file="header.jsp" %>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<h1>Услуга: ${service.name}</h1>

<div class="card mb-4">
    <div class="card-body">
        <dl class="row mb-0">
            <dt class="col-sm-3">ID</dt>
            <dd class="col-sm-9">${service.id}</dd>
            <dt class="col-sm-3">Название</dt>
            <dd class="col-sm-9">${service.name}</dd>
            <dt class="col-sm-3">Базовая стоимость</dt>
            <dd class="col-sm-9">${service.baseCost}</dd>
        </dl>
    </div>
</div>

<div class="mb-3">
    <a href="${pageContext.request.contextPath}/services/${service.id}/edit" class="btn btn-primary">Редактировать</a>
    <form method="post" action="${pageContext.request.contextPath}/services/${service.id}/delete"
          class="d-inline">
        <button type="submit" class="btn btn-danger">Удалить</button>
    </form>
</div>

<%@ include file="footer.jsp" %>
