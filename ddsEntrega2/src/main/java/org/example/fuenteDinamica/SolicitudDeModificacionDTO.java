package org.example.fuenteDinamica;

import org.example.agregador.EstadoSolicitudModificacion;
import org.example.agregador.Hecho;

public class SolicitudDeModificacionDTO extends SolicitudDTO {
    private Hecho hechoModificado;
    private EstadoSolicitudModificacion estadoSolicitudModificacion;
}
