package cargadorDinamico.controller;

import cargadorDinamico.domain.DinamicaDto.SolicitudEliminacion_D_DTO;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudEliminacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeEliminacion_D;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import cargadorDinamico.service.SolicitudesEliminacionService;

public class PostSolicitudesEliminacionHandler implements Handler {
    private final SolicitudesEliminacionService service;

    public PostSolicitudesEliminacionHandler(SolicitudesEliminacionService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String bodyString = context.body();
        SolicitudEliminacion_D_DTO solicitudnueva = context.bodyAsClass(SolicitudEliminacion_D_DTO.class);

        System.out.println("Post Solicitud Eliminacion");
        System.out.println(solicitudnueva);

        SolicitudDeEliminacion_D entidad = new SolicitudDeEliminacion_D();
        entidad.setHecho_id(solicitudnueva.getID_hechoAsociado());
        entidad.setJustificacion(solicitudnueva.getJustificacion());
        entidad.setUsuario(solicitudnueva.getUsuario());
        entidad.setEstadoSolicitudEliminacion(EstadoSolicitudEliminacion_D.PENDIENTE);

        System.out.println(entidad);

        service.guardarSolicitudEliminacion(entidad);
        context.status(201);
    }
}
