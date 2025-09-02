package CargadorDinamica.Presentacion;

import Agregador.Solicitudes.DetectorDeSpam;
import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.SolicitudDeModificacionDTO;

public class PostSolicitudesModificacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public PostSolicitudesModificacionHandler(DinamicoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeModificacionDTO solicitud = context.bodyAsClass(SolicitudDeModificacionDTO.class);
        if(!DetectorDeSpam.esSpam(solicitud.getJustificacion())) {
            System.out.println("Creando solicitud: " + bodyString);
            repositorio.guardarSolicitud(solicitud);
            context.status(201);
        }
        else{
            context.result("Solicitud de eliminacion no creada, la misma es spam");
            context.status(400);
        }
    }
}
