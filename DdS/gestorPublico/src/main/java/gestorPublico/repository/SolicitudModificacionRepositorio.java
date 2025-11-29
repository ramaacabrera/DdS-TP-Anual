package gestorPublico.repository;

import gestorPublico.domain.Solicitudes.SolicitudDeModificacion;
import utils.BDUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SolicitudModificacionRepositorio {

    public SolicitudModificacionRepositorio() {
        // No necesitamos inyectar HechoRepositorio aquí.
    }

    public void guardar(SolicitudDeModificacion solicitud) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            if (solicitud.getId() == null) {
                em.persist(solicitud);
            } else {
                em.merge(solicitud);
            }

            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
            throw new RuntimeException("Error guardando solicitud de modificación", e);
        } finally {
            em.close();
        }
    }

    public void agregarSolicitudDeModificacion(SolicitudDeModificacion solicitud) {
        this.guardar(solicitud);
    }

    public List<SolicitudDeModificacion> buscarTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT s FROM SolicitudDeModificacion s " +
                    "LEFT JOIN FETCH s.usuario " +
                    "LEFT JOIN FETCH s.hechoAsociado h " +
                    "ORDER BY s.estadoSolicitudModificacion ASC";

            TypedQuery<SolicitudDeModificacion> query = em.createQuery(jpql, SolicitudDeModificacion.class);
            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Optional<SolicitudDeModificacion> buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT s FROM SolicitudDeModificacion s " +
                    "LEFT JOIN FETCH s.usuario " +
                    "LEFT JOIN FETCH s.hechoAsociado h " +
                    "LEFT JOIN FETCH h.etiquetas " +
                    "WHERE s.id = :id";

            TypedQuery<SolicitudDeModificacion> query = em.createQuery(jpql, SolicitudDeModificacion.class);
            query.setParameter("id", id);

            return Optional.of(query.getSingleResult());

        } catch (javax.persistence.NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}