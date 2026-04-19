package ru.Pavelslavovich.webprak.dao;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.model.ContactMethodType;
import ru.Pavelslavovich.webprak.model.ContractEmployee;
import ru.Pavelslavovich.webprak.model.Employee;
import ru.Pavelslavovich.webprak.model.EmployeeContactMethod;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeDao extends DaoSupport {
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

    public List<Employee> search(String query) {
        String pattern = query == null || query.isBlank() ? null : "%" + query + "%";
        String hql = pattern == null
                ? "from Employee e order by e.id"
                : """
                  from Employee e
                  where e.fullName ilike :pattern
                     or e.position ilike :pattern
                     or (e.education is not null and e.education ilike :pattern)
                  order by e.id
                  """;

        return inTransaction(session -> {
            var typedQuery = session.createQuery(hql, Employee.class);
            if (pattern != null) {
                typedQuery.setParameter("pattern", pattern);
            }
            return typedQuery.list();
        });
    }

    public Optional<Employee> findByIdFull(Long id) {
        return inTransaction(session -> {
            Employee employee = session.get(Employee.class, id);
            if (employee != null) {
                Hibernate.initialize(employee.getContactMethods());
                Hibernate.initialize(employee.getContracts());
                for (ContractEmployee ce : employee.getContracts()) {
                    Hibernate.initialize(ce.getContract());
                    Hibernate.initialize(ce.getContract().getClient());
                    Hibernate.initialize(ce.getContract().getService());
                }
            }
            return Optional.ofNullable(employee);
        });
    }

    public Employee updateFull(Long id, String fullName, String position, String education,
                               String homeAddress, String note, List<ContactMethodData> methods) {
        return inTransaction(session -> {
            Employee e = session.get(Employee.class, id);
            if (e == null) return null;
            e.setFullName(fullName);
            e.setPosition(position);
            e.setEducation(education);
            e.setHomeAddress(homeAddress);
            e.setNote(note);
            e.getContactMethods().clear();
            session.flush();
            for (ContactMethodData md : methods) {
                e.addContactMethod(new EmployeeContactMethod(md.type(), md.value(), md.primary()));
            }
            return e;
        });
    }

    public boolean hasContracts(Long employeeId) {
        return inTransaction(session -> {
            Long count = session.createQuery(
                    "select count(ce) from ContractEmployee ce where ce.employee.id = :employeeId", Long.class)
                    .setParameter("employeeId", employeeId).uniqueResult();
            return count > 0;
        });
    }

    public record ContactMethodData(ContactMethodType type, String value, boolean primary) {}
}
