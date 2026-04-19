package ru.Pavelslavovich.webprak.dao;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.model.Client;
import ru.Pavelslavovich.webprak.model.ClientContact;
import ru.Pavelslavovich.webprak.model.ClientContactMethod;
import ru.Pavelslavovich.webprak.model.ClientType;
import ru.Pavelslavovich.webprak.model.ContactMethodType;
import ru.Pavelslavovich.webprak.model.ContractEmployee;
import ru.Pavelslavovich.webprak.model.ServiceContract;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientDao extends DaoSupport {
    public ClientDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Client save(Client client) {
        return inTransaction(session -> {
            session.persist(client);
            return client;
        });
    }

    public Optional<Client> findById(Long id) {
        return inTransaction(session -> Optional.ofNullable(session.get(Client.class, id)));
    }

    public Client update(Client client) {
        return inTransaction(session -> (Client) session.merge(client));
    }

    public boolean deleteById(Long id) {
        return inTransaction(session -> {
            Client existing = session.get(Client.class, id);
            if (existing == null) {
                return false;
            }
            session.remove(existing);
            return true;
        });
    }

    public List<Client> findAll() {
        return inTransaction(session -> session.createQuery("from Client c order by c.id", Client.class).list());
    }

    public List<Client> findByServiceInPeriodAndEmployee(Long serviceId, LocalDate fromDate, LocalDate toDate, Long employeeId) {
        return inTransaction(session -> session.createQuery(
                        """
                        select distinct c
                        from Client c
                        join c.contracts sc
                        left join sc.employees ce
                        where (:serviceId is null or sc.service.id = :serviceId)
                          and (:employeeId is null or ce.employee.id = :employeeId)
                          and sc.serviceStart <= :toDate
                          and (sc.serviceEnd is null or sc.serviceEnd >= :fromDate)
                        order by c.id
                        """,
                        Client.class
                )
                .setParameter("serviceId", serviceId)
                .setParameter("employeeId", employeeId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .list());
    }

    public List<Client> search(String nameQuery, ClientType type) {
        String pattern = nameQuery == null || nameQuery.isBlank() ? null : "%" + nameQuery + "%";
        List<String> conditions = new ArrayList<>();
        if (pattern != null) {
            conditions.add("c.displayName ilike :pattern");
        }
        if (type != null) {
            conditions.add("c.clientType = :type");
        }

        StringBuilder hql = new StringBuilder("from Client c");
        if (!conditions.isEmpty()) {
            hql.append(" where ").append(String.join(" and ", conditions));
        }
        hql.append(" order by c.id");

        return inTransaction(session -> {
            var query = session.createQuery(hql.toString(), Client.class);
            if (pattern != null) {
                query.setParameter("pattern", pattern);
            }
            if (type != null) {
                query.setParameter("type", type);
            }
            return query.list();
        });
    }

    public Optional<Client> findByIdFull(Long id) {
        return inTransaction(session -> {
            Client client = session.get(Client.class, id);
            if (client != null) {
                Hibernate.initialize(client.getContacts());
                for (ClientContact cc : client.getContacts()) {
                    Hibernate.initialize(cc.getMethods());
                }
                Hibernate.initialize(client.getContracts());
                for (ServiceContract sc : client.getContracts()) {
                    Hibernate.initialize(sc.getService());
                    Hibernate.initialize(sc.getEmployees());
                    for (ContractEmployee ce : sc.getEmployees()) {
                        Hibernate.initialize(ce.getEmployee());
                    }
                }
            }
            return Optional.ofNullable(client);
        });
    }

    public Client updateFull(Long id, ClientType type, String displayName, String note,
                             List<ContactData> contacts) {
        return inTransaction(session -> {
            Client client = session.get(Client.class, id);
            if (client == null) return null;
            client.setClientType(type);
            client.setDisplayName(displayName);
            client.setNote(note);
            client.getContacts().clear();
            session.flush();
            for (ContactData cd : contacts) {
                ClientContact cc = new ClientContact(cd.fullName(), cd.role(), cd.comment());
                for (MethodData md : cd.methods()) {
                    cc.addMethod(new ClientContactMethod(md.type(), md.value(), md.primary()));
                }
                client.addContact(cc);
            }
            return client;
        });
    }

    public boolean hasContracts(Long clientId) {
        return inTransaction(session -> {
            Long count = session.createQuery(
                    "select count(sc) from ServiceContract sc where sc.client.id = :clientId", Long.class)
                    .setParameter("clientId", clientId).uniqueResult();
            return count > 0;
        });
    }

    public record ContactData(String fullName, String role, String comment, List<MethodData> methods) {}
    public record MethodData(ContactMethodType type, String value, boolean primary) {}
}
