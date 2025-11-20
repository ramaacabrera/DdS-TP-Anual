package controller;

import domain.DinamicaDto.SolicitudEliminacion_D_DTO;
import repository.DinamicoRepositorio;
import domain.Solicitudes.EstadoSolicitudEliminacion_D;
import domain.Solicitudes.SolicitudDeEliminacion_D;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.SolicitudesEliminacionService;

public class PostSolicitudesEliminacionHandler implements Handler {
    private final SolicitudesEliminacionService service;

    public PostSolicitudesEliminacionHandler(SolicitudesEliminacionService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudEliminacion_D_DTO solicitudnueva = context.bodyAsClass(SolicitudEliminacion_D_DTO.class);

        /*
        Hecho_D hechoExistente = repositorio.buscarHechoPorId(solicitudnueva.getHechoAsociado());

        if (hechoExistente == null) {
            context.status(404).result("Hecho no encontrado");
            return;
        }*/

        SolicitudDeEliminacion_D entidad = new SolicitudDeEliminacion_D();
        //entidad.setHechoAsociado(hechoExistente);
        entidad.setHecho_id(solicitudnueva.getID_hechoAsociado());
        entidad.setJustificacion(solicitudnueva.getJustificacion());
        entidad.setUsuario(solicitudnueva.getUsuario());
        entidad.setEstadoSolicitudEliminacion(EstadoSolicitudEliminacion_D.PENDIENTE);

        service.guardarSolicitudEliminacion(entidad);
        context.status(201);
    }
}
