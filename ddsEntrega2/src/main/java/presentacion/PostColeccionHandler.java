package presentacion;

import io.javalin.http.Handler;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.Hecho;
import org.jetbrains.annotations.NotNull;
import org.example.agregador.HechoRepositorio;
import org.example.agregador.ColeccionRepositorio;
import org.example.agregador.Coleccion;

public class PostColeccionHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PostColeccionHandler(ColeccionRepositorio repositorioNuevo) { repositorio = repositorioNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        Coleccion nueva = ctx.bodyAsClass(Coleccion.class);

        System.out.println("Creando coleccion: " + jsonBody);
        repositorio.guardar(nueva);

        ctx.status(201).result("Colección creada con éxito.");
    }
}
