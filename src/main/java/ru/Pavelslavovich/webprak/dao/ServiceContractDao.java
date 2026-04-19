package ru.Pavelslavovich.webprak.dao;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.model.Client;
import ru.Pavelslavovich.webprak.model.ContractEmployee;
import ru.Pavelslavovich.webprak.model.ContractStatus;
import ru.Pavelslavovich.webprak.model.Employee;
import ru.Pavelslavovich.webprak.model.ServiceContract;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServiceContractDao extends DaoSupport {
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
            for (EmployeeRole role : employees) {
                Employee employee = session.get(Employee.class, role.employeeId());
                if (employee == null) {
                    return null;
                }
            }

            session.persist(contract);
            for (EmployeeRole role : employees) {
                Employee employee = session.get(Employee.class, role.employeeId());
                ContractEmployee contractEmployee = new ContractEmployee(contract, employee, role.role());
                contract.getEmployees().add(contractEmployee);
                employee.getContracts().add(contractEmployee);
            }
            return contract;
        });
    }

    public record EmployeeRole(Long employeeId, String role) {
    }

    public List<ServiceContract> findByFilters(Long clientId, Long serviceId, Long employeeId,
                                               ContractStatus status, LocalDate fromDate, LocalDate toDate) {
        return inTransaction(session -> {
            List<String> conditions = new ArrayList<>();
            if (clientId != null) {
                conditions.add("sc.client.id = :clientId");
            }
            if (serviceId != null) {
                conditions.add("sc.service.id = :serviceId");
            }
            if (employeeId != null) {
                conditions.add("ce.employee.id = :employeeId");
            }
            if (status != null) {
                conditions.add("sc.status = :status");
            }
            if (fromDate != null) {
                conditions.add("(sc.serviceEnd is null or sc.serviceEnd >= :fromDate)");
            }
            if (toDate != null) {
                conditions.add("sc.serviceStart <= :toDate");
            }

            StringBuilder hql = new StringBuilder(
                    "select distinct sc from ServiceContract sc left join sc.employees ce");
            if (!conditions.isEmpty()) {
                hql.append(" where ").append(String.join(" and ", conditions));
            }
            hql.append(" order by sc.id");

            var query = session.createQuery(hql.toString(), ServiceContract.class);
            if (clientId != null) {
                query.setParameter("clientId", clientId);
            }
            if (serviceId != null) {
                query.setParameter("serviceId", serviceId);
            }
            if (employeeId != null) {
                query.setParameter("employeeId", employeeId);
            }
            if (status != null) {
                query.setParameter("status", status);
            }
            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                query.setParameter("toDate", toDate);
            }

            List<ServiceContract> list = query.list();
            for (ServiceContract sc : list) {
                Hibernate.initialize(sc.getClient());
                Hibernate.initialize(sc.getService());
            }
            return list;
        });
    }

    public Optional<ServiceContract> findByIdFull(Long id) {
        return inTransaction(session -> {
            ServiceContract sc = session.get(ServiceContract.class, id);
            if (sc != null) {
                Hibernate.initialize(sc.getClient());
                Hibernate.initialize(sc.getService());
                Hibernate.initialize(sc.getEmployees());
                for (ContractEmployee ce : sc.getEmployees()) {
                    Hibernate.initialize(ce.getEmployee());
                }
            }
            return Optional.ofNullable(sc);
        });
    }

    public List<ServiceContract> findAllWithDetails() {
        return inTransaction(session -> session.createQuery(
                "from ServiceContract sc join fetch sc.client join fetch sc.service order by sc.id",
                ServiceContract.class
        ).list());
    }

    public ServiceContract updateFull(Long id, String contractNumber, Long clientId, Long serviceId,
                                      LocalDate signedOn, LocalDate serviceStart, LocalDate serviceEnd,
                                      ContractStatus status, BigDecimal agreedCost, String comment,
                                      List<EmployeeRole> employees) {
        return inTransaction(session -> {
            ServiceContract sc = session.get(ServiceContract.class, id);
            if (sc == null) return null;
            sc.setContractNumber(contractNumber);
            sc.setClient(session.get(Client.class, clientId));
            sc.setService(session.get(ServiceEntity.class, serviceId));
            sc.setSignedOn(signedOn);
            sc.setServiceStart(serviceStart);
            sc.setServiceEnd(serviceEnd);
            sc.setStatus(status);
            sc.setAgreedCost(agreedCost);
            sc.setComment(comment);
            sc.getEmployees().clear();
            session.flush();
            for (EmployeeRole er : employees) {
                Employee emp = session.get(Employee.class, er.employeeId());
                if (emp != null) {
                    ContractEmployee ce = new ContractEmployee(sc, emp, er.role());
                    sc.getEmployees().add(ce);
                }
            }
            return sc;
        });
    }
}
