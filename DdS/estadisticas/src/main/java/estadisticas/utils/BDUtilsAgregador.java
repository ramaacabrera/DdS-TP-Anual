package estadisticas.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BDUtilsAgregador {
    private static EntityManagerFactory factory;

    public static EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            System.out.println("Inicializando EntityManagerFactory con configuración dinámica...");

            try {
                LecturaConfig lector = new LecturaConfig();
                Properties config = lector.leerConfig();

                Map<String, Object> configOverrides = new HashMap<>();

                if (config.containsKey("DB_URL")) {
                    String url = config.getProperty("DB_URL");
                    System.out.println("🔌 Usando DB URL: " + url);
                    configOverrides.put("javax.persistence.jdbc.url", url);
                    configOverrides.put("hibernate.connection.url", url);
                }

                if (config.containsKey("DB_USER")) {
                    configOverrides.put("javax.persistence.jdbc.user", config.getProperty("DB_USER"));
                    configOverrides.put("hibernate.connection.username", config.getProperty("DB_USER"));
                }

                if (config.containsKey("DB_PASS")) {
                    configOverrides.put("javax.persistence.jdbc.password", config.getProperty("DB_PASS"));
                    configOverrides.put("hibernate.connection.password", config.getProperty("DB_PASS"));
                }

                configOverrides.put("hibernate.hbm2ddl.auto", "update");

                factory = Persistence.createEntityManagerFactory("servicioEstadisticas-PU", configOverrides);

            } catch (Exception e) {
                System.err.println("Error CRÍTICO al conectar a la BD: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Fallo al iniciar BDUtils", e);
            }
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
