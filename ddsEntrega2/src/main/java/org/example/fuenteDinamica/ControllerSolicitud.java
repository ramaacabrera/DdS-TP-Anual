package org.example.fuenteDinamica;

import Persistencia.DinamicoRepositorio;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;
import org.example.agregador.Solicitudes.EstadoSolicitudEliminacion;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;

import java.util.Optional;

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

    public boolean actualizarSolicitudEliminacion(String estado, String id){
        Optional<SolicitudDeEliminacion> resultadoBusqueda = baseDeDatos.buscarPorId(id);
        if(resultadoBusqueda.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            EstadoSolicitudEliminacion estadoEnum = EstadoSolicitudEliminacion.valueOf(estado.toUpperCase());
            if(estadoEnum == EstadoSolicitudEliminacion.ACEPTADA){
                resultadoBusqueda.get().aceptarSolicitud();
            } else if(estadoEnum == EstadoSolicitudEliminacion.RECHAZADA){
                resultadoBusqueda.get().rechazarSolicitud();
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
