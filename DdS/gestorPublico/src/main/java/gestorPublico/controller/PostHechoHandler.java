package gestorPublico.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostHechoHandler implements Handler {
    private final int puertoDinamica;

    public PostHechoHandler(int puertoDinamicaNuevo) {puertoDinamica = puertoDinamicaNuevo;}

    @Override
    public void handle(Context context) throws Exception {
        String bodyJson = context.body(); // request JSON completo

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + puertoDinamica + "/hechos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        context.status(response.statusCode()).result(response.body());
    }

//    private final ControllerSubirHechos controller;
//
//    public PostHechoHandler(ControllerSubirHechos controllerNuevo) { controller = controllerNuevo; }
//
//    @Override
//    public void handle(@NotNull Context context) throws Exception {
//        String bodyString = context.body();
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> map = mapper.readValue(bodyString, new TypeReference<>() {});
//
//        String hechoJson = mapper.writeValueAsString(map.get("hecho"));
//        String contribuyenteJson = mapper.writeValueAsString(map.get("contribuyente"));
//
//        HechoDTO hecho = mapper.readValue(hechoJson, HechoDTO.class);
//        Contribuyente contribuyente = null;
//
//        if (map.containsKey("contribuyente") && map.get("contribuyente") != null) {
//            contribuyente = mapper.readValue(contribuyenteJson, Contribuyente.class);
//        }
//
//        if (contribuyente != null) {
//            controller.subirHecho(hecho, contribuyente);
//        } else {
//            controller.subirHecho(hecho);
//        }
//
//        System.out.println("Creando hecho: " + bodyString);
//
//        context.status(201);
//    }
}
