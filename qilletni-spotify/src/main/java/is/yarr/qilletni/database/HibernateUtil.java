package is.yarr.qilletni.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration()
                    .configure()
                    .setProperty("hibernate.connection.url", System.getenv("SPOTIFY_DB_URL"))
                    .setProperty("hibernate.connection.username", System.getenv("SPOTIFY_DB_USERNAME"))
                    .setProperty("hibernate.connection.password", System.getenv("SPOTIFY_DB_PASSWORD"))
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
