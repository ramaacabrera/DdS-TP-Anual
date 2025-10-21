<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/PostFuentesColeccionRepoHandler.java
package agregador.Handlers;
========
package Agregador.Handlers;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/PostFuentesColeccionRepoHandler.java

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import utils.Dominio.fuente.Fuente;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PostFuentesColeccionRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PostFuentesColeccionRepoHandler(ColeccionRepositorio repositorioNuevo) { repositorio = repositorioNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id");
        Fuente fuente= ctx.bodyAsClass(Fuente.class);
        Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if (resultadoBusqueda.isPresent()) {
            resultadoBusqueda.get().agregarFuente(fuente);
            ctx.status(200);
        } else{
            ctx.status(404);
        }
    }
}
