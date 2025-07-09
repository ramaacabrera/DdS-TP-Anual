package presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;

public class DeleteColeccionesHandler implements Handler {
    ColeccionRepositorio repositorio;

    public DeleteColeccionesHandler(ColeccionRepositorio repositorioNuevo) {repositorio = repositorioNuevo;}

    @Override
    public void handle(Context ctx){
        String jsonBody = ctx.body();
        Coleccion nueva = ctx.bodyAsClass(Coleccion.class);

        System.out.println("Borrando coleccion: " + jsonBody);
        repositorio.eliminar(nueva);

        ctx.status(201).result("Colección borrada con éxito.");
    }
}
