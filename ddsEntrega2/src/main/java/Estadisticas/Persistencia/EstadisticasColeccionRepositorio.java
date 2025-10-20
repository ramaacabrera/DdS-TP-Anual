package Estadisticas.Persistencia;

import Estadisticas.Dominio.EstadisticasColeccion;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasColeccionRepositorio {
    private final EntityManagerFactory emf;

    public EstadisticasColeccionRepositorio(EntityManagerFactory emNuevo) {
        this.emf = emNuevo;
    }

        public void guardar(EstadisticasColeccion estadisticas) {
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

        public Optional<EstadisticasColeccion> buscarPorHandle(UUID handle) {
            EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EstadisticasColeccion> query = em.createQuery(
                    "SELECT e FROM EstadisticasColeccion e WHERE e.id.coleccion_id = :handleParam", EstadisticasColeccion.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<String> buscarProvinciaColeccion(UUID idColeccion) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticasColeccion_provincia FROM EstadisticasColeccion e JOIN Estadisticas es on es.estadisticas_id = e.id.estadisticas_id " +
                            "WHERE e.id.coleccion_id = :idColeccion AND es.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", String.class);

            query.setParameter("idColeccion", idColeccion);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    }