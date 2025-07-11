package presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;

import java.util.Optional;

public class DeleteColeccionesHandler implements Handler {
    ColeccionRepositorio repositorio;

    public DeleteColeccionesHandler(ColeccionRepositorio repositorioNuevo) {repositorio = repositorioNuevo;}

    @Override
    public void handle(Context ctx){
        String handle = ctx.pathParam("id");
        String jsonBody = ctx.body();
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

    }
}
