package service;

import repository.DinamicoRepositorio;
import utils.DTO.SolicitudDeModificacionDTO;
import java.util.List;


public class GetSolicitudesModificacionService{
    private final DinamicoRepositorio repositorio;

    public GetSolicitudesModificacionService(DinamicoRepositorio repositorio){this.repositorio = repositorio;}

    public List<SolicitudDeModificacionDTO> obtenerSolicitudes(){
        List<SolicitudDeModificacionDTO> solicitudes = repositorio.buscarSolicitudesModificacion();
        repositorio.resetearSolicitudesEliminacion();
        return solicitudes;
    }
}
