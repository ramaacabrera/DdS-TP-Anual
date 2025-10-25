package ApiAdministrativa.Presentacion;

import ApiAdministrativa.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import ApiAdministrativa.HechosYColecciones.TipoAlgoritmoConsenso;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PutAlgoritmoConsensoHandler implements Handler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ColeccionRepositorio coleccionRepositorio;

    public PutAlgoritmoConsensoHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String id = ctx.pathParam("id");

        // Recibo el algoritmo desde el body
        TipoAlgoritmoConsenso algoritmo = ctx.bodyAsClass(TipoAlgoritmoConsenso.class);

        // Serializo a JSON
        String algoritmoJson = objectMapper.writeValueAsString(algoritmo);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/colecciones/" + id + "/algoritmo"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(algoritmoJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ctx.status(response.statusCode()).result(response.body());
    }


//    private final ColeccionRepositorio repositorio;
//
//    public PutAlgoritmoConsensoHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}
//
//    @Override
//    public void handle(Context ctx) {
//        String handle = ctx.pathParam("id");
//        TipoAlgoritmoConsenso algoritmo= ctx.bodyAsClass(TipoAlgoritmoConsenso.class);
//        final Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
//        if (resultadoBusqueda.isPresent()) {
//            resultadoBusqueda.get().setAlgoritmoDeConsenso(algoritmo);
//            repositorio.actualizar(resultadoBusqueda.get());
//            ctx.status(200);
//        } else{
//            ctx.status(404);
//        }
//    }
}
