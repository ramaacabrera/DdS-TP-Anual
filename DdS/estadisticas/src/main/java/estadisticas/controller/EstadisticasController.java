package estadisticas.controller;

import estadisticas.service.EstadisticasService;
import estadisticas.service.normalizador.NormalizadorCategorias;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EstadisticasController {

    private final EstadisticasService service;

    public EstadisticasController(EstadisticasService service) {
        this.service = service;
    }

    public void getCategoriaMax(@NotNull Context ctx) {
        Optional<String> categoria = service.obtenerCategoriaMaxHechos();

        Map<String, Object> resultado = new HashMap<>();
        if (categoria.isPresent()) {
            resultado.put("categoria", categoria.get());
            ctx.status(200).json(resultado);
        } else {
            resultado.put("error", "Categoría no encontrada");
            resultado.put("status", 404);
            ctx.status(404).json(resultado);
        }
    }

    public void getCategorias(@NotNull Context ctx) {
        try {
            List<String> categorias = service.obtenerCategoriasNormalizadas();
            Map<String, Object> resultado = new HashMap<>();

            if (categorias.isEmpty()) {
                resultado.put("error", "No se encontraron categorías en las estadísticas");
                resultado.put("status", 404);
                ctx.status(404).json(resultado);
            } else {
                resultado.put("categorias", categorias);
                resultado.put("total", categorias.size());
                resultado.put("timestamp", new Date());
                ctx.status(200).json(resultado);
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }

    public void getHoraMaxCategoria(@NotNull Context ctx) {
        String categoriaParam = ctx.pathParam("categoria");
        if (parametroVacio(categoriaParam)) {
            ctx.status(400).json(Map.of("error", "Categoría no especificada"));
            return;
        }

        Optional<Integer> horaOpt = service.obtenerHoraMaxPorCategoria(categoriaParam);

        if (horaOpt.isPresent()) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("hora", horaOpt.get());
            resultado.put("categoria", NormalizadorCategorias.normalizar(categoriaParam));
            ctx.status(200).json(resultado);
        } else {
            ctx.status(404).json(Map.of("error", "No se encontraron datos para la categoría", "status", 404));
        }
    }

    public void getProvinciaCategoria(@NotNull Context ctx) {
        String categoriaParam = ctx.pathParam("categoria");
        if (parametroVacio(categoriaParam)) {
            ctx.status(400).json(Map.of("error", "Categoría no especificada"));
            return;
        }

        Optional<String> provinciaOpt = service.obtenerProvinciaPorCategoria(categoriaParam);

        if (provinciaOpt.isPresent()) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("provincia", provinciaOpt.get());
            resultado.put("categoria", NormalizadorCategorias.normalizar(categoriaParam));
            ctx.status(200).json(resultado);
        } else {
            ctx.status(404).json(Map.of("error", "No se encontraron datos para la categoría", "status", 404));
        }
    }

    public void getProvinciaColeccion(@NotNull Context ctx) {
        String coleccionParam = ctx.pathParam("coleccion");

        try {
            Map<String, String> datos = service.obtenerProvinciaPorColeccion(coleccionParam);

            if (datos != null) {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("provincia", datos.get("provincia"));
                resultado.put("nombre", datos.get("nombre"));
                ctx.status(200).json(resultado);
            } else {
                ctx.status(404).json(Map.of("error", "Colección no encontrada", "status", 404));
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", "UUID de colección inválido", "status", 400));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Error interno", "detalle", e.getMessage()));
        }
    }

    public void getSolicitudesSpam(@NotNull Context ctx) {
        Optional<Long> spamCount = service.obtenerSolicitudesSpam();

        if (spamCount.isPresent()) {
            ctx.status(200).json(Map.of("spam", spamCount.get()));
        } else {
            ctx.status(404).json(Map.of("error", "Cantidad de Spam no encontrada", "status", 404));
        }
    }

    public void exportarCSV(@NotNull Context ctx) {
        Optional<String> csvContent = service.generarReporteCSV();

        if (csvContent.isPresent()) {
            ctx.header("Content-Type", "text/csv");
            ctx.header("Content-Disposition", "attachment; filename=\"reporte_metamapa_" + LocalDate.now() + ".csv\"");
            ctx.result(csvContent.get());
        } else {
            ctx.status(404).result("No hay estadísticas generadas aún para exportar.");
        }
    }

    private boolean parametroVacio(String param) {
        return param == null || param.trim().isEmpty();
    }
}