package Estadisticas.Persistencia;

import Estadisticas.Dominio.EstadisticasCategoria;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalTime;
import java.util.Optional;

public class EstadisticasCategoriaRepositorio {
    private final EntityManagerFactory emf;

    public EstadisticasCategoriaRepositorio(EntityManagerFactory emNuevo) {
        this.emf = emNuevo;
    }

    public void guardar(EstadisticasCategoria estadisticas) {
        EntityManager em = emf.createEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(estadisticas);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Optional<EstadisticasCategoria> buscarPorHandle(String handle) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EstadisticasCategoria> query = em.createQuery(
                    "SELECT e FROM EstadisticasCategoria e WHERE e.id.estadisticas_id = :handleParam", EstadisticasCategoria.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<String> buscarProvinciaCategoria(String categoria) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticasCategoria_provincia FROM EstadisticasCategoria e " +
                            "WHERE e.id.categoria = :idCategoria AND e.estadisticas.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", String.class);

            query.setParameter("idCategoria", categoria);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<LocalTime> buscarHoraCategoria(String categoria) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<LocalTime> query = em.createQuery(
                    "SELECT e.estadisticasCategoria_hora FROM EstadisticasCategoria e " +
                            "WHERE e.id.categoria = :idCategoria AND e.estadisticas.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", LocalTime.class);

            query.setParameter("idCategoria", categoria);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
