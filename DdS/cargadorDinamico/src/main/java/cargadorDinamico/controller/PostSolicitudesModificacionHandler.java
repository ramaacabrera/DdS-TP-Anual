package cargadorDinamico.controller;

import cargadorDinamico.domain.DinamicaDto.SolicitudModificacion_D_DTO;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import cargadorDinamico.service.SolicitudesModificacionService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class PostSolicitudesModificacionHandler implements Handler {
    private final SolicitudesModificacionService service;

    public PostSolicitudesModificacionHandler(SolicitudesModificacionService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        System.out.println("Proceso la solicitud de modificacion");

        SolicitudModificacion_D_DTO solicitudDto = context.bodyAsClass(SolicitudModificacion_D_DTO.class);

        SolicitudDeModificacion_D entidad = new SolicitudDeModificacion_D();

        entidad.setID_HechoAsociado(solicitudDto.getID_HechoAsociado());
        entidad.setJustificacion(solicitudDto.getJustificacion());
        entidad.setUsuario(solicitudDto.getUsuario());
        entidad.setEstadoSolicitudModificacion(EstadoSolicitudModificacion_D.PENDIENTE);

        entidad.setHechoModificado(solicitudDto.getHechoModificado());

        service.guardarSolicitudModificacion(entidad);
        context.status(201).result("Solicitud guardada");
    }
}