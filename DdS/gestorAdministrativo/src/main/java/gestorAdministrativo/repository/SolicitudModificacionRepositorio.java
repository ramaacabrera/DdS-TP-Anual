package gestorAdministrativo.repository;

import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudEliminacion;
import gestorAdministrativo.domain.Solicitudes.EstadoSolicitudModificacion;
import gestorAdministrativo.domain.Solicitudes.SolicitudDeEliminacion;
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

    public long contarTodas(String estado){
        System.out.println("Contando solicitudes en estado: " + estado);
        EntityManager em = BDUtils.getEntityManager();
        try {
            if(estado == null){
                return em.createQuery("SELECT COUNT(s) FROM SolicitudDeModificacion s", Long.class)
                        .getSingleResult();
            } else{
                String jpql = "SELECT COUNT(s) FROM SolicitudDeModificacion s" +
                        " WHERE s.estado = :estado";

                TypedQuery<Long> query = em.createQuery(jpql, Long.class);
                query.setParameter("estado", EstadoSolicitudModificacion.valueOf(estado));

                return query.getSingleResult();
            }
        } catch (Exception e) {
            System.err.println("Error al contar solicitudes: " + e.getMessage());
        } finally {
            em.close();
        }
        return 0;
    }

    public List<SolicitudDeModificacion> obtenerPaginadas(int pagina, int limite, String estado){
        EntityManager em = BDUtils.getEntityManager();
        try {
            if(estado == null){
                String jpql = "SELECT s FROM SolicitudDeModificacion s " +
                        "LEFT JOIN FETCH s.usuario " +
                        "LEFT JOIN FETCH s.hechoAsociado " +
                        "ORDER BY s.id ASC";

                TypedQuery<SolicitudDeModificacion> query = em.createQuery(jpql, SolicitudDeModificacion.class);

                int offset = (pagina - 1) * limite;

                query.setFirstResult(offset);
                query.setMaxResults(limite);

                for(SolicitudDeModificacion solicitud : query.getResultList()){
                    Hibernate.initialize(solicitud.getHechoAsociado().getContenidoMultimedia());
                    Hibernate.initialize(solicitud.getHechoAsociado().getEtiquetas());
                    Hibernate.initialize(solicitud.getHechoModificado().getContenidoMultimedia());
                }

                return query.getResultList();
            } else{
                System.out.println("Estado seleccionado: " + estado);
                String jpql = "SELECT s FROM SolicitudDeModificacion s " +
                        "LEFT JOIN FETCH s.usuario " +
                        "LEFT JOIN FETCH s.hechoAsociado " +
                        "WHERE s.estado = :estado " +
                        "ORDER BY s.id ASC";
                TypedQuery<SolicitudDeModificacion> query = em.createQuery(jpql, SolicitudDeModificacion.class);
                query.setParameter("estado", EstadoSolicitudModificacion.valueOf(estado));

                int offset = (pagina - 1) * limite;

                query.setFirstResult(offset);
                query.setMaxResults(limite);

                for(SolicitudDeModificacion solicitud : query.getResultList()){
                    Hibernate.initialize(solicitud.getHechoAsociado().getContenidoMultimedia());
                    Hibernate.initialize(solicitud.getHechoAsociado().getEtiquetas());
                    Hibernate.initialize(solicitud.getHechoModificado().getContenidoMultimedia());
                }

                return query.getResultList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
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

    public Integer obtenerCantidad(String estado) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            String sql = "SELECT COUNT(*) FROM Solicitud s WHERE s.estado_modificacion = :estado";

            Query query = em.createNativeQuery(sql);

            query.setParameter("estado", estado);

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