package ru.Pavelslavovich.webprak.dao;

import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.config.HibernateUtil;
import ru.Pavelslavovich.webprak.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeDao extends DaoSupport {
    public EmployeeDao() {
        super(HibernateUtil.getSessionFactory());
    }

    public EmployeeDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Employee save(Employee employee) {
        return inTransaction(session -> {
            session.persist(employee);
            return employee;
        });
    }

    public Optional<Employee> findById(Long id) {
        return inTransaction(session -> Optional.ofNullable(session.get(Employee.class, id)));
    }

    public Employee update(Employee employee) {
        return inTransaction(session -> (Employee) session.merge(employee));
    }

    public boolean deleteById(Long id) {
        return inTransaction(session -> {
            Employee existing = session.get(Employee.class, id);
            if (existing == null) {
                return false;
            }
            session.remove(existing);
            return true;
        });
    }

    public List<Employee> findAll() {
        return inTransaction(session -> session.createQuery("from Employee e order by e.id", Employee.class).list());
    }

    public List<Employee> findByClientAndPeriod(Long clientId, LocalDate fromDate, LocalDate toDate) {
        return inTransaction(session -> session.createQuery(
                        """
                        select distinct e
                        from Employee e
                        join e.contracts ce
                        join ce.contract sc
                        where sc.client.id = :clientId
                          and sc.serviceStart <= :toDate
                          and (sc.serviceEnd is null or sc.serviceEnd >= :fromDate)
                        order by e.id
                        """,
                        Employee.class
                )
                .setParameter("clientId", clientId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .list());
    }
}
