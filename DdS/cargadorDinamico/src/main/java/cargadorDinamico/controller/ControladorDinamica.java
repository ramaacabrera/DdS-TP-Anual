package cargadorDinamico.controller;

import cargadorDinamico.conexionAgregador.Controlador;
import cargadorDinamico.domain.DinamicaDto.SolicitudModificacionSalienteDTO;
import cargadorDinamico.service.HechosDinamicoService;
import cargadorDinamico.service.SolicitudesEliminacionService;
import cargadorDinamico.service.SolicitudesModificacionService;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeEliminacionDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.domain.DinamicaDto.HechoDTO;

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
    public List<SolicitudModificacionSalienteDTO> obtenerSolicitudesModificacion() {
        return modificacionService.obtenerSolicitudes();
    }

    @Override
    public void guardarId(UUID id) {

    }
}
