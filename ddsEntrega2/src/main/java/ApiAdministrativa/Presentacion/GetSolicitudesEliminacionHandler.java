package ApiAdministrativa.Presentacion;

import Agregador.Persistencia.SolicitudEliminacionRepositorio;
import Agregador.Solicitudes.SolicitudDeEliminacion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GetSolicitudesEliminacionHandler implements Handler {

    private final SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;

    public GetSolicitudesEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    ObjectMapper mapper = new ObjectMapper();

    //public GetSolicitudesEliminacionHandler(SolicitudEliminacionRepositorio repositorio) {this.repositorio = repositorio;}

    @Override
    public void handle(@NotNull Context ctx) throws IOException, InterruptedException {
        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/solicitudes"))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        List<SolicitudDeEliminacion> solicitudes = mapper.readValue(response.body(), new TypeReference<>() {
        });

        ctx.status(200).json(solicitudes);

        //    <<<<<<<<<-

        //context.json(repositorio.buscarTodas());
    }

}
