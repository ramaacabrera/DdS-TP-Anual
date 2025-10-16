package Estadisticas.Presentacion;

import Estadisticas.Persistencia.EstadisticasRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetCategoriaMaxHandler implements Handler {
    private final EstadisticasRepositorio repository;

    public GetCategoriaMaxHandler(EstadisticasRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ctx.status(200).json(repository.buscarCategoria_max_hechos());
    }
}