package utils.Controladores;

import utils.DTO.HechoDTO;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.List;
import java.util.UUID;

abstract public class Controlador {

    public abstract List<HechoDTO> obtenerHechos();

    public abstract List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion();

    public abstract void guardarId(UUID id);

    public abstract List<SolicitudDeEliminacionDTO>  obtenerSolicitudesEliminacion();
}
