package web.controller;

import io.javalin.http.Handler;
import web.dto.Hechos.HechoDTO;
import web.dto.PageDTO;
import web.service.HechoService;
import web.utils.ViewUtil;

import java.util.*;
import java.util.stream.Collectors;

public class HechoController {

    private HechoService hechoService;
    private String urlPublica;
    private Map<String, Object> dataCloud;
    private String urlAdmin;

    public HechoController(String urlPublica, String urlAdmin ,HechoService hechoService, Map<String, Object> dataCloud) {
        this.urlPublica = urlPublica;
        this.urlAdmin = urlAdmin;
        this.hechoService = hechoService;
        this.dataCloud = dataCloud;
    }

    public Handler listarHechos = ctx -> {
        // 1) Construir la definición visual de los filtros (para el sidebar)
        List<HechoController.FilterDef> filters = buildFilters();

        List<Map<String, Object>> filtersForTemplate = filters.stream()
                .map(filter -> Map.of(
                        "key", filter.getKey(),
                        "label", filter.getLabel(),
                        "type", filter.getType(),
                        "options", filter.getOptions()
                ))
                .collect(Collectors.toList());

        // 2) Leer parámetros de la URL (Query Params)
        String categoria = ctx.queryParam("categoria");
        String textoBusqueda = ctx.queryParam("textoBusqueda");

        // Fechas
        String fechaAcontecimientoDesde = ctx.queryParam("fecha_acontecimiento_desde");
        String fechaAcontecimientoHasta = ctx.queryParam("fecha_acontecimiento_hasta");
        String descripcion = ctx.queryParam("descripcion");
        String callback =  ctx.queryParam("callback");

        // Paginación (Default: Página 1, Tamaño 10)
        int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
        int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

        // 3) Armar mapa de filtros para enviar al servicio
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("categoria", categoria);
        queryParams.put("textoBusqueda", textoBusqueda);
        queryParams.put("fecha_acontecimiento_desde", fechaAcontecimientoDesde);
        queryParams.put("fecha_acontecimiento_hasta", fechaAcontecimientoHasta);
        queryParams.put("descripcion", descripcion);

        // 4) Llamar al servicio (Lógica de negocio/HTTP)
        PageDTO<HechoDTO> resp = hechoService.buscarHechos(queryParams, page, size);

        // 5) Índices para visualización "Mostrando X-Y de Z"
        long fromIndex = 0;
        long toIndex = 0;

        if (resp.content != null && !resp.content.isEmpty()) {
            // Backend pagina desde 1.
            fromIndex = ((long) (resp.page - 1) * resp.size) + 1;
            toIndex = fromIndex + resp.content.size() - 1;
        }

        // 6) Preparar el Modelo para FreeMarker
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("baseHref", "/hechos");
        model.put("filters", filtersForTemplate);

        // Valores actuales para rellenar el formulario (Input values)
        model.put("filterValues", Map.of(
                "textoBusqueda", textoBusqueda != null ? textoBusqueda : "",
                "categoria", categoria != null ? categoria : "",
                "fecha_acontecimiento_desde", formatDateForInput(fechaAcontecimientoDesde),
                "fecha_acontecimiento_hasta", formatDateForInput(fechaAcontecimientoHasta),
                "descripcion", descripcion != null ? descripcion : ""
        ));

        // Datos principales
        model.put("hechos", resp.content != null ? resp.content : Collections.emptyList());
        model.put("total", resp.totalElements);
        model.put("page", resp.page);
        model.put("size", resp.size);
        model.put("totalPages", resp.totalPages);
        model.put("fromIndex", fromIndex);
        model.put("toIndex", toIndex);
        model.put("urlAdmin", urlAdmin);
        model.put("callback", callback);

        // 7) Renderizar
        ctx.render("hechos.ftl", model);
    };


    public Handler obtenerHechoPorId = ctx -> {
        String hechoIdString = ctx.pathParam("id");

        if (hechoIdString == null || hechoIdString.trim().isEmpty()) {
            ctx.status(400).result("Error 400: ID de hecho no proporcionado.");
            return;
        }

        HechoDTO hecho = hechoService.obtenerHechoPorId(hechoIdString);

        if(hecho.getEstadoHecho().toString().equals("OCULTO")){
            ctx.redirect("/hechos");
            return;
        }

        if (hecho == null) {
            ctx.status(404).result("Hecho no encontrado o servicio no disponible.");
            return;
        }

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("hecho", hecho);

        ctx.render("hecho-especifico.ftl", modelo);
    };

    public Handler obtenerPageCrearHecho = ctx -> {

        List<String> categorias = hechoService.obtenerCategorias();

        // Solo renderiza la plantilla con el formulario vacío
        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Reportar un Hecho");
        modelo.put("urlPublica", urlPublica);
        modelo.put("cloudinaryUrl", dataCloud.get("cloudinaryUrl"));
        modelo.put("cloudinaryPreset", dataCloud.get("cloudinaryPreset"));
        modelo.put("categorias", categorias);
        ctx.render("crear-hecho.ftl", modelo);
    };


    // --- Helpers Internos ---

    private String formatDateForInput(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) return "";
        return hechoService.formatearFechaParaInput(rawDate);
    }

    private List<HechoController.FilterDef> buildFilters() {
        List<HechoController.FilterDef> list = new ArrayList<>();

        // Categoría (Obtenida dinámicamente del servicio)
        HechoController.FilterDef cat = new HechoController.FilterDef("categoria", "Categoría", "select");
        List<String> categorias = hechoService.obtenerCategorias();
        if (categorias != null) {
            cat.setOptions(categorias);
        }
        list.add(cat);

        // Fechas de acontecimiento
        list.add(new HechoController.FilterDef("fecha_acontecimiento_desde", "Acontecimiento desde", "date"));
        list.add(new HechoController.FilterDef("fecha_acontecimiento_hasta", "Acontecimiento hasta", "date"));

        list.add(new HechoController.FilterDef("descripcion", "Ubicación (Descripción)", "search"));

        return list;
    }

    // --- Clase interna para definición de filtros ---
    public static class FilterDef {
        private String key;
        private String label;
        private String type;
        private List<String> options;

        public FilterDef(String key, String label, String type) {
            this.key = key;
            this.label = label;
            this.type = type;
            this.options = new ArrayList<>();
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public String getType() {
            return type;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }
    }
}