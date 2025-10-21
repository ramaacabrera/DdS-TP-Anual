<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/DeleteFuenteHandler.java
package agregador.Handlers;

import agregador.Cargador.ConexionCargador;
========
package Agregador.Handlers;

import Agregador.Cargador.ConexionCargador;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/DeleteFuenteHandler.java
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.io.IOException;
import java.net.URISyntaxException;
<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/DeleteFuenteHandler.java
import java.util.UUID;
========
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/DeleteFuenteHandler.java

public class DeleteFuenteHandler implements Handler {
    private final ConexionCargador conexionCargador;

    public DeleteFuenteHandler(ConexionCargador conexionCargadorNuevo) { conexionCargador = conexionCargadorNuevo; }

    @Override
    public void handle(Context ctx) throws URISyntaxException, IOException, InterruptedException {
        UUID handle = UUID.fromString(ctx.pathParam("id"));

        if(conexionCargador.borrarFuente(handle)){
            ctx.status(200).result("Colección borrada con éxito.");
        }
        else {
            //System.out.println("Coleccion no encontrada: " + jsonBody);
            ctx.status(404);
        }
    }
}