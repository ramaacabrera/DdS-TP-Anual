package cargadorDinamico.Presentacion;

import cargadorDinamico.DinamicaDto.SolicitudModificacion_D_DTO;
import cargadorDinamico.DinamicoRepositorio;
import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
import cargadorDinamico.Dominio.Solicitudes.EstadoSolicitudModificacion_D;
import cargadorDinamico.Dominio.Solicitudes.SolicitudDeModificacion_D;
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

        repositorio.guardarSolicitudModificacion(entidad);
        context.status(201);
    }
}
