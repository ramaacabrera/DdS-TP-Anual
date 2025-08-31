package ApiAdministrativa.Presentacion;

import Agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class GetColeccionesHandler implements Handler {
    //private final ColeccionRepositorio repositorio;
    ObjectMapper mapper = new ObjectMapper();

    //public GetColeccionesHandler(ColeccionRepositorio colecciones) { repositorio = colecciones; }

    public void handle(@NotNull Context ctx) throws Exception {
        //ctx.json(repositorio.obtenerTodas());

         /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<Coleccion> colecciones = mapper.readValue(response.body(), new TypeReference<>() {
        });

        ctx.status(200).json(colecciones);

        //    <<<<<<<<<-
    }

}
