package FuenteDinamica;

import Agregador.DTO.SolicitudDeEliminacionDTO;
import Agregador.DTO.SolicitudDeModificacionDTO;
import Agregador.fuente.TipoDeFuente;
import Agregador.fuente.*;

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
