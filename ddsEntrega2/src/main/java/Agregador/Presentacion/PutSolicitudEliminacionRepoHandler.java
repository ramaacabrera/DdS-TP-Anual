package Agregador.Presentacion;

import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Handler;
import io.javalin.http.Context;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PutSolicitudEliminacionRepoHandler implements Handler {
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public PutSolicitudEliminacionRepoHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorioNuevo) {solicitudEliminacionRepositorio = solicitudEliminacionRepositorioNuevo;}

    @Override
    public void handle(Context context) {
        int id = Integer.parseInt(context.pathParam("id"));
        String body = context.body();
        boolean resultado = solicitudEliminacionRepositorio.actualizarEstadoSolicitudEliminacion(body,id);
        if(resultado){
            context.status(200);
        } else {
            context.status(404);
        }
    }
}
