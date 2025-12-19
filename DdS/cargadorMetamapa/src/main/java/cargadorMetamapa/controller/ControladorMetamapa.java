package cargadorMetamapa.controller;

import cargadorMetamapa.service.HechosService;
import utils.Controladores.Controlador;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.List;
import java.util.UUID;

public class ControladorMetamapa implements Controlador {
    HechosService service;

    public ControladorMetamapa(HechosService service) {
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