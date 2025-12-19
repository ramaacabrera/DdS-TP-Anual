package cargadorEstatico.controller;

import cargadorEstatico.conexionAgregador.Controlador;
import cargadorEstatico.dto.Hechos.HechoDTO;
import cargadorEstatico.service.HechosEstaticoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ControladorEstatica implements Controlador {
    HechosEstaticoService service;

    public ControladorEstatica(HechosEstaticoService handlerNuevo) {
        this.service = handlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return service.obtenerHechosDrive();
    }

    @Override
    public void guardarId(UUID id) {

    }

    @Override
    public List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion() {
        return List.of();
    }

    @Override
    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        return List.of();
    }
}
