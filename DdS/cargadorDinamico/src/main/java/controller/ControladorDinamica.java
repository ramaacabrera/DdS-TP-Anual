package controller;

import conexionAgregador.Controlador;
import service.HechosDinamicoService;
import service.SolicitudesEliminacionService;
import service.SolicitudesModificacionService;
import domain.DinamicaDto.SolicitudDeEliminacionDTO;
import domain.DinamicaDto.SolicitudDeModificacionDTO;
import domain.DinamicaDto.HechoDTO;

import java.util.List;
import java.util.UUID;

public class ControladorDinamica implements Controlador {
    HechosDinamicoService hechosService;
    SolicitudesModificacionService modificacionService;
    SolicitudesEliminacionService eliminacionService;

    public ControladorDinamica(HechosDinamicoService handlerNuevo, SolicitudesModificacionService modificacionHandlerNuevo, SolicitudesEliminacionService eliminacionHandlerNuevo) {
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
