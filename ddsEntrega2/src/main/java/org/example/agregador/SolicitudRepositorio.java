package org.example.agregador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SolicitudRepositorio {
    private List<Solicitud> solicitudes = new ArrayList<Solicitud>();

    public SolicitudRepositorio() {}

    private Optional<SolicitudDeEliminacion> buscarSolicitudEliminacion(){
        return solicitudes.stream()
                .filter(SolicitudDeEliminacion.class::isInstance)
                .map(SolicitudDeEliminacion.class::cast)
                .findFirst();
    }

    private Optional<SolicitudDeModificacion> buscarSolicitudModificacion(){
        return solicitudes.stream()
                .filter(SolicitudDeModificacion.class::isInstance)
                .map(SolicitudDeModificacion.class::cast)
                .findFirst();
    }

    public void agregarSolicitud(Solicitud solicitud){
        solicitudes.add(solicitud);
    }

    public void eliminarSolicitud(Solicitud solicitud){
        solicitudes.remove(solicitud);
    }

    public void actualizarSolicitud(Solicitud solicitud){
        solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    }

}
