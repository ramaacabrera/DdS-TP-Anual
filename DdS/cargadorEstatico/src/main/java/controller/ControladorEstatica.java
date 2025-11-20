package controller;

import conexionAgregador.Controlador;
import domain.DTO.HechoDTO;
import service.HechosEstaticoService;

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
