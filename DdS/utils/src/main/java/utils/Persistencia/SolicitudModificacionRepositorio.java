package utils.Persistencia;

import utils.Dominio.Solicitudes.SolicitudDeModificacion;
import utils.BDUtils;
import utils.DTO.SolicitudDeModificacionDTO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

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

}
