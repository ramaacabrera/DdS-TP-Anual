<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/GetColeccionEspecificaRepoHandler.java
package agregador.Handlers;
========
package Agregador.Handlers;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/GetColeccionEspecificaRepoHandler.java

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GetColeccionEspecificaRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public GetColeccionEspecificaRepoHandler(ColeccionRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) {
        String handle = ctx.pathParam("id");
        Optional<Coleccion> coleccionOpt = repositorio.buscarPorHandle(handle);

        if (!coleccionOpt.isPresent()) {
            ctx.status(404);
        } else {
            ctx.json(coleccionOpt.get());
        }
    }
}
