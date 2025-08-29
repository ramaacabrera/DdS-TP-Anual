package ApiAdministrativa.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.DTO.SolicitudDeModificacionDTO;
import FuenteDinamica.ControllerSolicitud;
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
