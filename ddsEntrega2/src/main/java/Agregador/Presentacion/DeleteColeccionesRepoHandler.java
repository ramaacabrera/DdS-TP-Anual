package Agregador.Presentacion;

import Agregador.HechosYColecciones.Coleccion;
import Agregador.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class DeleteColeccionesRepoHandler implements Handler {
    ColeccionRepositorio repositorio;


    public DeleteColeccionesRepoHandler(ColeccionRepositorio repositorioNuevo) {repositorio = repositorioNuevo;}

    @Override
    public void handle(Context ctx) throws URISyntaxException, IOException, InterruptedException {
        String handle = ctx.pathParam("id");

        Optional<Coleccion> aBorrar = repositorio.buscarPorHandle(handle);
        if(aBorrar.isPresent()){
            //System.out.println("Borrando coleccion: " + ctx.json()jsonBody);
            repositorio.eliminar(aBorrar.get());
            ctx.status(201).result("Colección borrada con éxito.");
        }
        else {
            //System.out.println("Coleccion no encontrada: " + jsonBody);
            ctx.status(404);
        }
    }
}
