package utils.Persistencia;

import utils.Dominio.Solicitudes.EstadoSolicitudEliminacion;
import utils.Dominio.Solicitudes.EstadoSolicitudModificacion;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
import utils.Dominio.Solicitudes.SolicitudDeModificacion;
import utils.BDUtils;
import utils.DTO.SolicitudDeModificacionDTO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SolicitudModificacionRepositorio {
    //private final EntityManagerFactory emf;
    //private final List<SolicitudDeModificacion> solicitudes = new ArrayList<SolicitudDeModificacion>();
    private final HechoRepositorio hechoRepositorio;

    public SolicitudModificacionRepositorio(HechoRepositorio  hechoRepositorio) {
        this.hechoRepositorio = new HechoRepositorio();
    }

    //private Optional<SolicitudDeModificacion> buscarSolicitudModificacion(){
        //return solicitudes.stream().findFirst();
    //}
    private Optional<SolicitudDeModificacion> buscarSolicitudModificacion() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            // JPQL que apunta a la entidad hija.
            TypedQuery<SolicitudDeModificacion> query = em.createQuery(
                    "SELECT s FROM SolicitudDeModificacion s", SolicitudDeModificacion.class);

            query.setMaxResults(1);

            SolicitudDeModificacion solicitud = query.getSingleResult();

            return Optional.ofNullable(solicitud);

        } catch (NoResultException e) {
            // No se encontró la solicitud
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    //public void agregarSolicitudDeModificacion(SolicitudDeModificacion solicitud){
        //solicitudes.add(solicitud);
    //}
    public void agregarSolicitudDeModificacion(SolicitudDeModificacion solicitud) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            BDUtils.comenzarTransaccion(em);
            em.merge(solicitud);
            BDUtils.commit(em);
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    //public void agregarSolicitudDeModificacion(SolicitudDeModificacionDTO solicitud){
        //solicitudes.add(new SolicitudDeModificacion(solicitud));
    //}
    public void agregarSolicitudDeModificacion(SolicitudDeModificacionDTO solicitudDTO) {
        // Convierte utils.DTO a Entidad y guarda
        SolicitudDeModificacion solicitud = new SolicitudDeModificacion(solicitudDTO, hechoRepositorio);
        this.agregarSolicitudDeModificacion(solicitud);
    }


    //public void actualizarSolicitudModificacion(SolicitudDeModificacion solicitud){
        //solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    //}
    public void actualizarSolicitudModificacion(SolicitudDeModificacion solicitud){
        this.agregarSolicitudDeModificacion(solicitud);
    }

    ///

    public List<SolicitudDeModificacion> buscarTodas() {
        EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<SolicitudDeModificacion> query = em.createQuery(
                    "SELECT s FROM SolicitudDeModificacion s", SolicitudDeModificacion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }


    public Optional<SolicitudDeModificacion> buscarPorId(UUID id) {
        EntityManager em = BDUtils.getEntityManager();
        try {
            SolicitudDeModificacion solicitud = em.find(SolicitudDeModificacion.class, id);

            return Optional.ofNullable(solicitud);
        } finally {
            em.close();
        }
    }

    public boolean actualizarEstadoSolicitudModificacion(String body, UUID id) {
        Optional<SolicitudDeModificacion> resultadoBusqueda = this.buscarPorId(id);
        if (resultadoBusqueda.isPresent()) {
            SolicitudDeModificacion solicitud = resultadoBusqueda.get();
            EstadoSolicitudModificacion estadoEnum = EstadoSolicitudModificacion.valueOf(body.toUpperCase());
            if (estadoEnum == EstadoSolicitudModificacion.ACEPTADA) {
                solicitud.aceptarSolicitud();
            } else if (estadoEnum == EstadoSolicitudModificacion.RECHAZADA) {
                solicitud.rechazarSolicitud();
            } else {
                return false;
            }

            // agregador.Persistencia del cambio
            EntityManager em = BDUtils.getEntityManager();
            try {
                BDUtils.comenzarTransaccion(em);
                // em.merge actualiza el objeto modificado que ya fue cargado de la BD
                em.merge(solicitud);
                BDUtils.commit(em);
                return true;
            } catch (Exception e) {
                BDUtils.rollback(em);
                e.printStackTrace();
                return false;
            } finally {
                em.close();
            }
        }
        return false; // No se encontró la solicitud
    }

}
