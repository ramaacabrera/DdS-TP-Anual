package Agregador.Handlers;

import Agregador.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.ColeccionDTO;

public class PostColeccionRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public PostColeccionRepoHandler(ColeccionRepositorio repositorioNuevo) { repositorio = repositorioNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        ColeccionDTO nueva = ctx.bodyAsClass(ColeccionDTO.class);

        System.out.println("Creando coleccion: " + jsonBody);
        repositorio.guardar(nueva);

        ctx.status(201).result("Colección creada con éxito.");
    }
}
