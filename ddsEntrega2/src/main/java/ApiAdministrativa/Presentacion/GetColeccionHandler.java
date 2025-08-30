package ApiAdministrativa.Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import Agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class GetColeccionHandler implements Handler {
    private final ColeccionRepositorio repositorio;
    ObjectMapper mapper = new ObjectMapper();
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
