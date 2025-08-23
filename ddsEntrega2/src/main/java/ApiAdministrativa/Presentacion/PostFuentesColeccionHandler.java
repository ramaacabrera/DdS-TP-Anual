package ApiAdministrativa.Presentacion;

import io.javalin.http.Handler;
import Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.fuente.Fuente;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PostFuentesColeccionHandler implements Handler{
    private final ColeccionRepositorio repositorio;

    public PostFuentesColeccionHandler(ColeccionRepositorio repositorioNuevo) { repositorio = repositorioNuevo; }

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
