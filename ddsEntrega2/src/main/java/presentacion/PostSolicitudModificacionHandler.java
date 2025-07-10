package presentacion;

import Persistencia.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.DTO.SolicitudDeModificacionDTO;
import org.example.agregador.Solicitudes.SolicitudDeModificacion;
import Persistencia.SolicitudModificacionRepositorio;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudModificacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public PostSolicitudModificacionHandler(DinamicoRepositorio dinamicoRepositorio) { repositorio = dinamicoRepositorio; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeModificacionDTO solicitud = context.bodyAsClass(SolicitudDeModificacionDTO.class);

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.guardarSolicitudModificacion(solicitud);

        context.status(201);
    }
}
