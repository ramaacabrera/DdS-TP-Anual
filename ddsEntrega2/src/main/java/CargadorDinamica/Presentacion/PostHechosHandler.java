package CargadorDinamica.Presentacion;

import Agregador.fuente.Fuente;
import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.DTO.HechoDTO;
import org.jetbrains.annotations.NotNull;
import CargadorDinamica.DinamicaDto.Hecho_D_DTO;

public class PostHechosHandler implements Handler {

    private final DinamicoRepositorio repositorio;

    public PostHechosHandler(DinamicoRepositorio repositorioNuevo, Fuente fuenteNueva) {
        this.repositorio = repositorioNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String jsonBody = ctx.body();
        Hecho_D_DTO nueva = ctx.bodyAsClass(Hecho_D_DTO.class);

        System.out.println("Creando hechoDTO: " + jsonBody);
        repositorio.guardarHecho(nueva);

        ctx.status(201).result("HechoDTO creado con éxito.");
    }
}
