package ru.Pavelslavovich.webprak.dao;

import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.config.HibernateUtil;
import ru.Pavelslavovich.webprak.model.ContractEmployee;
import ru.Pavelslavovich.webprak.model.ContractStatus;
import ru.Pavelslavovich.webprak.model.Employee;
import ru.Pavelslavovich.webprak.model.ServiceContract;

import java.util.List;
import java.util.Optional;

public class ServiceContractDao extends DaoSupport {
    public ServiceContractDao() {
        super(HibernateUtil.getSessionFactory());
    }

    public ServiceContractDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ServiceContract save(ServiceContract contract) {
        return inTransaction(session -> {
            session.persist(contract);
            return contract;
        });
    }

    public Optional<ServiceContract> findById(Long id) {
        return inTransaction(session -> Optional.ofNullable(session.get(ServiceContract.class, id)));
    }

    public Optional<ServiceContract> findByContractNumber(String contractNumber) {
        return inTransaction(session -> session.createQuery(
                        "from ServiceContract sc where sc.contractNumber = :contractNumber",
                        ServiceContract.class
                )
                .setParameter("contractNumber", contractNumber)
                .uniqueResultOptional());
    }

    public List<ServiceContract> findAll() {
        return inTransaction(session -> session.createQuery("from ServiceContract sc order by sc.id", ServiceContract.class).list());
    }

    public List<ServiceContract> findByStatus(ContractStatus status) {
        return inTransaction(session -> session.createQuery(
                        "from ServiceContract sc where sc.status = :status order by sc.id",
                        ServiceContract.class
                )
                .setParameter("status", status)
                .list());
    }

    public ServiceContract update(ServiceContract contract) {
        return inTransaction(session -> (ServiceContract) session.merge(contract));
    }

    public boolean deleteById(Long id) {
        return inTransaction(session -> {
            ServiceContract existing = session.get(ServiceContract.class, id);
            if (existing == null) {
                return false;
            }
            session.remove(existing);
            return true;
        });
    }

    public ServiceContract registerContractWithEmployees(ServiceContract contract, List<EmployeeRole> employees) {
        return inTransaction(session -> {
            session.persist(contract);
            for (EmployeeRole role : employees) {
                Employee employee = session.get(Employee.class, role.employeeId());
                if (employee == null) {
                    throw new IllegalArgumentException("Employee not found: " + role.employeeId());
                }
                ContractEmployee contractEmployee = new ContractEmployee(contract, employee, role.role());
                contract.getEmployees().add(contractEmployee);
                employee.getContracts().add(contractEmployee);
            }
            return contract;
        });
    }

    public record EmployeeRole(Long employeeId, String role) {
    }
}
