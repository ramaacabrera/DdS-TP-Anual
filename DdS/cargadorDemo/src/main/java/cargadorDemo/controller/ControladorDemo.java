package cargadorDemo.controller;

import cargadorDemo.service.GetHechosService;
import cargadorDemo.dto.HechoDTO;
import cargadorDemo.dto.SolicitudDeEliminacionDTO;
import cargadorDemo.dto.SolicitudDeModificacionDTO;
import cargadorDemo.controller.Controlador;

import java.util.List;
import java.util.UUID;

public class ControladorDemo implements Controlador {
    GetHechosService service;

    public ControladorDemo(GetHechosService service) {
        this.service = service;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return service.obtenerHechos();
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
