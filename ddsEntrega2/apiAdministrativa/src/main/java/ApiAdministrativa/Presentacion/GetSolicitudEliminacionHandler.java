package ApiAdministrativa.Presentacion;

<<<<<<<< HEAD:DdS/apiAdministrativa/src/main/java/ApiAdministrativa/Presentacion/GetSolicitudEliminacionHandler.java
import utils.Persistencia.SolicitudEliminacionRepositorio;
========
import ApiAdministrativa.Persistencia.SolicitudEliminacionRepositorio;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/apiAdministrativa/src/main/java/ApiAdministrativa/Presentacion/GetSolicitudEliminacionHandler.java
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
<<<<<<<< HEAD:DdS/apiAdministrativa/src/main/java/ApiAdministrativa/Presentacion/GetSolicitudEliminacionHandler.java
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
========
import ApiAdministrativa.Solicitudes.SolicitudDeEliminacion;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/apiAdministrativa/src/main/java/ApiAdministrativa/Presentacion/GetSolicitudEliminacionHandler.java

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetSolicitudEliminacionHandler implements Handler {
    private SolicitudEliminacionRepositorio solicitudEliminacionRepositorio;
    ObjectMapper mapper = new ObjectMapper();

    public GetSolicitudEliminacionHandler(SolicitudEliminacionRepositorio solicitudEliminacionRepositorio) {
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
    //public GetSolicitudEliminacionHandler(SolicitudEliminacionRepositorio repositorio){ this.repositorio = repositorio;
    }

    @Override
    public void handle(Context ctx) throws IOException, InterruptedException {
        String handle = ctx.pathParam("id");

        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/colecciones/" + handle))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SolicitudDeEliminacion solicitud = mapper.readValue(response.body(), new TypeReference<>() {
        });

        ctx.status(200).json(solicitud);

        //    <<<<<<<<<-

//        final Optional<SolicitudDeEliminacion> resultadoBusqueda = repositorio.buscarPorId(handle);
//        if (resultadoBusqueda.isPresent()) {
//            ctx.status(200).json(resultadoBusqueda.get());
//        } else {
//            ctx.status(404);
//        }
    }
}