package ApiPublica.Presentacion;

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

        try {
            System.out.println("=== INICIANDO POST /api/hechos ===");
            String bodyJson = context.body();
            System.out.println("Body recibido: " + bodyJson);

            // Verificar que el body no esté vacío
            if (bodyJson == null || bodyJson.trim().isEmpty()) {
                System.err.println("ERROR: Body vacío");
                context.status(400).result("Body vacío");
                return;
            }

            System.out.println("Llamando al servicio dinámico en puerto: " + puertoDinamica);
            System.out.println("URL destino: http://localhost:" + puertoDinamica + "/hechos");

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + puertoDinamica + "/hechos"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            System.out.println("Enviando request al servicio dinámico...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta del servicio dinámico - Status: " + response.statusCode());
            System.out.println("Respuesta del servicio dinámico - Body: " + response.body());

            context.status(response.statusCode()).result(response.body());

        } catch (Exception e) {
            System.err.println("ERROR en PostHechoHandler: " + e.getMessage());
            e.printStackTrace();
            context.status(500).result("Error interno del servidor: " + e.getMessage());
        }




        /*String bodyJson = context.body(); // request JSON completo

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + puertoDinamica + "/hechos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        context.status(response.statusCode()).result(response.body());*/
    }
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

