package Estadisticas.Presentacion;

import Estadisticas.GeneradorEstadisticas;
import Estadisticas.Persistencia.EstadisticasRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GetSolicitudesSpamHandler implements Handler {
    private final EstadisticasRepositorio repository;

    public GetSolicitudesSpamHandler(EstadisticasRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Optional<Integer> resultado = repository.buscarSpam();
        if(!resultado.isPresent()){
            ctx.status(404);
        }

        ctx.status(200).json(resultado.get());
    }
}
