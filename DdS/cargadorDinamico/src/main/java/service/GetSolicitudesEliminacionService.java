package service;

import repository.DinamicoRepositorio;
import utils.DTO.SolicitudDeEliminacionDTO;

import java.util.List;

public class GetSolicitudesEliminacionService {
    private final DinamicoRepositorio repositorio;

    public GetSolicitudesEliminacionService(DinamicoRepositorio repositorio){this.repositorio = repositorio;}

    public List<SolicitudDeEliminacionDTO> obtenerSolicitudes(){
        List<SolicitudDeEliminacionDTO> solicitudes = repositorio.buscarSolicitudesEliminacion();
        repositorio.resetearSolicitudesEliminacion();
        return solicitudes;
    }
}
