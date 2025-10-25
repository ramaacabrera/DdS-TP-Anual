package ApiAdministrativa.Presentacion;

import ApiAdministrativa.Persistencia.ColeccionRepositorio;
import ApiAdministrativa.Persistencia.SolicitudEliminacionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PutSolicitudEliminacionHandler implements Handler{
    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public PutSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    }
    @Override
    public void handle(Context context) throws Exception {
        String id = context.pathParam("id");
        String bodyJson = context.body(); // el body recibido

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/solicitudes/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        context.status(response.statusCode()).result(response.body());
    }

    //    private SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
//
//    public PutSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorioNuevo) {solicitudEliminacionRepositorio = solicitudEliminacionRepositorioNuevo;}
//
//    @Override
//    public void handle(Context context) {
//        String id = context.pathParam("id");
//        String body = context.body();
//        boolean resultado = solicitudEliminacionRepositorio.actualizarEstadoSolicitudEliminacion(body,id);
//        if(resultado){
//            context.status(200);
//        } else {
//            context.status(404);
//        }
//    }

}
