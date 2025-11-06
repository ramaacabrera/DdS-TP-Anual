package gestorAdministrativo.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.Persistencia.ColeccionRepositorio;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteColeccionesHandler implements Handler {
    // ColeccionRepositorio repositorio;

    private final ColeccionRepositorio coleccionRepositorio;

    public DeleteColeccionesHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    //public DeleteColeccionesHandler(ColeccionRepositorio repositorioNuevo) {repositorio = repositorioNuevo;}

    @Override
    public void handle(Context ctx) throws URISyntaxException, IOException, InterruptedException {
        String handle = ctx.pathParam("id");

        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones/" + handle))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        ctx.status(response.statusCode());

        //    <<<<<<<<<-

        /*
        Optional<Coleccion> aBorrar = repositorio.buscarPorHandle(handle);
        if(aBorrar.isPresent()){
            System.out.println("Borrando coleccion: " + jsonBody);
            repositorio.eliminar(aBorrar.get());
            ctx.status(201).result("Colección borrada con éxito.");
        }
        else {
            System.out.println("Coleccion no encontrada: " + jsonBody);
            ctx.status(404);
        }

         */

    }
}
