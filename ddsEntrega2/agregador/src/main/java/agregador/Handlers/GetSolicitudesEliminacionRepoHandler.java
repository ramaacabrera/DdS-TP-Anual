package agregador.Handlers;

import agregador.Persistencia.SolicitudEliminacionRepositorio;
import agregador.Solicitudes.SolicitudDeEliminacion;
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