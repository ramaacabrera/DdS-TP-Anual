package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.DTO.SolicitudDeEliminacionDTO;
import utils.DTO.SolicitudDeModificacionDTO;

import java.util.List;

public class GetSolicitudesEliminacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public GetSolicitudesEliminacionHandler(DinamicoRepositorio repositorio){this.repositorio = repositorio;}

    @Override
    public void handle(Context contexto){
        List<SolicitudDeEliminacionDTO> solicitudes = repositorio.buscarSolicitudesEliminacion();
        repositorio.resetearSolicitudesEliminacion();
        contexto.json(solicitudes);
        contexto.status(200);
    }
}
