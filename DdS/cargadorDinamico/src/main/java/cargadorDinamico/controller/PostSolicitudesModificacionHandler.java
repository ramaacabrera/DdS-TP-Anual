package cargadorDinamico.controller;

import cargadorDinamico.domain.DinamicaDto.SolicitudModificacion_D_DTO;
import cargadorDinamico.domain.Solicitudes.EstadoSolicitudModificacion_D;
import cargadorDinamico.domain.Solicitudes.SolicitudDeModificacion_D;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import cargadorDinamico.service.SolicitudesModificacionService;

public class PostSolicitudesModificacionHandler implements Handler {
    private final SolicitudesModificacionService service;

    public PostSolicitudesModificacionHandler(SolicitudesModificacionService service) {
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        System.out.println("Proceso la solicitud de modificacion");
        String bodyString = context.body();
        SolicitudModificacion_D_DTO solicitudNueva = context.bodyAsClass(SolicitudModificacion_D_DTO.class);

        /*Hecho_D hechoExistente = repositorio.buscarHechoPorId(solicitudNueva.getID_HechoAsociado());
        if (hechoExistente == null) {
            context.status(404).result("Hecho no encontrado");
            return;
        }*/

        SolicitudDeModificacion_D entidad = new SolicitudDeModificacion_D();
        entidad.setID_HechoAsociado(solicitudNueva.getID_HechoAsociado());
        entidad.setJustificacion(solicitudNueva.getJustificacion());
        entidad.setUsuario(solicitudNueva.getUsuario());
        entidad.setEstadoSolicitudModificacion(EstadoSolicitudModificacion_D.PENDIENTE);
        entidad.setHechoModificado(solicitudNueva.getHechoModificado());

        System.out.println("ID del usuario: " + entidad.getUsuario().getId_usuario().toString());

        service.guardarSolicitudModificacion(entidad);
        context.status(201);
    }
}
