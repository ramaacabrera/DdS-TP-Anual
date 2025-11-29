package cargadorDinamico.service;

import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import java.util.List;


public class SolicitudesModificacionService {
    private final DinamicoRepositorio repositorio;

    public SolicitudesModificacionService(DinamicoRepositorio repositorio){this.repositorio = repositorio;}

    public List<SolicitudDeModificacionDTO> obtenerSolicitudes(){
        List<SolicitudDeModificacionDTO> solicitudes = repositorio.buscarSolicitudesModificacion();
        repositorio.resetearSolicitudesEliminacion();
        return solicitudes;
    }

    public void guardarSolicitudModificacion(SolicitudDeModificacion_D entidad) {
        repositorio.guardarSolicitudModificacion(entidad);
    }
}
