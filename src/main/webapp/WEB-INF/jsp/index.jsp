<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Главная" scope="request"/>
<%@ include file="header.jsp" %>

<h1>Главная</h1>
<p>Система управления клиентской базой юридической фирмы.</p>
<div class="list-group" style="max-width: 400px;">
    <a href="${pageContext.request.contextPath}/clients" class="list-group-item list-group-item-action">Клиенты</a>
    <a href="${pageContext.request.contextPath}/employees" class="list-group-item list-group-item-action">Служащие</a>
    <a href="${pageContext.request.contextPath}/services" class="list-group-item list-group-item-action">Услуги</a>
    <a href="${pageContext.request.contextPath}/contracts" class="list-group-item list-group-item-action">Договоры</a>
</div>

<%@ include file="footer.jsp" %>
