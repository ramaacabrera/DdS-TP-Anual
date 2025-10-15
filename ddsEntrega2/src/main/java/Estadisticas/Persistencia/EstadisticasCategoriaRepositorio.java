package Estadisticas.Persistencia;

import Estadisticas.Dominio.Estadisticas;
import Estadisticas.Dominio.EstadisticasCategoria;
import Estadisticas.Dominio.EstadisticasColeccion;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

public class EstadisticasCategoriaRepositorio {
    private final EntityManager em;

    public EstadisticasCategoriaRepositorio(EntityManager emNuevo) {
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

    public Optional<EstadisticasCategoria> buscarPorHandle(String handle) {
        try {
            TypedQuery<EstadisticasCategoria> query = em.createQuery(
                    "SELECT e FROM EstadisticasCategoria e WHERE e.estadisticasCategoria_estadisticas = :handleParam", EstadisticasCategoria.class);

            query.setParameter("handleParam", handle);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<String> buscarProvinciaCategoria(UUID idCategoria) {
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT e.estadisticasCategoria_provincia FROM EstadisticasCategoria e JOIN Estadisticas es on es.estadisticas_id = e.estadisticasCategoria_estadisticas" +
                            "WHERE e.estadisticasCategoria_categoria = :idCategoria AND es.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", String.class);

            query.setParameter("idCategoria", idCategoria.toString());

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<LocalTime> buscarHoraCategoria(UUID idCategoria) {
        try {
            TypedQuery<LocalTime> query = em.createQuery(
                    "SELECT e.estadisticasCategoria_hora FROM EstadisticasCategoria e JOIN Estadisticas es on es.estadisticas_id = e.estadisticasCategoria_estadisticas " +
                            "WHERE e.estadisticasCategoria_categoria = :idCategoria AND es.estadisticas_fecha = " +
                            "( SELECT MAX(e2.estadisticas_fecha) FROM Estadisticas e2)", LocalTime.class);

            query.setParameter("idCategoria", idCategoria);

            return Optional.of(query.getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
