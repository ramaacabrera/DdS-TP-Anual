package Agregador.Persistencia;

import Agregador.Solicitudes.EstadoSolicitudEliminacion;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import utils.BDUtils;
import utils.DTO.SolicitudDeEliminacionDTO;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SolicitudEliminacionRepositorio {
    private final EntityManager em;
    //private final List<SolicitudDeEliminacion> solicitudes = new ArrayList<SolicitudDeEliminacion>();

    public SolicitudEliminacionRepositorio(EntityManager em) {
        this.em = em;
    }

    //private Optional<SolicitudDeEliminacion> buscarSolicitudEliminacion(){
        //return solicitudes.stream().findFirst();
    //}
    private Optional<SolicitudDeEliminacion> buscarSolicitudEliminacion() {
        //EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<SolicitudDeEliminacion> query = em.createQuery("SELECT s FROM SolicitudDeEliminacion s", SolicitudDeEliminacion.class);
            query.setMaxResults(1);
            SolicitudDeEliminacion solicitud = query.getSingleResult();
            return Optional.ofNullable(solicitud);

        } catch (javax.persistence.NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    //public List<SolicitudDeEliminacion> buscarTodas(){return solicitudes;}
    public List<SolicitudDeEliminacion> buscarTodas() {
        //EntityManager em = BDUtils.getEntityManager();
        try {
            TypedQuery<SolicitudDeEliminacion> query = em.createQuery(
                    "SELECT s FROM SolicitudDeEliminacion s", SolicitudDeEliminacion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    //public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud){
        //solicitudes.add(solicitud);
    //}
    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud) {
        //EntityManager em = BDUtils.getEntityManager();
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

    //public void agregarSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud){
        //solicitudes.add(new SolicitudDeEliminacion(solicitud));
    //}
    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitudDTO) {
        // Convierte DTO a Entidad y guarda
        SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(solicitudDTO);
        this.agregarSolicitudDeEliminacion(solicitud);
    }

    /*public boolean actualizarEstadoSolicitudEliminacion(String body, int id){
        Optional<SolicitudDeEliminacion> resultadoBusqueda = this.buscarPorId(id);
        if(resultadoBusqueda.isPresent()) {
            EstadoSolicitudEliminacion estadoEnum = EstadoSolicitudEliminacion.valueOf(body.toUpperCase());
            if(estadoEnum == EstadoSolicitudEliminacion.ACEPTADA){
                resultadoBusqueda.get().aceptarSolicitud();
            } else if(estadoEnum == EstadoSolicitudEliminacion.RECHAZADA){
                resultadoBusqueda.get().rechazarSolicitud();
            } else {
                return false;
            }
            return true;
        }
        return false;
    }*/
    public boolean actualizarEstadoSolicitudEliminacion(String body, UUID id) {
        Optional<SolicitudDeEliminacion> resultadoBusqueda = this.buscarPorId(id);
        if (resultadoBusqueda.isPresent()) {
            SolicitudDeEliminacion solicitud = resultadoBusqueda.get();
            EstadoSolicitudEliminacion estadoEnum = EstadoSolicitudEliminacion.valueOf(body.toUpperCase());
            if (estadoEnum == EstadoSolicitudEliminacion.ACEPTADA) {
                solicitud.aceptarSolicitud();
            } else if (estadoEnum == EstadoSolicitudEliminacion.RECHAZADA) {
                solicitud.rechazarSolicitud();
            } else {
                return false;
            }

            // Persistencia del cambio
            //EntityManager em = BDUtils.getEntityManager();
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

    //public Optional<SolicitudDeEliminacion> buscarPorId(int id){
    //    return solicitudes.stream().filter(c -> c.getId() == id).findFirst();
    //}
    public Optional<SolicitudDeEliminacion> buscarPorId(UUID id) {
        //EntityManager em = BDUtils.getEntityManager();
        try {
            SolicitudDeEliminacion solicitud = em.find(SolicitudDeEliminacion.class, id);

            return Optional.ofNullable(solicitud);
        } finally {
            em.close();
        }
    }

}
