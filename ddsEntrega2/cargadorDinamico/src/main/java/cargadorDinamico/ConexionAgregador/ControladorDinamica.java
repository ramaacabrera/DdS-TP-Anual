package cargadorDinamico.ConexionAgregador;

import cargadorDinamico.Presentacion.GetHechosDinamicoHandler;
import cargadorDinamico.Presentacion.GetSolicitudesEliminacionHandler;
import cargadorDinamico.Presentacion.GetSolicitudesModificacionHandler;
import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;
import utils.Controladores.Controlador;

import java.util.List;
import java.util.UUID;

public class ControladorDinamica implements Controlador {
    GetHechosDinamicoHandler hechosHandler;
    GetSolicitudesModificacionHandler modificacionHandler;
    GetSolicitudesEliminacionHandler eliminacionHandler;

    public ControladorDinamica(GetHechosDinamicoHandler handlerNuevo, GetSolicitudesModificacionHandler modificacionHandlerNuevo, GetSolicitudesEliminacionHandler eliminacionHandlerNuevo) {
        this.hechosHandler = handlerNuevo;
        this.modificacionHandler = modificacionHandlerNuevo;
        this.eliminacionHandler = eliminacionHandlerNuevo;
    }

    @Override
    public List<HechoDTO> obtenerHechos() {
        return hechosHandler.obtenerHechos();
    }

    @Override
    public List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion() {
        return eliminacionHandler.obtenerSolicitudes();
    }

    @Override
    public List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion() {
        return modificacionHandler.obtenerSolicitudes();
    }

    @Override
    public void guardarId(UUID id) {

    }
}
