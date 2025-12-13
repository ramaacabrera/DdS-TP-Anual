package cargadorEstatico.conexionAgregador;

import cargadorEstatico.dto.Hechos.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.List;
import java.util.UUID;

abstract public interface Controlador {

    public abstract List<HechoDTO> obtenerHechos();

    public abstract void guardarId(UUID id);


    List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion();

    List<SolicitudDeEliminacionDTO> obtenerSolicitudesEliminacion();
}
