package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicaDto.SolicitudModificacion_D_DTO;
import CargadorDinamica.DinamicoRepositorio;
import CargadorDinamica.Dominio.Solicitudes.SolicitudDeModificacion_D;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudesModificacionHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public PostSolicitudesModificacionHandler(DinamicoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudModificacion_D_DTO solicitudNueva = context.bodyAsClass(SolicitudModificacion_D_DTO.class);

        SolicitudDeModificacion_D entidad = new SolicitudDeModificacion_D(solicitudNueva);

        repositorio.guardarSolicitudModificacion(entidad);
        context.status(201);
    }
}
