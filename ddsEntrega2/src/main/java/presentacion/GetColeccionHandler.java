package presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;

import java.util.Optional;

public class GetColeccionHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public GetColeccionHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) {
        String handle = ctx.pathParam("id");
        final Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
        if (resultadoBusqueda.isPresent()) {
            ctx.status(200).json(resultadoBusqueda.get());
        } else {
            ctx.status(404);
        }
    }
}
