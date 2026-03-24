package ru.Pavelslavovich.webprak.dao;

import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.config.HibernateUtil;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ServiceDao extends DaoSupport {
    public ServiceDao() {
        super(HibernateUtil.getSessionFactory());
    }

    public ServiceDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ServiceEntity save(ServiceEntity service) {
        return inTransaction(session -> {
            session.persist(service);
            return service;
        });
    }

    public Optional<ServiceEntity> findById(Long id) {
        return inTransaction(session -> Optional.ofNullable(session.get(ServiceEntity.class, id)));
    }

    public ServiceEntity update(ServiceEntity service) {
        return inTransaction(session -> (ServiceEntity) session.merge(service));
    }

    public boolean deleteById(Long id) {
        return inTransaction(session -> {
            ServiceEntity existing = session.get(ServiceEntity.class, id);
            if (existing == null) {
                return false;
            }
            session.remove(existing);
            return true;
        });
    }

    public List<ServiceEntity> findAll() {
        return inTransaction(session -> session.createQuery("from ServiceEntity s order by s.id", ServiceEntity.class).list());
    }

    public List<ServiceEntity> findByNameLikeAndCostRange(String namePart, BigDecimal minCost, BigDecimal maxCost) {
        return inTransaction(session -> session.createQuery(
                        """
                        from ServiceEntity s
                        where (:namePart is null or lower(s.name) like concat('%', lower(:namePart), '%'))
                          and (:minCost is null or s.baseCost >= :minCost)
                          and (:maxCost is null or s.baseCost <= :maxCost)
                        order by s.id
                        """,
                        ServiceEntity.class
                )
                .setParameter("namePart", namePart)
                .setParameter("minCost", minCost)
                .setParameter("maxCost", maxCost)
                .list());
    }
}
