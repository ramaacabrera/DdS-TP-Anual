<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/PutSolicitudEliminacionRepoHandler.java
package agregador.Handlers;
========
package Agregador.Handlers;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/PutSolicitudEliminacionRepoHandler.java

import utils.Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import java.util.UUID;

public class PutSolicitudEliminacionRepoHandler implements Handler {
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public PutSolicitudEliminacionRepoHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorioNuevo) {solicitudEliminacionRepositorio = solicitudEliminacionRepositorioNuevo;}

    @Override
    public void handle(Context context) {
        /*int id = Integer.parseInt(context.pathParam("id"));
        String body = context.body();
        boolean resultado = solicitudEliminacionRepositorio.actualizarEstadoSolicitudEliminacion(body,id);
        if(resultado){
            context.status(200);
        } else {
            context.status(404);
        }*/
        String idString = context.pathParam("id");
        try{
            UUID id = UUID.fromString(idString);
            String body = context.body();
            boolean resultado = solicitudEliminacionRepositorio.actualizarEstadoSolicitudEliminacion(body, id);
            if (resultado) {
                context.status(200);
            } else {
                context.status(404);
            }
        } catch (IllegalArgumentException e) {
            context.status(400); // Bad Request
            context.result("El ID proporcionado no es un UUID válido.");
        }
    }
}
