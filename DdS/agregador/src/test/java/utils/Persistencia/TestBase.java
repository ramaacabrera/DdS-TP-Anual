package utils.Persistencia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestBase {
    protected EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeEach
    void setUp() {
        // Configurar EntityManager para tests
        emf = Persistence.createEntityManagerFactory("test-pu");
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    protected void beginTransaction() {
        em.getTransaction().begin();
    }

    protected void commitTransaction() {
        em.getTransaction().commit();
    }

    protected void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}