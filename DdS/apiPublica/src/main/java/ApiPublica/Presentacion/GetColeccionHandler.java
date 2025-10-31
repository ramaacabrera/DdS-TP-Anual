package ApiPublica.Presentacion;

import utils.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.Dominio.HechosYColecciones.Coleccion;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class GetColeccionHandler implements Handler {
    //private final ColeccionRepositorio repositorio;
    ObjectMapper mapper = new ObjectMapper();
    private final ColeccionRepositorio coleccionRepositorio;

    public GetColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

//    public GetColeccionHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}

    @Override
    public void handle(Context ctx) throws URISyntaxException, IOException, InterruptedException {
        String handle = ctx.pathParam("id");

        if(!handle.trim().isEmpty()) {
            Coleccion coleccion = coleccionRepositorio.buscarPorHandle(handle);

            ctx.status(200).json(coleccion);

        } else {
            ctx.status(404);
        }

    }
}
