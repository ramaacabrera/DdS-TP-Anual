package org.example.agregador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SolicitudModificacionRepositorio {
    private final List<SolicitudDeModificacion> solicitudes = new ArrayList<SolicitudDeModificacion>();

    public SolicitudModificacionRepositorio() {}

    private Optional<SolicitudDeModificacion> buscarSolicitudModificacion(){
        return solicitudes.stream().findFirst();
    }

    public void agregarSolicitudDeModificacion(SolicitudDeModificacion solicitud){
        solicitudes.add(solicitud);
    }

    public void eliminarSolicitudModificacion(SolicitudDeModificacion solicitud){
        solicitudes.remove(solicitud);
    }

    public void actualizarSolicitudModificacion(SolicitudDeModificacion solicitud){
        solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    }

}
