package presentacion;

import Persistencia.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;
import org.example.agregador.Solicitudes.SolicitudDeModificacion;
import Persistencia.SolicitudModificacionRepositorio;
import org.example.fuenteDinamica.ControllerSolicitud;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudModificacionHandler implements Handler {
    private final ControllerSolicitud controllerSolicitud;

    public PostSolicitudModificacionHandler(ControllerSolicitud controllerSolicitudNuevo) { controllerSolicitud = controllerSolicitudNuevo; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeModificacionDTO solicitud = context.bodyAsClass(SolicitudDeModificacionDTO.class);

        System.out.println("Creando solicitud: " + bodyString);
        controllerSolicitud.subirSolicitudModificacion(solicitud);

        context.status(201);
    }
}
