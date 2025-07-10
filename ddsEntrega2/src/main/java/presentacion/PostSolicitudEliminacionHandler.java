package presentacion;

import Persistencia.DinamicoRepositorio;
import Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.DTO.SolicitudDeEliminacionDTO;
import org.example.agregador.Solicitudes.SolicitudDeEliminacion;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudEliminacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public PostSolicitudEliminacionHandler(DinamicoRepositorio dinamicoRepositorio) { repositorio = dinamicoRepositorio; }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeEliminacionDTO solicitud = context.bodyAsClass(SolicitudDeEliminacionDTO.class);

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.guardarSolicitudEliminacion(solicitud);

        context.status(201);
    }
}
