package Agregador.Persistencia;

import Agregador.Solicitudes.EstadoSolicitudEliminacion;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import utils.DTO.SolicitudDeEliminacionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SolicitudEliminacionRepositorio {
    private final List<SolicitudDeEliminacion> solicitudes = new ArrayList<SolicitudDeEliminacion>();

    public SolicitudEliminacionRepositorio() {}

    private Optional<SolicitudDeEliminacion> buscarSolicitudEliminacion(){
        return solicitudes.stream().findFirst();
    }

    public List<SolicitudDeEliminacion> buscarTodas(){return solicitudes;}

    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud){
        solicitudes.add(solicitud);
    }

    public void agregarSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud){
        solicitudes.add(new SolicitudDeEliminacion(solicitud));
    }

    public boolean actualizarEstadoSolicitudEliminacion(String body, int id){
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
    }

    public Optional<SolicitudDeEliminacion> buscarPorId(int id){
        return solicitudes.stream().filter(c -> c.getId() == id).findFirst();
    }
}
