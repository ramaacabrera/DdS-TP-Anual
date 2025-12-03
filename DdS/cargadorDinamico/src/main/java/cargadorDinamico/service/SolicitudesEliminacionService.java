package cargadorDinamico.service;

import cargadorDinamico.domain.Solicitudes.SolicitudDeEliminacion_D;
import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeEliminacionDTO;

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
        System.out.println("Solicitudes Eliminacion Service");
        repositorio.guardarSolicitudEliminacion(entidad);
    }
}
