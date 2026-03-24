package ru.Pavelslavovich.webprak.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Function;

public abstract class DaoSupport {
    protected final SessionFactory sessionFactory;

    protected DaoSupport(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected <T> T inTransaction(Function<Session, T> action) {
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
}
