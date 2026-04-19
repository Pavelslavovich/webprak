<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Юридическая фирма</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" id="clients_nav" href="${pageContext.request.contextPath}/clients">Клиенты</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="employees_nav" href="${pageContext.request.contextPath}/employees">Служащие</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="services_nav" href="${pageContext.request.contextPath}/services">Услуги</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="contracts_nav" href="${pageContext.request.contextPath}/contracts">Договоры</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container">
