package Estadisticas.Persistencia;

import Estadisticas.Dominio.EstadisticasCategoria;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalTime;
import java.util.Optional;

public class EstadisticasCategoriaRepositorio {
    private final EntityManager em;

    public EstadisticasCategoriaRepositorio(EntityManager emNuevo) {
        this.em = emNuevo;
    }

    public void guardar(EstadisticasCategoria estadisticas) {
        try {
            BDUtils.comenzarTransaccion(em);

            em.merge(estadisticas);

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        }
    }

    public Optional<EstadisticasCategoria> buscarPorHandle(String handle) {
        try {
            TypedQuery<EstadisticasCategoria> query = em.createQuery(
                    "SELECT e FROM EstadisticasCategoria e WHERE e.id.estadisticas_id = :handleParam", EstadisticasCategoria.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<String> buscarProvinciaCategoria(String categoria) {
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticasCategoria_provincia FROM EstadisticasCategoria e JOIN Estadisticas es on es.estadisticas_id = e.id.estadisticas_id " +
                            "WHERE e.id.categoria = :idCategoria AND es.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", String.class);

            query.setParameter("idCategoria", categoria);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<LocalTime> buscarHoraCategoria(String categoria) {
        try {
            TypedQuery<LocalTime> query = em.createQuery(
                    "SELECT e.estadisticasCategoria_hora FROM EstadisticasCategoria e JOIN Estadisticas es on es.estadisticas_id = e.id.estadisticas_id " +
                            "WHERE e.id.categoria = :idCategoria AND es.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", LocalTime.class);

            query.setParameter("idCategoria", categoria);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
