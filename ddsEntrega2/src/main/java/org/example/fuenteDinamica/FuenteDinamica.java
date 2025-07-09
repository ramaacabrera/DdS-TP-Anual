package org.example.fuenteDinamica;

import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;
import org.example.agregador.fuente.TipoDeFuente;
import org.example.agregador.fuente.*;

import java.util.List;

public class FuenteDinamica extends Fuente {

    public FuenteDinamica(Conexion conexion) { super(TipoDeFuente.DINAMICA, conexion); }

    public List<SolicitudDeModificacionDTO> obtenerSolicitudDeModificacion() {
        return ((ConexionBD) conexion).obtenerSolicitudDeModificacion();
    }

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudDeEliminacion() {
        return ((ConexionBD) conexion).obtenerSolicitudDeEliminacion();
    }
}
