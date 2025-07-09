package presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.SolicitudDeModificacion;
import org.example.agregador.SolicitudModificacionRepositorio;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudModificacionHandler implements Handler {
    private final SolicitudModificacionRepositorio repositorio;

    public PostSolicitudModificacionHandler(SolicitudModificacionRepositorio solicitudRepositorio) { repositorio = solicitudRepositorio; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeModificacion solicitud = context.bodyAsClass(SolicitudDeModificacion.class);

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.agregarSolicitudDeModificacion(solicitud);

        context.status(201);
    }
}
