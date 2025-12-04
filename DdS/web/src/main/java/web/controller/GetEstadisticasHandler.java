package web.controller;

import web.service.Normalizador.DesnormalizadorCategorias;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.CategoriasService;
import web.service.EstadisticasService;
import web.utils.ViewUtil;

import java.util.*;


public class GetEstadisticasHandler implements Handler {

    private final EstadisticasService estadisticasService;
    private final CategoriasService categoriasService;

    public GetEstadisticasHandler(EstadisticasService estadisticasService, CategoriasService categoriasService) {
        this.estadisticasService = estadisticasService;
        this.categoriasService = categoriasService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String uuidColeccion = ctx.queryParam("uuid");

            // Obtener estadísticas generales
            Map<String, Object> statsGenerales = estadisticasService.obtenerEstadisticasGenerales(); //hacerConsulta("/api/estadisticas/categoriaMax");
            Map<String, Object> statsUsuarios = estadisticasService.obtenerEstadisticasUsuarios(); // hacerConsulta("/api/estadisticas/solicitudesSpam");

            List<String> categoriasTotales = categoriasService.obtenerCategorias();

            // Procesar categorías en paralelo
            List<Map<String, Object>> statsCategoria = categoriasService.procesarCategorias(categoriasTotales);

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            Object rawValue = obtenerValorConFallback(statsGenerales, Arrays.asList("categoria", "estadisticas_categoria_max_hechos"), "N/A");
            String categoriaString = (rawValue != null) ? rawValue.toString() : "N/A";
            String categoriaFormateada = "N/A".equals(categoriaString) ? "N/A" : DesnormalizadorCategorias.desnormalizar(categoriaString);

            modelo.put("categoriaMax", categoriaFormateada);
            modelo.put("solicitudesSpam", obtenerValorConFallback(statsUsuarios, Arrays.asList("spam", "spamCount", "estadisticas_spam"), 0));
            modelo.put("categorias", statsCategoria);
            modelo.put("totalCategorias", categoriasTotales.size());

            if (uuidColeccion != null && !uuidColeccion.trim().isEmpty() && esUUIDValido(uuidColeccion)) {
                try {
                    Map<String, Object> statsColeccion = estadisticasService.obtenerEstadisticasColeccion(uuidColeccion);

                    modelo.put("uuidColeccion", uuidColeccion.trim());
                    modelo.put("statsColeccion", statsColeccion);
                    if (statsColeccion.containsKey("nombre")) {
                        modelo.put("nombreColeccion", statsColeccion.get("nombre"));
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo stats de colección " + uuidColeccion + ": " + e.getMessage());
                    modelo.put("uuidColeccion", uuidColeccion.trim());
                }
            }

            ctx.render("estadisticas.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error general en GetEstadisticasHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json(Map.of(
                    "error", "Error al obtener estadísticas",
                    "detalle", e.getMessage()
            ));
        }
    }

    private boolean esUUIDValido(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuid.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Object obtenerValorConFallback(Map<String, Object> map, List<String> possibleKeys, Object defaultValue){
        if (map == null || map.isEmpty() || possibleKeys == null || possibleKeys.isEmpty()) {
            return defaultValue;
        }

        for (String key : possibleKeys) {
            if (map.containsKey(key)) {
                Object value = map.get(key);

                if (value != null) {
                    return value;
                }
            }
        }

        return defaultValue;
    }
}