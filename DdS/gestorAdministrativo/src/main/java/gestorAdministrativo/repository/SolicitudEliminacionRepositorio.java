package gestorAdministrativo.repository;

import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudModificacion;
import gestorAdministrativo.utils.BDUtils;
import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudEliminacion;
import gestorAdministrativo.domain.Solicitudes.SolicitudDeEliminacion;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SolicitudEliminacionRepositorio {

    public SolicitudEliminacionRepositorio() {
    }

    public void guardar(SolicitudDeEliminacion solicitud) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);

            if (solicitud.getId() == null) {
                em.persist(solicitud);
            } else {
                em.merge(solicitud);
            }

            BDUtils.commit(em);
            System.out.println("✅ Solicitud guardada con éxito: " + solicitud.getId());
        } catch (Exception e) {
            BDUtils.rollback(em);
            System.err.println("❌ Error guardando solicitud: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar la solicitud", e);
        } finally {
            em.close();
        }
    }

    public long contarTodas(String estado){
        System.out.println("Contando solicitudes en estado: " + estado);
        EntityManager em = BDUtils.getEntityManager();
        try {
            if(estado == null){
                return em.createQuery("SELECT COUNT(s) FROM SolicitudDeEliminacion s", Long.class)
                        .getSingleResult();
            } else{
                String jpql = "SELECT COUNT(s) FROM SolicitudDeEliminacion s" +
                " WHERE s.estado = :estado";

                TypedQuery<Long> query = em.createQuery(jpql, Long.class);
                query.setParameter("estado", EstadoSolicitudEliminacion.valueOf(estado));

                return query.getSingleResult();
            }
        } catch (Exception e) {
            System.err.println("Error al contar solicitudes: " + e.getMessage());
        } finally {
            em.close();
        }
        return 0;
    }

    public List<SolicitudDeEliminacion> obtenerPaginadas(int pagina, int limite, String estado){
        EntityManager em = BDUtils.getEntityManager();
        try {
            if(estado == null){
                String jpql = "SELECT s FROM SolicitudDeEliminacion s " +
                        "LEFT JOIN FETCH s.usuario " +
                        "LEFT JOIN FETCH s.hechoAsociado " +
                        "ORDER BY s.id ASC";

                TypedQuery<SolicitudDeEliminacion> query = em.createQuery(jpql, SolicitudDeEliminacion.class);

                int offset = (pagina - 1) * limite;

                query.setFirstResult(offset);
                query.setMaxResults(limite);

                return query.getResultList();
            } else{
                String jpql = "SELECT s FROM SolicitudDeEliminacion s " +
                        "LEFT JOIN FETCH s.usuario " +
                        "LEFT JOIN FETCH s.hechoAsociado " +
                        "WHERE s.estado = :estado " +
                        "ORDER BY s.id ASC";
                TypedQuery<SolicitudDeEliminacion> query = em.createQuery(jpql, SolicitudDeEliminacion.class);
                query.setParameter("estado", EstadoSolicitudEliminacion.valueOf(estado));

                int offset = (pagina - 1) * limite;

                query.setFirstResult(offset);
                query.setMaxResults(limite);

                return query.getResultList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud) {
        this.guardar(solicitud);
    }

    public List<SolicitudDeEliminacion> buscarTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT s FROM SolicitudDeEliminacion s " +
                    "LEFT JOIN FETCH s.usuario " +
                    "LEFT JOIN FETCH s.hechoAsociado h " +
                    "ORDER BY s.estado ASC";

            TypedQuery<SolicitudDeEliminacion> query = em.createQuery(jpql, SolicitudDeEliminacion.class);
            return query.getResultList();

        } catch (Exception e) {
            System.err.println("❌ Error en buscarTodas: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Optional<SolicitudDeEliminacion> buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT s FROM SolicitudDeEliminacion s " +
                    "LEFT JOIN FETCH s.usuario " +
                    "LEFT JOIN FETCH s.hechoAsociado h " +
                    "LEFT JOIN FETCH h.etiquetas " +
                    "LEFT JOIN FETCH h.ubicacion " +
                    "WHERE s.id = :id";

            TypedQuery<SolicitudDeEliminacion> query = em.createQuery(jpql, SolicitudDeEliminacion.class);
            query.setParameter("id", id);

            return Optional.of(query.getSingleResult());

        } catch (javax.persistence.NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public boolean actualizarEstadoSolicitudEliminacion(String accion, UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            SolicitudDeEliminacion solicitud = em.find(SolicitudDeEliminacion.class, id);

            if (solicitud == null) {
                return false;
            }

            BDUtils.comenzarTransaccion(em);

            EstadoSolicitudEliminacion nuevoEstado = EstadoSolicitudEliminacion.valueOf(accion.toUpperCase());

            if (nuevoEstado == EstadoSolicitudEliminacion.ACEPTADA) {
                solicitud.aceptarSolicitud();
            } else if (nuevoEstado == EstadoSolicitudEliminacion.RECHAZADA) {
                solicitud.rechazarSolicitud();
            } else {
                solicitud.setEstado(nuevoEstado);
            }

            BDUtils.commit(em);

            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("Estado inválido: " + accion);
            BDUtils.rollback(em);
            return false;
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public Integer obtenerCantidad(String estado) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String jpql = "SELECT COUNT(s) FROM SolicitudDeEliminacion s WHERE s.estado = :estado";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("estado", EstadoSolicitudEliminacion.valueOf(estado));
            return query.getSingleResult().intValue();
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0; // Es mejor devolver 0 que null si es un conteo
        }
    }
}