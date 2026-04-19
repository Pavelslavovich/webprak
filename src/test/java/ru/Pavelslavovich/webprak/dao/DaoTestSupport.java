package ru.Pavelslavovich.webprak.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import ru.Pavelslavovich.webprak.model.Client;
import ru.Pavelslavovich.webprak.model.ClientType;
import ru.Pavelslavovich.webprak.model.ContractStatus;
import ru.Pavelslavovich.webprak.model.Employee;
import ru.Pavelslavovich.webprak.model.ServiceContract;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class DaoTestSupport {
    private static final String TEST_DB_URL = "jdbc:h2:mem:webprak;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=VALUE";
    private static final String TEST_DB_USER = "sa";
    private static final String TEST_DB_PASSWORD = "";

    protected SessionFactory sessionFactory;

    @BeforeClass
    public void setUpSessionFactory() {
        initializeSchema();
        sessionFactory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeMethod
    public void cleanDatabase() {
        inTransaction(session -> {
            session.createMutationQuery("delete from ContractEmployee").executeUpdate();
            session.createMutationQuery("delete from ServiceContract").executeUpdate();
            session.createMutationQuery("delete from EmployeeContactMethod").executeUpdate();
            session.createMutationQuery("delete from Employee").executeUpdate();
            session.createMutationQuery("delete from ClientContactMethod").executeUpdate();
            session.createMutationQuery("delete from ClientContact").executeUpdate();
            session.createMutationQuery("delete from ServiceEntity").executeUpdate();
            session.createMutationQuery("delete from Client").executeUpdate();
            return null;
        });
    }

    protected SeedData seedBasicData() {
        return inTransaction(session -> {
            Client c1 = new Client(ClientType.ORGANIZATION, "ООО Альфа", "клиент 1");
            Client c2 = new Client(ClientType.INDIVIDUAL, "Петров Петр", "клиент 2");
            session.persist(c1);
            session.persist(c2);

            ServiceEntity s1 = new ServiceEntity("Судебное представительство", new BigDecimal("90000.00"));
            ServiceEntity s2 = new ServiceEntity("Консультации", new BigDecimal("5000.00"));
            session.persist(s1);
            session.persist(s2);

            Employee e1 = new Employee("Иванов И.И.", "Юрист", "МГУ", "Москва", null);
            Employee e2 = new Employee("Сидоров С.С.", "Партнер", "СПбГУ", "СПб", null);
            Employee e3 = new Employee("Климова А.А.", "Юрист", "МГЮА", "Москва", null);
            session.persist(e1);
            session.persist(e2);
            session.persist(e3);

            ServiceContract sc1 = new ServiceContract(
                    "LF-T-001",
                    c1,
                    s1,
                    LocalDate.of(2026, 1, 5),
                    LocalDate.of(2026, 1, 10),
                    null,
                    ContractStatus.ACTIVE,
                    new BigDecimal("95000.00"),
                    "идет"
            );
            sc1.addEmployee(e1, "ведущий");
            sc1.addEmployee(e2, "куратор");
            session.persist(sc1);

            ServiceContract sc2 = new ServiceContract(
                    "LF-T-002",
                    c2,
                    s2,
                    LocalDate.of(2025, 11, 1),
                    LocalDate.of(2025, 11, 10),
                    LocalDate.of(2025, 11, 20),
                    ContractStatus.COMPLETED,
                    new BigDecimal("5000.00"),
                    "закрыт"
            );
            sc2.addEmployee(e3, "консультант");
            session.persist(sc2);

            List<Client> clients = new ArrayList<>();
            clients.add(c1);
            clients.add(c2);

            List<Employee> employees = new ArrayList<>();
            employees.add(e1);
            employees.add(e2);
            employees.add(e3);

            List<ServiceEntity> services = new ArrayList<>();
            services.add(s1);
            services.add(s2);

            List<ServiceContract> contracts = new ArrayList<>();
            contracts.add(sc1);
            contracts.add(sc2);

            return new SeedData(clients, employees, services, contracts);
        });
    }

    protected <T> T inTransaction(java.util.function.Function<Session, T> action) {
        Session session = null;
        Transaction tx = null;
        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            T result = action.apply(session);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    protected record SeedData(
            List<Client> clients,
            List<Employee> employees,
            List<ServiceEntity> services,
            List<ServiceContract> contracts
    ) {
    }

    private void initializeSchema() {
        String ddl = """
                drop table if exists contract_employees;
                drop table if exists service_contracts;
                drop table if exists employee_contact_methods;
                drop table if exists employees;
                drop table if exists client_contact_methods;
                drop table if exists client_contacts;
                drop table if exists services;
                drop table if exists clients;

                create table clients (
                    client_id bigint generated by default as identity primary key,
                    client_type varchar(32) not null,
                    display_name varchar(255) not null,
                    created_at timestamp with time zone not null,
                    note varchar(2000)
                );

                create table client_contacts (
                    contact_id bigint generated by default as identity primary key,
                    client_id bigint not null,
                    full_name varchar(255) not null,
                    role varchar(255),
                    comment varchar(2000),
                    constraint fk_client_contacts_client foreign key (client_id) references clients(client_id) on delete cascade
                );

                create table client_contact_methods (
                    method_id bigint generated by default as identity primary key,
                    contact_id bigint not null,
                    method_type varchar(32) not null,
                    value varchar(255) not null,
                    is_primary boolean not null,
                    constraint fk_client_contact_methods_contact foreign key (contact_id) references client_contacts(contact_id) on delete cascade
                );

                create table employees (
                    employee_id bigint generated by default as identity primary key,
                    full_name varchar(255) not null,
                    position varchar(255) not null,
                    education varchar(255),
                    home_address varchar(255),
                    note varchar(2000)
                );

                create table employee_contact_methods (
                    method_id bigint generated by default as identity primary key,
                    employee_id bigint not null,
                    method_type varchar(32) not null,
                    value varchar(255) not null,
                    is_primary boolean not null,
                    constraint fk_employee_contact_methods_employee foreign key (employee_id) references employees(employee_id) on delete cascade
                );

                create table services (
                    service_id bigint generated by default as identity primary key,
                    name varchar(255) not null,
                    base_cost decimal(12,2) not null
                );

                create table service_contracts (
                    contract_id bigint generated by default as identity primary key,
                    contract_number varchar(255) not null unique,
                    client_id bigint not null,
                    service_id bigint not null,
                    signed_on date not null,
                    service_start date not null,
                    service_end date,
                    status varchar(32) not null,
                    agreed_cost decimal(12,2) not null,
                    comment varchar(2000),
                    constraint fk_service_contracts_client foreign key (client_id) references clients(client_id),
                    constraint fk_service_contracts_service foreign key (service_id) references services(service_id)
                );

                create table contract_employees (
                    contract_id bigint not null,
                    employee_id bigint not null,
                    role varchar(255),
                    primary key (contract_id, employee_id),
                    constraint fk_contract_employees_contract foreign key (contract_id) references service_contracts(contract_id) on delete cascade,
                    constraint fk_contract_employees_employee foreign key (employee_id) references employees(employee_id) on delete cascade
                );
                """;

        try (Connection connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            for (String sql : ddl.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to initialize H2 test schema", ex);
        }
    }
}
