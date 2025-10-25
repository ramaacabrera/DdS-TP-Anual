package agregador.Handlers;

import agregador.HechosYColecciones.Coleccion;
import agregador.Persistencia.ColeccionRepositorio;
import agregador.fuente.Fuente;
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
