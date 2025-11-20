package controller;

import service.GetHechosDinamicoService;
import service.GetSolicitudesEliminacionService;
import service.GetSolicitudesModificacionService;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.Controladores.Controlador;

import java.util.List;
import java.util.UUID;

public class ControladorDinamica implements Controlador {
    GetHechosDinamicoService hechosService;
    GetSolicitudesModificacionService modificacionService;
    GetSolicitudesEliminacionService eliminacionService;

    public ControladorDinamica(GetHechosDinamicoService handlerNuevo, GetSolicitudesModificacionService modificacionHandlerNuevo, GetSolicitudesEliminacionService eliminacionHandlerNuevo) {
        this.hechosService = handlerNuevo;
        this.modificacionService = modificacionHandlerNuevo;
        this.eliminacionService = eliminacionHandlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return hechosService.obtenerHechos();
    }

    @Override
    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        return eliminacionService.obtenerSolicitudes();
    }

    @Override
    public List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion() {
        return modificacionService.obtenerSolicitudes();
    }

    @Override
    public void guardarId(UUID id) {

    }
}
