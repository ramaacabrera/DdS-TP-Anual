<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/GetSolicitudesEliminacionRepoHandler.java
package agregador.Handlers;

import utils.Persistencia.SolicitudEliminacionRepositorio;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
========
package Agregador.Handlers;

import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import Agregador.Solicitudes.SolicitudDeEliminacion;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/GetSolicitudesEliminacionRepoHandler.java
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetSolicitudesEliminacionRepoHandler implements Handler {

    private final SolicitudEliminacionRepositorio repositorio;

    public GetSolicitudesEliminacionRepoHandler(SolicitudEliminacionRepositorio solicitudes) { repositorio = solicitudes; }

    public void handle(@NotNull Context ctx) throws Exception {
        List<SolicitudDeEliminacion> solicitudes = repositorio.buscarTodas();
        ctx.json(solicitudes);
    }
}