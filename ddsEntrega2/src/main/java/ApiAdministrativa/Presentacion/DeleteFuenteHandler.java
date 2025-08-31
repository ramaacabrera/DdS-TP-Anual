package ApiAdministrativa.Presentacion;

import Agregador.fuente.Fuente;
import Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import Agregador.HechosYColecciones.Coleccion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Optional;

public class DeleteFuenteHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public DeleteFuenteHandler(ColeccionRepositorio repositorio) {this.repositorio = repositorio;}

    @Override
    public void handle(Context context) {
        String handle = context.pathParam("id");
        context.bodyAsClass(Fuente.class)

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

//        Optional<Coleccion> resultadoBusqueda = repositorio.buscarPorHandle(handle);
//        if(resultadoBusqueda.isPresent()){
//            resultadoBusqueda.get().eliminarFuente(context.bodyAsClass(Fuente.class));
//            context.status(200);
//        } else {
//            context.status(404);
//        }
    }

}
