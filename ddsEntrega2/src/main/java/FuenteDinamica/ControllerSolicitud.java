package FuenteDinamica;

import Persistencia.DinamicoRepositorio;
import Agregador.DTO.SolicitudDeEliminacionDTO;
import Agregador.DTO.SolicitudDeModificacionDTO;

public class ControllerSolicitud {
    private final DinamicoRepositorio baseDeDatos;

    public ControllerSolicitud(DinamicoRepositorio baseDeDatos){
        this.baseDeDatos = baseDeDatos;
    }

    public void subirSolicitudModificacion(SolicitudDeModificacionDTO solicitud){
        baseDeDatos.guardarSolicitudModificacion(solicitud);
    }

    public void subirSolicitudEliminacion(SolicitudDeEliminacionDTO solicitud){
        baseDeDatos.guardarSolicitudEliminacion(solicitud);
    }
}
