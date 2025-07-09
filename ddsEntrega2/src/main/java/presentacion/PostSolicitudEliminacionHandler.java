package presentacion;

import org.example.agregador.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.SolicitudDeEliminacion;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudEliminacionHandler implements Handler {
    private final SolicitudEliminacionRepositorio repositorio;

    public PostSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudRepositorio) { repositorio = solicitudRepositorio; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeEliminacion solicitud = context.bodyAsClass(SolicitudDeEliminacion.class);

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.agregarSolicitudDeEliminacion(solicitud);

        context.status(201);
    }
}
