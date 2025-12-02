package web.controller;

import io.javalin.http.Handler;
import web.dto.Hechos.HechoDTO;
import web.dto.PageDTO;
import web.service.HechoService;

import java.util.*;
import java.util.stream.Collectors;

public class HechoController {

    private HechoService hechoService;

    public HechoController(HechoService hechoService) {
        this.hechoService = hechoService;
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
        String fechaCargaDesde = ctx.queryParam("fecha_carga_desde");
        String fechaCargaHasta = ctx.queryParam("fecha_carga_hasta");
        String fechaAcontecimientoDesde = ctx.queryParam("fecha_acontecimiento_desde");
        String fechaAcontecimientoHasta = ctx.queryParam("fecha_acontecimiento_hasta");
        String latitud = ctx.queryParam("latitud");
        String longitud = ctx.queryParam("longitud");

        // Paginación (Default: Página 1, Tamaño 10)
        int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
        int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

        // 3) Armar mapa de filtros para enviar al servicio
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("categoria", categoria);
        queryParams.put("textoBusqueda", textoBusqueda);
        queryParams.put("fecha_carga_desde", fechaCargaDesde);
        queryParams.put("fecha_carga_hasta", fechaCargaHasta);
        queryParams.put("fecha_acontecimiento_desde", fechaAcontecimientoDesde);
        queryParams.put("fecha_acontecimiento_hasta", fechaAcontecimientoHasta);
        queryParams.put("latitud", latitud);
        queryParams.put("longitud", longitud);

        // 4) Llamar al servicio (Lógica de negocio/HTTP)
        PageDTO<HechoDTO> resp = hechoService.buscarHechos(queryParams, page, size);

        // 5) Índices para visualización "Mostrando X-Y de Z"
        long fromIndex = 0;
        long toIndex = 0;

        if (resp.content != null && !resp.content.isEmpty()) {
            // Backend pagina desde 1.
            // Ejemplo: Pag 1, Size 10 -> (1-1)*10 + 1 = 1.
            fromIndex = ((long) (resp.page - 1) * resp.size) + 1;

            // Fin: Inicio + Cantidad actual - 1
            toIndex = fromIndex + resp.content.size() - 1;
        }

        // 6) Preparar el Modelo para FreeMarker
        Map<String, Object> model = new HashMap<>();
        model.put("baseHref", "/hechos");
        model.put("filters", filtersForTemplate);

        // Valores actuales para rellenar el formulario (Input values)
        // Importante: Formateamos las fechas para que el input type="date" las acepte (yyyy-MM-dd)
        model.put("filterValues", Map.of(
                "textoBusqueda", textoBusqueda != null ? textoBusqueda : "",
                "categoria", categoria != null ? categoria : "",
                "fecha_carga_desde", formatDateForInput(fechaCargaDesde),
                "fecha_carga_hasta", formatDateForInput(fechaCargaHasta),
                "fecha_acontecimiento_desde", formatDateForInput(fechaAcontecimientoDesde),
                "fecha_acontecimiento_hasta", formatDateForInput(fechaAcontecimientoHasta),
                "latitud", latitud != null ? latitud : "",
                "longitud", longitud != null ? longitud : ""
        ));

        // Datos principales
        model.put("hechos", resp.content != null ? resp.content : Collections.emptyList());
        model.put("total", resp.totalElements);
        model.put("page", resp.page);
        model.put("size", resp.size);
        model.put("totalPages", resp.totalPages);
        model.put("fromIndex", fromIndex);
        model.put("toIndex", toIndex);

        // Datos de Sesión (Navbar)
        if (!ctx.sessionAttributeMap().isEmpty()) {
            String username = ctx.sessionAttribute("username");
            String accessToken = ctx.sessionAttribute("access_token");
            model.put("username", username);
            model.put("access_token", accessToken);
        }

        // 7) Renderizar
        ctx.render("home.ftl", model);
    };


    public Handler obtenerHechoPorId = ctx -> {
        String hechoIdString = ctx.pathParam("id");

        if (hechoIdString == null || hechoIdString.trim().isEmpty()) {
            ctx.status(400).result("Error 400: ID de hecho no proporcionado.");
            return;
        }

        HechoDTO hecho = hechoService.obtenerHechoPorId(hechoIdString);

        if (hecho == null) {
            ctx.status(404).result("Hecho no encontrado o servicio no disponible.");
            return;
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("hecho", hecho);

        if (!ctx.sessionAttributeMap().isEmpty()) {
            modelo.put("username", ctx.sessionAttribute("username"));
            modelo.put("access_token", ctx.sessionAttribute("access_token"));
        }

        ctx.render("hecho-especifico.ftl", modelo);
    };

    public Handler obtenerPageCrearHecho = ctx -> {
        // Solo renderiza la plantilla con el formulario vacío
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Reportar un Hecho");
        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            modelo.put("username", username);
            modelo.put("access_token", access_token);
        }
        ctx.render("crear-hecho.ftl", modelo);
    };

    public Handler actualizarHecho = ctx -> {

        String id = ctx.pathParam("id");
        HechoDTO dto = ctx.bodyAsClass(HechoDTO.class);

        boolean actualizado = hechoService.actualizarHecho(id, dto);

        if (!actualizado) {
            ctx.status(404).result("Hecho no encontrado");
            return;
        }

        ctx.status(200).result("Hecho actualizado correctamente");
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

        // Fechas de carga
        list.add(new HechoController.FilterDef("fecha_carga_desde", "Fecha carga desde", "date"));
        list.add(new HechoController.FilterDef("fecha_carga_hasta", "Fecha carga hasta", "date"));

        // Fechas de acontecimiento
        list.add(new HechoController.FilterDef("fecha_acontecimiento_desde", "Acontecimiento desde", "date"));
        list.add(new HechoController.FilterDef("fecha_acontecimiento_hasta", "Acontecimiento hasta", "date"));

        // Coordenadas
        list.add(new HechoController.FilterDef("latitud", "Latitud", "search"));
        list.add(new HechoController.FilterDef("longitud", "Longitud", "search"));

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
