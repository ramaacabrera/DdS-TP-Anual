package cargadorEstatico.ConexionAgregador;

import cargadorEstatico.GetHechosEstaticoHandler;
import utils.Controladores.Controlador;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.List;
import java.util.UUID;

public class ControladorEstatica implements Controlador {
    GetHechosEstaticoHandler handler;

    public ControladorEstatica(GetHechosEstaticoHandler handlerNuevo) {
        this.handler = handlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return handler.obtenerHechos();
    }

    @Override
    public List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion() {
        return null;
    }


    @Override
    public void guardarId(UUID id) {

    }

    @Override
    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        return List.of();
    }
}
