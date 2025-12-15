package agregador.repository;

import agregador.domain.Solicitudes.EstadoSolicitudEliminacion;
import agregador.domain.Solicitudes.SolicitudDeEliminacion;
import agregador.utils.BDUtils;

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
        } catch (Exception e) {
            BDUtils.rollback(em);
            e.printStackTrace();
            throw new RuntimeException("Error al guardar la solicitud", e);
        } finally {
            em.close();
        }
    }

    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud) {
        this.guardar(solicitud);
    }

}