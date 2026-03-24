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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class DaoTestSupport {
    protected SessionFactory sessionFactory;

    @BeforeClass
    public void setUpSessionFactory() {
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
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            T result = action.apply(session);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }

    protected record SeedData(
            List<Client> clients,
            List<Employee> employees,
            List<ServiceEntity> services,
            List<ServiceContract> contracts
    ) {
    }
}
