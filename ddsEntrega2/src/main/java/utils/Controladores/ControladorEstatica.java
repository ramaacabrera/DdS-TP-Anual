package utils.Controladores;

import CargadorEstatica.GetHechosEstaticoHandler;
import utils.DTO.HechoDTO;

import java.util.List;
import java.util.UUID;

public class ControladorEstatica extends Controlador {
    GetHechosEstaticoHandler handler;

    public ControladorEstatica(GetHechosEstaticoHandler handlerNuevo) {
        this.handler = handlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return handler.obtenerHechos();
    }

    @Override
    public void obtenerSolicitudes() {

    }

    @Override
    public void guardarId(UUID id) {

    }
}
