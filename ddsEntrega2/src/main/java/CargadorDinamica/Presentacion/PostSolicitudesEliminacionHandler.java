package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicaDto.SolicitudEliminacion_D_DTO;
import CargadorDinamica.DinamicoRepositorio;
import CargadorDinamica.Dominio.Solicitudes.SolicitudDeEliminacion_D;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudesEliminacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public PostSolicitudesEliminacionHandler(DinamicoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudEliminacion_D_DTO solicitudnueva = context.bodyAsClass(SolicitudEliminacion_D_DTO.class);

        SolicitudDeEliminacion_D entidad = new SolicitudDeEliminacion_D(solicitudnueva);

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.guardarSolicitudEliminacion(entidad);
        context.status(201);
    }
}
