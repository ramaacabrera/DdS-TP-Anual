package cargadorDemo.controller;

import cargadorDemo.service.HechosService;
import cargadorDemo.dto.HechoDTO;
import cargadorDemo.controller.Controlador;

import java.util.List;
import java.util.UUID;

public class ControladorDemo implements Controlador {
    HechosService service;

    public ControladorDemo(HechosService service) {
        this.service = service;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return service.obtenerHechos();
    }

    @Override
    public void guardarId(UUID id) {

    }
}
