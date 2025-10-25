package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GetCategoriaMaxHandler implements Handler {
    private final EstadisticasRepositorio repository;

    public GetCategoriaMaxHandler(EstadisticasRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        Optional<String> resultado = repository.buscarCategoria_max_hechos();
        if(!resultado.isPresent()){
            ctx.status(404);
        }

        ctx.status(200).json(resultado.get());
    }
}