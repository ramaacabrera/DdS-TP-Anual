package Agregador.Presentacion;

import Agregador.Persistencia.HechoRepositorio;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class GetHechosRepoHandler implements Handler {

    private final HechoRepositorio repositorio;

    public GetHechosRepoHandler(HechoRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) {
        ctx.json(repositorio);
    }
}
