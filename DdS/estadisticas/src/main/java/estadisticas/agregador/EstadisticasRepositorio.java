package estadisticas.agregador;

import estadisticas.Dominio.Estadisticas;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasRepositorio {
    private final EntityManagerFactory emf;

    public EstadisticasRepositorio(EntityManagerFactory emNuevo) {
        this.emf = emNuevo;
    }

    public void guardar(Estadisticas estadisticas) {
            EntityManager em = emf.createEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(estadisticas);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        }  finally {
            em.close();
        }
    }

    public Optional<Estadisticas> buscarPorHandle(UUID handle) {
            EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Estadisticas> query = em.createQuery(
                    "SELECT e FROM Estadisticas e WHERE e.estadisticas_id = :handleParam", Estadisticas.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<Integer> buscarSpam() {
            EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT e.estadisticas_spam FROM Estadisticas e WHERE e.estadisticas_fecha = (select max(e1.estadisticas_fecha) from Estadisticas e1)", Integer.class);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<String> buscarCategoria_max_hechos() {
            EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticas_categoria_max_hechos FROM Estadisticas e WHERE e.estadisticas_fecha = (select max(e1.estadisticas_fecha) from Estadisticas e1)", String.class);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}