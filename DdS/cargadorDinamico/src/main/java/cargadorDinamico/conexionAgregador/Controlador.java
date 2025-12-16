package cargadorDinamico.conexionAgregador;

import cargadorDinamico.domain.DinamicaDto.HechoDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeEliminacionDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.domain.DinamicaDto.SolicitudModificacionSalienteDTO;

import java.util.List;
import java.util.UUID;

abstract public interface Controlador {

    public abstract List<HechoDTO> obtenerHechos();

    public abstract List<SolicitudModificacionSalienteDTO> obtenerSolicitudesModificacion();

    public abstract void guardarId(UUID id);

    public abstract List<SolicitudDeEliminacionDTO>  obtenerSolicitudesEliminacion();
}
