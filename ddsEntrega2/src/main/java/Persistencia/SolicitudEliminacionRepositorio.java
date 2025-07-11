package Persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.agregador.Solicitudes.EstadoSolicitudEliminacion;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;

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

    public void eliminarSolicitudEliminacion(SolicitudDeEliminacion solicitud){
        solicitudes.remove(solicitud);
    }

    public void actualizarSolicitudEliminacion(SolicitudDeEliminacion solicitud){
        solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    }

    public boolean actualizarEstadoSolicitudEliminacion(String body, String id){
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

    public Optional<SolicitudDeEliminacion> buscarPorId(String id){
        return solicitudes.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public void add(SolicitudDeEliminacion solicitudDeEliminacion) {
        solicitudes.add(solicitudDeEliminacion);
    }
}
