package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetCategoriaMaxHandler implements Handler {
    private final EstadisticasRepositorio repository;

    public GetCategoriaMaxHandler(EstadisticasRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        Optional<String> categoria = repository.buscarCategoria_max_hechos();

        Map<String,Object> resultado = new HashMap<>();
        if (!categoria.isPresent()) {
            resultado.put("error", "Categoría no encontrada");
            resultado.put("status", 404);
            ctx.status(404).json(resultado);
        } else {
            resultado.put("categoria", categoria.get());
            ctx.status(200).json(resultado);
        }
    }
}