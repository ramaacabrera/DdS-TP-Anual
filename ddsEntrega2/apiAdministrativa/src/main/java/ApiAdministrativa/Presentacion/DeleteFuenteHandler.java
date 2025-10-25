package ApiAdministrativa.Presentacion;

import ApiAdministrativa.fuente.Fuente;
import ApiAdministrativa.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteFuenteHandler implements Handler {
    //private final ColeccionRepositorio repositorio;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ColeccionRepositorio coleccionRepositorio;

    public DeleteFuenteHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    //public DeleteFuenteHandler(ColeccionRepositorio repositorio) {this.repositorio = repositorio;}

    @Override
    public void handle(Context context) throws IOException, URISyntaxException, InterruptedException {
        String handle = context.pathParam("id");
//        Fuente agregador.fuente = context.bodyAsClass(Fuente.class);
//
//        String fuenteJson = objectMapper.writeValueAsString(agregador.fuente);

        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones/" + handle + "/fuente"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(context.body()))//ofString(fuenteJson)) // DELETE con body
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        context.status(response.statusCode()).result(response.body());

        //    <<<<<<<<<-

//        Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
//        if(resultadoBusqueda.isPresent()){
//            resultadoBusqueda.get().eliminarFuente(context.bodyAsClass(Fuente.class));
//            context.status(200);
//        } else {
//            context.status(404);
//        }
    }

}
