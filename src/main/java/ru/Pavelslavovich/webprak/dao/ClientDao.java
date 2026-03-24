package ru.Pavelslavovich.webprak.dao;

import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.config.HibernateUtil;
import ru.Pavelslavovich.webprak.model.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClientDao extends DaoSupport {
    public ClientDao() {
        super(HibernateUtil.getSessionFactory());
    }

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
}
