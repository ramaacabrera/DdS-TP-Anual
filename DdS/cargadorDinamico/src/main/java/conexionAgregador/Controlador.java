package conexionAgregador;

import domain.DinamicaDto.HechoDTO;
import domain.DinamicaDto.SolicitudDeEliminacionDTO;
import domain.DinamicaDto.SolicitudDeModificacionDTO;

import java.util.List;
import java.util.UUID;

abstract public interface Controlador {

    public abstract List<HechoDTO> obtenerHechos();

    public abstract List<SolicitudDeModificacionDTO> obtenerSolicitudesModificacion();

    public abstract void guardarId(UUID id);

    public abstract List<SolicitudDeEliminacionDTO>  obtenerSolicitudesEliminacion();
}
