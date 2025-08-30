package ApiAdministrativa.Presentacion;

import Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import Agregador.Solicitudes.SolicitudDeEliminacion;

import java.util.Optional;

public class GetSolicitudEliminacionHandler implements Handler {
    private final SolicitudEliminacionRepositorio repositorio;

    public GetSolicitudEliminacionHandler(SolicitudEliminacionRepositorio repositorio){ this.repositorio = repositorio; }

    @Override
    public void handle(Context ctx){
        String handle = ctx.pathParam("id");

        final Optional<SolicitudDeEliminacion> resultadoBusqueda = repositorio.buscarPorId(handle);
        if (resultadoBusqueda.isPresent()) {
            ctx.status(200).json(resultadoBusqueda.get());
        } else {
            ctx.status(404);
        }
    }
}
