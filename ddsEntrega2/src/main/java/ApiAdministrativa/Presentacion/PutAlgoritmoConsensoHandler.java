package ApiAdministrativa.Presentacion;

import Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import Agregador.HechosYColecciones.Coleccion;
import Agregador.HechosYColecciones.TipoAlgoritmoConsenso;

import java.util.Optional;

public class PutAlgoritmoConsensoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PutAlgoritmoConsensoHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) {
        String handle = ctx.pathParam("id");
        TipoAlgoritmoConsenso algoritmo= ctx.bodyAsClass(TipoAlgoritmoConsenso.class);
        final Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if (resultadoBusqueda.isPresent()) {
            resultadoBusqueda.get().setAlgoritmoDeConsenso(algoritmo);
            repositorio.actualizar(resultadoBusqueda.get());
            ctx.status(200);
        } else{
            ctx.status(404);
        }
    }
}
