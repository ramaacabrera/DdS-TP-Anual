package cargadorDemo.ConexionAgregador;

import cargadorDemo.controller.GetHechosHandler;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.Controladores.Controlador;

import java.util.List;
import java.util.UUID;

public class ControladorDemo implements Controlador {
    GetHechosHandler handler;

    public ControladorDemo(GetHechosHandler handlerNuevo) {
        this.handler = handlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return handler.obtenerHechos();
    }

    @Override
    public List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion() {
        return List.of();
    }


    @Override
    public void guardarId(UUID id) {

    }

    @Override
    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        return List.of();
    }
}
