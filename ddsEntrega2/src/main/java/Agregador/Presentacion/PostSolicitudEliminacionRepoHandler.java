package Agregador.Presentacion;

import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import Agregador.Solicitudes.DetectorDeSpam;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.SolicitudDeEliminacionDTO;

public class PostSolicitudEliminacionRepoHandler implements Handler {
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public PostSolicitudEliminacionRepoHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeEliminacionDTO solicitud = context.bodyAsClass(SolicitudDeEliminacionDTO.class);
        if(!DetectorDeSpam.esSpam(solicitud.getJustificacion())) {
            System.out.println("Creando solicitud: " + bodyString);
            solicitudEliminacionRepositorio.agregarSolicitudDeEliminacion(new SolicitudDeEliminacion(solicitud));
            context.status(201);
        }
        else{
            context.result("Solicitud de eliminacion no creada, la misma es spam");
            context.status(400);
        }
    }
}
