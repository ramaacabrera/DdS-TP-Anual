package ApiAdministrativa.Presentacion;

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
        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones/" + handle))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200) {
            Coleccion coleccion = mapper.readValue(response.body(), new TypeReference<>() {
            });

            ctx.status(200).json(coleccion);
        }
        else{
            ctx.status(404).result("No se encontro el registro");
        }
        //    <<<<<<<<<-

//        final Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
//        if (resultadoBusqueda.isPresent()) {
//            ctx.status(200).json(resultadoBusqueda.get());
//        } else {
//            ctx.status(404);
//        }
    }
}
