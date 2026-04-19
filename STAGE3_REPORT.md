# Этап 3. Web-интерфейс, Spring MVC и системные тесты

## Что реализовано

В проекте реализован полноценный Web-интерфейс для работы с клиентской базой юридической фирмы на базе Spring MVC, JSP и Hibernate.

Выполнены следующие работы:

- разработаны классы-контроллеры Spring MVC для основных вариантов использования;
- созданы JSP-страницы для списка, карточки, создания и редактирования клиентов, служащих, услуг и договоров;
- добавлены конфигурационные файлы Spring-контекста и основного сервлета;
- расширен Ant-сценарий сборки задачами компиляции, сборки WAR, развертывания и запуска системных тестов;
- реализованы системные тесты Selenium для основных пользовательских сценариев;
- выполнена отладка PostgreSQL-совместимости запросов, enum-типов и end-to-end поведения приложения.

## Результаты по требованиям

### 1. Код классов-контроллеров

Реализованы контроллеры:

- [src/main/java/ru/Pavelslavovich/webprak/controller/HomeController.java](src/main/java/ru/Pavelslavovich/webprak/controller/HomeController.java)
- [src/main/java/ru/Pavelslavovich/webprak/controller/ClientController.java](src/main/java/ru/Pavelslavovich/webprak/controller/ClientController.java)
- [src/main/java/ru/Pavelslavovich/webprak/controller/EmployeeController.java](src/main/java/ru/Pavelslavovich/webprak/controller/EmployeeController.java)
- [src/main/java/ru/Pavelslavovich/webprak/controller/ServiceController.java](src/main/java/ru/Pavelslavovich/webprak/controller/ServiceController.java)
- [src/main/java/ru/Pavelslavovich/webprak/controller/ContractController.java](src/main/java/ru/Pavelslavovich/webprak/controller/ContractController.java)

Контроллеры обрабатывают:

- просмотр списков;
- фильтрацию и поиск;
- просмотр карточек;
- создание, редактирование и удаление сущностей;
- регистрацию договоров и назначение участников.

### 2. Код JSP-страниц

Созданы JSP-представления:

- [src/main/webapp/WEB-INF/jsp/index.jsp](src/main/webapp/WEB-INF/jsp/index.jsp)
- [src/main/webapp/WEB-INF/jsp/clients.jsp](src/main/webapp/WEB-INF/jsp/clients.jsp)
- [src/main/webapp/WEB-INF/jsp/client.jsp](src/main/webapp/WEB-INF/jsp/client.jsp)
- [src/main/webapp/WEB-INF/jsp/clientEdit.jsp](src/main/webapp/WEB-INF/jsp/clientEdit.jsp)
- [src/main/webapp/WEB-INF/jsp/employees.jsp](src/main/webapp/WEB-INF/jsp/employees.jsp)
- [src/main/webapp/WEB-INF/jsp/employee.jsp](src/main/webapp/WEB-INF/jsp/employee.jsp)
- [src/main/webapp/WEB-INF/jsp/employeeEdit.jsp](src/main/webapp/WEB-INF/jsp/employeeEdit.jsp)
- [src/main/webapp/WEB-INF/jsp/services.jsp](src/main/webapp/WEB-INF/jsp/services.jsp)
- [src/main/webapp/WEB-INF/jsp/service.jsp](src/main/webapp/WEB-INF/jsp/service.jsp)
- [src/main/webapp/WEB-INF/jsp/serviceEdit.jsp](src/main/webapp/WEB-INF/jsp/serviceEdit.jsp)
- [src/main/webapp/WEB-INF/jsp/contracts.jsp](src/main/webapp/WEB-INF/jsp/contracts.jsp)
- [src/main/webapp/WEB-INF/jsp/contract.jsp](src/main/webapp/WEB-INF/jsp/contract.jsp)
- [src/main/webapp/WEB-INF/jsp/contractEdit.jsp](src/main/webapp/WEB-INF/jsp/contractEdit.jsp)
- [src/main/webapp/WEB-INF/jsp/header.jsp](src/main/webapp/WEB-INF/jsp/header.jsp)
- [src/main/webapp/WEB-INF/jsp/footer.jsp](src/main/webapp/WEB-INF/jsp/footer.jsp)

### 3. Конфигурационный файл Spring

Корневой Spring-контекст:

- [src/main/webapp/WEB-INF/applicationContext.xml](src/main/webapp/WEB-INF/applicationContext.xml)

В нём настроены DAO-компоненты и `SessionFactory`.

### 4. Конфигурация основного сервлета приложения

Конфигурация `DispatcherServlet`:

- [src/main/webapp/WEB-INF/dispatcher-servlet.xml](src/main/webapp/WEB-INF/dispatcher-servlet.xml)

Также используется дескриптор приложения:

- [src/main/webapp/WEB-INF/web.xml](src/main/webapp/WEB-INF/web.xml)

### 5. Дополненный файл сборки Ant

Файл сборки:

- [build.xml](build.xml)

В нём поддержаны:

- разрешение зависимостей Ivy;
- компиляция основного кода и тестов;
- запуск DAO-тестов;
- запуск Selenium-тестов;
- сборка WAR;
- развертывание в Tomcat;
- команды `db.create`, `db.init`, `db.reset`.

### 6. Описание системных тестов и Selenium-тесты

Selenium-тесты реализованы в файлах:

- [src/test/java/ru/Pavelslavovich/webprak/selenium/SeleniumTests.java](src/test/java/ru/Pavelslavovich/webprak/selenium/SeleniumTests.java)
- [testng-selenium.xml](testng-selenium.xml)

Системные тесты покрывают:

- загрузку основных страниц;
- навигацию из главной страницы;
- успешное создание, изменение и удаление клиентов;
- успешное создание, изменение и удаление служащих;
- успешное создание, изменение и удаление услуг;
- регистрацию и изменение договоров;
- регистрацию договора с участниками;
- фильтрацию клиентов, служащих, услуг и договоров;
- негативные сценарии удаления сущностей, связанных с договорами;
- сценарии пустой выдачи фильтрации;
- работу карточек и переходов между связанными сущностями.

### 7. Итог тестирования

Проверка выполнена локально на Tomcat и PostgreSQL.

Результаты:

- DAO-тесты: 16 из 16 успешно;
- Selenium-тесты: 32 из 32 успешно.

Отчёт Selenium находится в:

- [build/test-output-selenium/webprak_selenium_suite/selenium-tests.xml](build/test-output-selenium/webprak_selenium_suite/selenium-tests.xml)

## Вывод

Требования этапа 3 выполнены:

- Web-интерфейс разработан;
- JSP-страницы созданы;
- Spring-контроллеры реализованы;
- конфигурация Spring и `DispatcherServlet` добавлена;
- Ant-сборка дополнена;
- системные тесты написаны и проходят;
- итоговый отчёт подготовлен.