package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetHoraMaxCategoriaHandler implements Handler {
    private final EstadisticasCategoriaRepositorio repository;

    public GetHoraMaxCategoriaHandler(EstadisticasCategoriaRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String categoria = ctx.pathParam("categoria");
        Optional<LocalTime> hora = repository.buscarHoraCategoria(categoria);

        Map<String,Object> resultado = new HashMap<>();
        if (!hora.isPresent()) {
            resultado.put("error", "Categoría no encontrada");
            resultado.put("status", 404);
            ctx.status(200).json(resultado);
        } else {
            resultado.put("hora", hora.get().toString());
            ctx.status(200).json(resultado);
        }
    }
}