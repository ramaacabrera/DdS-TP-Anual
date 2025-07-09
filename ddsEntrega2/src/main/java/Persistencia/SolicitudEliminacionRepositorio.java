package Persistencia;

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

    public void agregarSolicitudDeEliminacion(SolicitudDeEliminacion solicitud){
        solicitudes.add(solicitud);
    }

    public void eliminarSolicitudEliminacion(SolicitudDeEliminacion solicitud){
        solicitudes.remove(solicitud);
    }

    public void actualizarSolicitudEliminacion(SolicitudDeEliminacion solicitud){
        solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    }

    public Optional<SolicitudDeEliminacion> buscarPorId(String id){
        return solicitudes.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

}
