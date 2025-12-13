package estadisticas.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class BDUtilsEstadisticas {
    private static EntityManagerFactory factory;

    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            factory = javax.persistence.Persistence.createEntityManagerFactory("estadisticas-PU");
        }
        return factory.createEntityManager();
    }

    public static void comenzarTransaccion(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
    }

    public static void commit(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            tx.commit();
        }
    }

    public static void rollback(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            tx.rollback();
        }
    }
}

