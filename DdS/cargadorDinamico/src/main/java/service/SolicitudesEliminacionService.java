package service;

import domain.Solicitudes.SolicitudDeEliminacion_D;
import repository.DinamicoRepositorio;
import domain.DinamicaDto.SolicitudDeEliminacionDTO;

import java.util.List;

public class SolicitudesEliminacionService {
    private final DinamicoRepositorio repositorio;

    public SolicitudesEliminacionService(DinamicoRepositorio repositorio){this.repositorio = repositorio;}

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudes(){
        List<SolicitudDeEliminacionDTO> solicitudes = repositorio.buscarSolicitudesEliminacion();
        repositorio.resetearSolicitudesEliminacion();
        return solicitudes;
    }

    public void guardarSolicitudEliminacion(SolicitudDeEliminacion_D entidad) {
        repositorio.guardarSolicitudEliminacion(entidad);
    }
}
