package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.PaqueteNormalizador.NormalizadorCategorias;

import java.util.*;
import java.util.stream.Collectors;

public class GetCategoriasHandler implements Handler {
    private final EstadisticasCategoriaRepositorio repository;

    public GetCategoriasHandler(EstadisticasCategoriaRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            List<String> categorias = repository.obtenerTodasLasCategorias();

            List<String> categoriasNormalizadas = categorias.stream()
                    .map(NormalizadorCategorias::normalizar)
                    .collect(Collectors.toList());

            Map<String, Object> resultado = new HashMap<>();

            if (categoriasNormalizadas.isEmpty()) {
                System.out.println("❌ No se encontraron categorías");
                resultado.put("error", "No se encontraron categorías en las estadísticas");
                resultado.put("status", 404);
                ctx.status(404).json(resultado);
            } else {
                resultado.put("categorias", categoriasNormalizadas);
                resultado.put("total", categoriasNormalizadas.size());
                resultado.put("timestamp", new Date());
                ctx.status(200).json(resultado);
            }
        } catch (Exception e) {
            System.err.println("❌ Error en GetCategoriasHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json(Map.of(
                    "error", "Error interno del servidor",
                    "detalle", e.getMessage()
            ));
        }
    }
}