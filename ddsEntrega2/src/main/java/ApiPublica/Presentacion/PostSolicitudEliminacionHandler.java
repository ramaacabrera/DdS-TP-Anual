package ApiPublica.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.DTO.SolicitudDeEliminacionDTO;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostSolicitudEliminacionHandler implements Handler {
    private final int puertoDinamica;

    public PostSolicitudEliminacionHandler(int puertoDinamica) {
        this.puertoDinamica = puertoDinamica;
    }

        @Override
        public void handle(Context context) throws Exception {
            String bodyJson = context.body(); // request JSON completo

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/solicitudesEliminacion")) // 🔹 Puerto del Dinámico
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            context.status(response.statusCode()).result(response.body());
        }

    //    private final ControllerSolicitud controllerSolicitud;
//
//    public PostSolicitudEliminacionHandler(ControllerSolicitud controllerSolicitudNuevo) { controllerSolicitud = controllerSolicitudNuevo; }
//
//    @Override
//    public void handle(@NotNull Context context) throws Exception {
//        String bodyString = context.body();
//        SolicitudDeEliminacionDTO solicitud = context.bodyAsClass(SolicitudDeEliminacionDTO.class);
//
//        System.out.println("Creando solicitud: " + bodyString);
//        controllerSolicitud.subirSolicitudEliminacion(solicitud);
//
//        context.status(201);
//    }
}
