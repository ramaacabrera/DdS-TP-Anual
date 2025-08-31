package ApiAdministrativa.Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import Agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class DeleteColeccionesHandler implements Handler {
    ColeccionRepositorio repositorio;

    public DeleteColeccionesHandler(ColeccionRepositorio repositorioNuevo) {repositorio = repositorioNuevo;}

    @Override
    public void handle(Context ctx) throws URISyntaxException {
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
