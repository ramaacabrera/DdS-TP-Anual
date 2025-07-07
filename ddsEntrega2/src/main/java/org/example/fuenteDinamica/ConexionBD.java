package org.example.fuenteDinamica;

import org.example.fuente.*;

import java.util.List;

public class ConexionBD extends Conexion{
    private final DinamicoRepositorio baseDeDatos;

    public ConexionBD(DinamicoRepositorio baseDeDatos) {
        this.baseDeDatos = baseDeDatos;
    }

    @Override
    public List<HechoDTO> obtenerHechos(){ return baseDeDatos.obtenerHechos();}

    public List<SolicitudDeModificacionDTO> obtenerSolicitudDeModificacion() {
        return baseDeDatos.obtenerSolicitudDeModificacion();
    }

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudDeEliminacion() {
        return baseDeDatos.obtenerSolicitudDeEliminacion();
    }
}