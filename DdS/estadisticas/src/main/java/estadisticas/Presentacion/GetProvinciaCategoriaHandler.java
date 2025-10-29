package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetProvinciaCategoriaHandler implements Handler {
    private final EstadisticasCategoriaRepositorio repository;

    public GetProvinciaCategoriaHandler(EstadisticasCategoriaRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String categoria = ctx.pathParam("categoria");
        Optional<String> provincia = repository.buscarProvinciaCategoria(categoria);

        Map<String,Object> resultado = new HashMap<>();
        if (!provincia.isPresent()) {
            resultado.put("error", "Categoría no encontrada");
            resultado.put("status", 404);
            ctx.status(404).json(resultado);
        } else {
            resultado.put("provincia", provincia.get());
            ctx.status(200).json(resultado);
        }
    }
}
