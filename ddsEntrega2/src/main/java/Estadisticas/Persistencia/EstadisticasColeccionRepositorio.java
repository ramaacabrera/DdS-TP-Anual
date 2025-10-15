package Estadisticas.Persistencia;

import Estadisticas.Dominio.Estadisticas;
import Estadisticas.Dominio.EstadisticasColeccion;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasColeccionRepositorio {
    private final EntityManager em;

    public EstadisticasColeccionRepositorio(EntityManager emNuevo) {
        this.em = emNuevo;
    }

        public void guardar(Estadisticas estadisticas) {
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(estadisticas);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        }
    }

        public Optional<EstadisticasColeccion> buscarPorHandle(UUID handle) {
        try {
            TypedQuery<EstadisticasColeccion> query = em.createQuery(
                    "SELECT e FROM EstadisticasColeccion e WHERE e.estadisticasColeccion_estadisticas = :handleParam", EstadisticasColeccion.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<String> buscarProvinciaColeccion(UUID idColeccion) {
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticasColeccion_provincia FROM EstadisticasColeccion e JOIN Estadisticas es on es.estadisticas_id = e.estadisticasColeccion_estadisticas" +
                            "WHERE e.estadisticasColeccion_coleccion = :idColeccion AND es.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", String.class);

            query.setParameter("idColeccion", idColeccion);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    }