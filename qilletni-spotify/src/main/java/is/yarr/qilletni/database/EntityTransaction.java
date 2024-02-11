package is.yarr.qilletni.database;

import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.function.Function;

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
