package ru.Pavelslavovich.webprak.dao;

import org.hibernate.SessionFactory;
import ru.Pavelslavovich.webprak.model.ServiceEntity;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ServiceDao extends DaoSupport {
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
        String pattern = namePart == null || namePart.isBlank() ? null : "%" + namePart + "%";
        List<String> conditions = new ArrayList<>();
        if (pattern != null) {
            conditions.add("s.name ilike :pattern");
        }
        if (minCost != null) {
            conditions.add("s.baseCost >= :minCost");
        }
        if (maxCost != null) {
            conditions.add("s.baseCost <= :maxCost");
        }

        StringBuilder hql = new StringBuilder("from ServiceEntity s");
        if (!conditions.isEmpty()) {
            hql.append(" where ").append(String.join(" and ", conditions));
        }
        hql.append(" order by s.id");

        return inTransaction(session -> {
            var query = session.createQuery(hql.toString(), ServiceEntity.class);
            if (pattern != null) {
                query.setParameter("pattern", pattern);
            }
            if (minCost != null) {
                query.setParameter("minCost", minCost);
            }
            if (maxCost != null) {
                query.setParameter("maxCost", maxCost);
            }
            return query.list();
        });
    }

    public boolean hasContracts(Long serviceId) {
        return inTransaction(session -> {
            Long count = session.createQuery(
                    "select count(sc) from ServiceContract sc where sc.service.id = :serviceId", Long.class)
                    .setParameter("serviceId", serviceId).uniqueResult();
            return count > 0;
        });
    }

    public ServiceEntity updateById(Long id, String name, java.math.BigDecimal baseCost) {
        return inTransaction(session -> {
            ServiceEntity s = session.get(ServiceEntity.class, id);
            if (s == null) return null;
            s.setName(name);
            s.setBaseCost(baseCost);
            return s;
        });
    }
}
