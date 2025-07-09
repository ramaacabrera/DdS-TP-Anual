package org.example.fuenteDinamica;

import Persistencia.DinamicoRepositorio;
import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;

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
