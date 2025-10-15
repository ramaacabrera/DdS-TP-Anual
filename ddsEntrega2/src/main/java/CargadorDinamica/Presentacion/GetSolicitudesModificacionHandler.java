package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.DTO.SolicitudDeModificacionDTO;
import java.util.List;


public class GetSolicitudesModificacionHandler implements Handler{
    private final DinamicoRepositorio repositorio;

    public GetSolicitudesModificacionHandler(DinamicoRepositorio repositorio){this.repositorio = repositorio;}

    @Override
    public void handle(Context contexto){
        //List<SolicitudDeModificacionDTO> solicitudes = repositorio.buscarSolicitudesModificacion();
        repositorio.resetearSolicitudesModificacion();
        //contexto.json(solicitudes);
        contexto.status(200);
    }

}
