package Agregador.Persistencia;

import Agregador.Solicitudes.SolicitudDeModificacion;
import utils.DTO.SolicitudDeModificacionDTO;

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

    public void agregarSolicitudDeModificacion(SolicitudDeModificacionDTO solicitud){
        solicitudes.add(new SolicitudDeModificacion(solicitud));
    }

    public void actualizarSolicitudModificacion(SolicitudDeModificacion solicitud){
        solicitudes.set(solicitudes.indexOf(solicitud), solicitud);
    }

}
