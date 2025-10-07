package CargadorDinamica.Presentacion;

import Agregador.fuente.Fuente;
import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.DTO.HechoDTO;
import org.jetbrains.annotations.NotNull;

public class PostHechosHandler implements Handler {

    private final DinamicoRepositorio repositorio;
    private final Fuente fuente;

    public PostHechosHandler(DinamicoRepositorio repositorioNuevo, Fuente fuenteNueva) {
        this.repositorio = repositorioNuevo;

        this.fuente = fuenteNueva;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        HechoDTO nueva = ctx.bodyAsClass(HechoDTO.class);

        nueva.setFuente(fuente);

        System.out.println("Creando hechoDTO: " + jsonBody);
        repositorio.guardarHecho(nueva);

        ctx.status(201).result("HechoDTO creado con éxito.");
    }
}
