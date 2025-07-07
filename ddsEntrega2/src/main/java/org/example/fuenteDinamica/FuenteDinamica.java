package org.example.fuenteDinamica;

import org.example.agregador.TipoDeFuente;
import org.example.fuente.*;

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
