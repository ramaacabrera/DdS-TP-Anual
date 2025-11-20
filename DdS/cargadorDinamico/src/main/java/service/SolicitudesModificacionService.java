package service;

import domain.Solicitudes.SolicitudDeModificacion_D;
import repository.DinamicoRepositorio;
import domain.DinamicaDto.SolicitudDeModificacionDTO;
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
