package cargadorDinamico.Presentacion;

import CargadorDinamica.DinamicaDto.SolicitudModificacion_D_DTO;
import cargadorDinamico.DinamicoRepositorio;
import CargadorDinamica.Dominio.HechosYColecciones.Hecho_D;
import CargadorDinamica.Dominio.Solicitudes.EstadoSolicitudModificacion_D;
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

        Hecho_D hechoExistente = repositorio.buscarHechoPorId(solicitudNueva.getID_HechoAsociado());
        if (hechoExistente == null) {
            context.status(404).result("Hecho no encontrado");
            return;
        }

        SolicitudDeModificacion_D entidad = new SolicitudDeModificacion_D();
        entidad.setHechoAsociado(hechoExistente);
        entidad.setJustificacion(solicitudNueva.getJustificacion());
        entidad.setUsuario(solicitudNueva.getUsuario());
        entidad.setEstadoSolicitudModificacion(EstadoSolicitudModificacion_D.PENDIENTE);
        entidad.setHechoAsociado(solicitudNueva.getHechoModificado());

        repositorio.guardarSolicitudModificacion(entidad);
        context.status(201);
    }
}
