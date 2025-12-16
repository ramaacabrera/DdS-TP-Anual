package gestorAdministrativo.repository;

import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudModificacion;
import gestorAdministrativo.utils.BDUtils;
import gestorAdministrativo.domain.Solicitudes.SolicitudDeModificacion;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
            String sql = "SELECT * FROM Solicitud WHERE tipo_solicitud = 'MODIFICACION'";

            Query query = em.createNativeQuery(sql, SolicitudDeModificacion.class);

            List<SolicitudDeModificacion> lista = query.getResultList();

            for (SolicitudDeModificacion s : lista) {

                if (s.getUsuario() != null) {
                    s.getUsuario().getUsername();
                }

                if (s.getHechoModificado() != null) {
                    s.getHechoModificado().getContenidoMultimedia().size();
                }

                if (s.getHechoAsociado() != null) {
                    s.getHechoAsociado().getTitulo();
                }
            }
            // -------------------------------

            return lista;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Optional<SolicitudDeModificacion> buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String sql = "SELECT * FROM Solicitud " +
                    "WHERE id = :id " +
                    "AND tipo_solicitud = 'MODIFICACION'";

            Query query = em.createNativeQuery(sql, SolicitudDeModificacion.class);

            query.setParameter("id", id.toString());

            SolicitudDeModificacion s = (SolicitudDeModificacion) query.getSingleResult();

            if (s.getUsuario() != null) {
                s.getUsuario().getUsername();
            }

            if (s.getHechoModificado() != null) {
                s.getHechoModificado().getContenidoMultimedia().size();

                if (s.getHechoModificado().getUbicacion() != null) {
                    s.getHechoModificado().getUbicacion().getDescripcion();
                }
            }

            if (s.getHechoAsociado() != null) {
                s.getHechoAsociado().getTitulo();
            }

            Hibernate.initialize(s.getHechoAsociado().getContenidoMultimedia());
            Hibernate.initialize(s.getHechoAsociado().getEtiquetas());

            return Optional.of(s);

        } catch (javax.persistence.NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Integer obtenerCantidadPendientes() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String sql = "SELECT COUNT(*) FROM Solicitud " +
                    "WHERE tipo_solicitud = 'MODIFICACION' " +
                    "AND estado_modificacion = :estadoStr";

            Query query = em.createNativeQuery(sql);

            query.setParameter("estadoStr", EstadoSolicitudModificacion.PENDIENTE.name());

            Number resultado = (Number) query.getSingleResult();

            return resultado != null ? resultado.intValue() : 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}