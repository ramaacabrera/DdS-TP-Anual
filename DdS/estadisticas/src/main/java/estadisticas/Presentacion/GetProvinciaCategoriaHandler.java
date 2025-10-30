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
        try {
            String categoria = ctx.pathParam("categoria");

            if (categoria == null || categoria.trim().isEmpty()) {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("error", "Categoría no especificada");
                resultado.put("status", 400);
                ctx.status(400).json(resultado);
                return;
            }

            Optional<String> provinciaOpt = repository.buscarProvinciaCategoria(categoria.trim());

            Map<String, Object> resultado = new HashMap<>();
            if (!provinciaOpt.isPresent()) {
                resultado.put("error", "No se encontraron datos para la categoría: " + categoria);
                resultado.put("status", 404);
                ctx.status(404).json(resultado);
            } else {
                resultado.put("provincia", provinciaOpt.get());
                resultado.put("categoria", categoria);
                ctx.status(200).json(resultado);
            }
        } catch (Exception e) {
            System.err.println("Error en GetProvinciaCategoriaHandler: " + e.getMessage());
            ctx.status(500).json(Map.of(
                    "error", "Error interno del servidor",
                    "detalle", e.getMessage()
            ));
        }
    }
}