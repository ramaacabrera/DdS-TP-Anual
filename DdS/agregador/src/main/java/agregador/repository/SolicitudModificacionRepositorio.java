package agregador.repository;

import agregador.domain.Solicitudes.SolicitudDeModificacion;
import agregador.utils.BDUtils;

import javax.persistence.EntityManager;
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
            System.out.println("GUARDANDO SOLICITUD 1");
            BDUtils.comenzarTransaccion(em);

            System.out.println("GUARDANDO SOLICITUD 2");

            if (solicitud.getId() == null) {
                em.persist(solicitud);
            } else {
                em.merge(solicitud);
            }

            System.out.println("GUARDANDO SOLICITUD 3");

            BDUtils.commit(em);
            System.out.println("GUARDANDO SOLICITUD 4");
        } catch (Exception e) {
            System.out.println("ERROR GUARDANDO SOLICITUD HAGO ROLLBACK");
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

}