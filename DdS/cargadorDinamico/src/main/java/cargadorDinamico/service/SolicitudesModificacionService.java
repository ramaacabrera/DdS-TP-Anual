package cargadorDinamico.service;

import cargadorDinamico.domain.DinamicaDto.Hecho_D_DTO;
import cargadorDinamico.domain.HechosYColecciones.Hecho;
import cargadorDinamico.domain.HechosYColeccionesD.Hecho_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.domain.DinamicaDto.SolicitudDeModificacionDTO;
import cargadorDinamico.repository.HechoRepositorio;

import java.util.List;
import java.util.UUID;


public class SolicitudesModificacionService {
    private final DinamicoRepositorio repositorio;
    private final HechoRepositorio hechoRepositorio;

    public SolicitudesModificacionService(DinamicoRepositorio repositorio, HechoRepositorio hechoRepositorio){
        this.repositorio = repositorio;
        this.hechoRepositorio = hechoRepositorio;
    }

    public List<SolicitudDeModificacionDTO> obtenerSolicitudes(){
        List<SolicitudDeModificacionDTO> solicitudes = repositorio.buscarSolicitudesModificacion();
        repositorio.resetearSolicitudesEliminacion();
        return solicitudes;
    }

    public void aplicarModificacion(SolicitudDeModificacion_D sol){

        UUID id = sol.getID_HechoAsociado();

        Hecho original = hechoRepositorio.buscarPorId(id);
        Hecho_D modificado = sol.getHechoModificado();

        original.setTitulo(modificado.getTitulo());
        original.setDescripcion(modificado.getDescripcion());


        hechoRepositorio.actualizar(original);
    }


    public void guardarSolicitudModificacion(SolicitudDeModificacion_D entidad) {
        repositorio.guardarSolicitudModificacion(entidad);
    }
}
