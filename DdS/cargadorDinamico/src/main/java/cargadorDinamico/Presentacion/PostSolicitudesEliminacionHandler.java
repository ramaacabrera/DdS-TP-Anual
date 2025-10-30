package cargadorDinamico.Presentacion;

import cargadorDinamico.DinamicaDto.SolicitudEliminacion_D_DTO;
import cargadorDinamico.DinamicoRepositorio;
import cargadorDinamico.Dominio.HechosYColeccionesD.Hecho_D;
import cargadorDinamico.Dominio.Solicitudes.EstadoSolicitudEliminacion_D;
import cargadorDinamico.Dominio.Solicitudes.SolicitudDeEliminacion_D;
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

        System.out.println("Creando solicitud: " + bodyString);
        repositorio.guardarSolicitudEliminacion(entidad);
        context.status(201);
    }
}
