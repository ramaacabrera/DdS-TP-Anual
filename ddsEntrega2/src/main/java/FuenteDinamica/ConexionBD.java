package FuenteDinamica;

import Persistencia.DinamicoRepositorio;
import Agregador.DTO.HechoDTO;
import Agregador.DTO.SolicitudDeEliminacionDTO;
import Agregador.DTO.SolicitudDeModificacionDTO;
import Agregador.Criterios.Criterio;
import Agregador.fuente.*;

import java.util.List;

public class ConexionBD extends Conexion{
    private final DinamicoRepositorio baseDeDatos;

    public ConexionBD(DinamicoRepositorio baseDeDatos) {
        this.baseDeDatos = baseDeDatos;
    }

    @Override
    public List<HechoDTO> obtenerHechos(List<Criterio> criterios){ return baseDeDatos.obtenerHechos();}

    public List<SolicitudDeModificacionDTO> obtenerSolicitudDeModificacion() {
        return baseDeDatos.obtenerSolicitudDeModificacion();
    }

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudDeEliminacion() {
        return baseDeDatos.obtenerSolicitudDeEliminacion();
    }
}