package estadisticas.controller;

import estadisticas.service.normalizador.NormalizadorCategorias;
import estadisticas.repository.EstadisticasCategoriaRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

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
        try {
            String categoriaParam = ctx.pathParam("categoria");

            if (categoriaParam == null || categoriaParam.trim().isEmpty()) {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("error", "Categoría no especificada");
                resultado.put("status", 400);
                ctx.status(400).json(resultado);
                return;
            }

            String categoria = NormalizadorCategorias.normalizar(categoriaParam);

            // Usar el nuevo método con dos queries separadas
            Optional<Integer> horaOpt = repository.buscarHoraCategoria(categoria.trim());

            Map<String, Object> resultado = new HashMap<>();
            if (!horaOpt.isPresent()) {
                resultado.put("error", "No se encontraron datos para la categoría: " + categoria);
                resultado.put("status", 404);
                ctx.status(404).json(resultado);
            } else {
                resultado.put("hora", horaOpt.get());
                resultado.put("categoria", categoria);
                ctx.status(200).json(resultado);
            }
        } catch (Exception e) {
            System.err.println("Error en GetHoraMaxCategoriaHandler: " + e.getMessage());
            ctx.status(500).json(Map.of(
                    "error", "Error interno del servidor",
                    "detalle", e.getMessage()
            ));
        }
    }
}