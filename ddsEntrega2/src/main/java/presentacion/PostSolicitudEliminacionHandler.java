package presentacion;

import Persistencia.DinamicoRepositorio;
import Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;
import org.example.fuenteDinamica.ControllerSolicitud;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudEliminacionHandler implements Handler {
    private final ControllerSolicitud controllerSolicitud;

    public PostSolicitudEliminacionHandler(ControllerSolicitud controllerSolicitudNuevo) { controllerSolicitud = controllerSolicitudNuevo; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeEliminacionDTO solicitud = context.bodyAsClass(SolicitudDeEliminacionDTO.class);

        System.out.println("Creando solicitud: " + bodyString);
        controllerSolicitud.subirSolicitudEliminacion(solicitud);

        context.status(201);
    }
}
