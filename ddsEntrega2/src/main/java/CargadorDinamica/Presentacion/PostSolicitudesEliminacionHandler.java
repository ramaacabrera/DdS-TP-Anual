package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.SolicitudDeEliminacionDTO;

public class PostSolicitudesEliminacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public PostSolicitudesEliminacionHandler(DinamicoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudDeEliminacionDTO solicitud = context.bodyAsClass(SolicitudDeEliminacionDTO.class);

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.guardarSolicitudEliminacion(solicitud);
        context.status(201);
    }
}
