package cargadorEstatico.controller;

import cargadorEstatico.conexionAgregador.Controlador;
import cargadorEstatico.dto.HechoDTO;
import cargadorEstatico.service.HechosEstaticoService;

import java.util.List;
import java.util.UUID;

public class ControladorEstatica implements Controlador {
    HechosEstaticoService service;

    public ControladorEstatica(HechosEstaticoService handlerNuevo) {
        this.service = handlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return service.obtenerHechos();
    }

    @Override
    public void guardarId(UUID id) {

    }
}
