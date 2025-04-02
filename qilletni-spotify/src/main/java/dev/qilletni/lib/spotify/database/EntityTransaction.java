package dev.qilletni.lib.spotify.database;

import org.hibernate.Session;

public class EntityTransaction implements AutoCloseable {

    private final Session session;

    private EntityTransaction(Session session) {
        this.session = session;
    }
    
    public static EntityTransaction beginTransaction() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        return new EntityTransaction(session);
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void close() {
        session.getTransaction().commit();
        session.close();
    }
}
