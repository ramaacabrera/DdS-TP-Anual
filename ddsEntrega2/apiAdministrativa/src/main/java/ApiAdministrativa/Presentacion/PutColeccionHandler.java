package ApiAdministrativa.Presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import ApiAdministrativa.HechosYColecciones.Coleccion;
import ApiAdministrativa.Persistencia.ColeccionRepositorio;
import ApiAdministrativa.Criterios.Criterio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PutColeccionHandler implements Handler {

        private final ObjectMapper objectMapper = new ObjectMapper();
    private final ColeccionRepositorio coleccionRepositorio;

    public PutColeccionHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    @Override
        public void handle(Context ctx) throws Exception {
            String id = ctx.pathParam("id");

            // Tomo el body tal cual lo mandó el cliente
            String bodyJson = ctx.body();

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/colecciones/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            ctx.status(response.statusCode()).result(response.body());
        }



//    private final ColeccionRepositorio repositorio;
//
//    public PutColeccionHandler(ColeccionRepositorio coleccion) {repositorio = coleccion;}
//
//    @Override
//    public void handle(Context ctx) {
//        String handle = ctx.pathParam("id");
//        Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
//        if (resultadoBusqueda.isPresent()) {
//            ObjectMapper mapper = new ObjectMapper();
//            try {
//                JsonNode body = mapper.readTree((ctx.body()));
//                if(body.has("titulo")){
//                    resultadoBusqueda.get().setTitulo(body.get("titulo").asText());
//                }
//                if(body.has("descripcion")){
//                    resultadoBusqueda.get().setDescripcion(body.get("descripcion").asText());
//                }
//                if(body.has("criteriosDePertenencia")){
//                    List<Criterio> nuevosCriterios = mapper.readValue(body.get("criteriosDePertenencia")
//                                    .toString(), new TypeReference<List<Criterio>>(){});
//                    resultadoBusqueda.get().setCriteriosDePertenencia(nuevosCriterios);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            ctx.status(200);
//        } else{
//            ctx.status(404);
//        }
//    }
}
