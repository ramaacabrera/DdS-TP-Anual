package org.example.agregador.DTO;

import org.example.agregador.Solicitudes.EstadoSolicitudModificacion;
import org.example.agregador.HechosYColecciones.Hecho;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private Hecho hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;
}
