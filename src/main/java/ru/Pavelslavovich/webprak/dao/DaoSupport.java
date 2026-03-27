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
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = action.apply(session);
                tx.commit();
                return result;
            } catch (RuntimeException ex) {
                tx.rollback();
                throw ex;
            }
        }
    }
}
