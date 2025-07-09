package org.example.fuenteDinamica;

import Persistencia.DinamicoRepositorio;
import org.example.agregador.DTO.HechoDTO;
import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;
import org.example.agregador.Criterios.Criterio;
import org.example.agregador.fuente.*;

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