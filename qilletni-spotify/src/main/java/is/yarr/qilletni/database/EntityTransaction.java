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
    
    public <T> List<T> findAllEntitiesFromExpression(Class<T> entityType, Function<CriteriaBuilder, Expression<Boolean>> predicates) {
        var session = getSession();

        var builder = session.getCriteriaBuilder();
        var criteria = builder.createQuery(entityType);
        
        criteria.where(predicates.apply(builder));
        
        return session.createQuery(criteria).getResultList();
    }
    
    public <T> List<T> findAllEntitiesFromPredicates(Class<T> entityType, Function<CriteriaBuilder, List<Predicate>> predicates) {
        var session = getSession();

        var builder = session.getCriteriaBuilder();
        var criteria = builder.createQuery(entityType);
        
        criteria.where(predicates.apply(builder).toArray(Predicate[]::new));
        
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public void close() {
        session.getTransaction().commit();
        session.close();
    }
}
