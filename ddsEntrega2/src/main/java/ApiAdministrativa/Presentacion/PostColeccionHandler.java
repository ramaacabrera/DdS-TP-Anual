package ApiAdministrativa.Presentacion;

import io.javalin.http.Handler;
import io.javalin.http.Context;
import Agregador.DTO.ColeccionDTO;
import org.jetbrains.annotations.NotNull;
import Persistencia.ColeccionRepositorio;

public class PostColeccionHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PostColeccionHandler(ColeccionRepositorio repositorioNuevo) { repositorio = repositorioNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        ColeccionDTO nueva = ctx.bodyAsClass(ColeccionDTO.class);

        System.out.println("Creando coleccion: " + jsonBody);
        repositorio.guardar(nueva);

        ctx.status(201).result("Colección creada con éxito.");
    }
}
