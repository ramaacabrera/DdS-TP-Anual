package Agregador;

import Agregador.Criterios.Criterio;
import Agregador.HechosYColecciones.Hecho;
import Persistencia.HechoRepositorio;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.List;

public class GetHechosRepoHandler implements Handler {

    private final HechoRepositorio repositorio;

    public GetHechosRepoHandler(HechoRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) {
        ctx.json(repositorio);
    }
}
