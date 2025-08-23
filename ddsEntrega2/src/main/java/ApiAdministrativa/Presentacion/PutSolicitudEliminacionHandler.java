package ApiAdministrativa.Presentacion;

import Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class PutSolicitudEliminacionHandler implements Handler{
    private SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public PutSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorioNuevo) {solicitudEliminacionRepositorio = solicitudEliminacionRepositorioNuevo;}

    @Override
    public void handle(Context context) {
        String id = context.pathParam("id");
        String body = context.body();
        boolean resultado = solicitudEliminacionRepositorio.actualizarEstadoSolicitudEliminacion(body,id);
        if(resultado){
            context.status(200);
        } else {
            context.status(404);
        }
    }

}
