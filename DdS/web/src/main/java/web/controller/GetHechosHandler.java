package web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import web.dto.HechoDTO;
import web.dto.PageDTO;
import web.service.HechoService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GetHechosHandler implements Handler {
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private final HechoService hechoService;

    public GetHechosHandler(HechoService hechoService) {
        this.hechoService = hechoService;
    }

    @Override
    public void handle(Context ctx) throws IOException {
        // 1) Filtros para la UI
        List<FilterDef> filters = buildFilters();

        // Convertir FilterDef a Map para FreeMarker
        List<Map<String, Object>> filtersForTemplate = filters.stream()
                .map(filter -> Map.of(
                        "key", filter.getKey(),
                        "label", filter.getLabel(),
                        "type", filter.getType(),
                        "options", filter.getOptions()
                ))
                .collect(Collectors.toList());

        String categoria = ctx.queryParam("categoria");
        String textoBusqueda = ctx.queryParam("textoBusqueda");
        String fechaCargaDesde = ctx.queryParam("fechaCargaDesde");
        String fechaCargaHasta = ctx.queryParam("fechaCargaHasta");
        String fechaAcontecimientoDesde = ctx.queryParam("fechaAcontecimientoDesde");
        String fechaAcontecimientoHasta = ctx.queryParam("fechaAcontecimientoHasta");
        String latitud = ctx.queryParam("latitud");
        String longitud = ctx.queryParam("longitud");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("categoria", categoria);
        queryParams.put("fecha_carga_desde", fechaCargaDesde);
        queryParams.put("fecha_carga_hasta", fechaCargaHasta);
        queryParams.put("fecha_acontecimiento_desde", fechaAcontecimientoDesde);
        queryParams.put("fecha_acontecimiento_hasta", fechaAcontecimientoHasta);
        queryParams.put("latitud", latitud);
        queryParams.put("longitud", longitud);
        queryParams.put("textoBusqueda", textoBusqueda);
        queryParams.put("page", Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1)));
        queryParams.put("size",Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10)));

        PageDTO<HechoDTO> resp = hechoService.obtenerHechos(queryParams);

        // 5) Índices para “Mostrando X–Y”
        int fromIndex = (resp.page - 1) * resp.size;                // 0-based
        int toIndex   = fromIndex + (resp.content != null ? resp.content.size() : 0);

        // 6) Modelo de datos para la vista
        Map<String, Object> model = new HashMap<>();
        model.put("baseHref", "/hechos");
        model.put("filters", filtersForTemplate); // ← Usar la versión convertida a Map

        model.put("filterValues", Map.of(
                "textoBusqueda", textoBusqueda != null ? textoBusqueda : "",
                "categoria", categoria != null ? categoria : "",
                "fecha_carga_desde", fechaCargaDesde != null ? formatDateForInput(fechaCargaDesde) : "",
                "fecha_carga_hasta", fechaCargaHasta != null ? formatDateForInput(fechaCargaHasta) : "",
                "fecha_acontecimiento_desde", fechaAcontecimientoDesde != null ? formatDateForInput(fechaAcontecimientoDesde) : "",
                "fecha_acontecimiento_hasta", fechaAcontecimientoHasta != null ? formatDateForInput(fechaAcontecimientoHasta) : "",
                "latitud", latitud != null ? latitud : "",
                "longitud", longitud != null ? longitud : ""
        ));

        model.put("hechos", resp.content != null ? resp.content : Collections.emptyList());
        model.put("total", resp.totalElements);
        model.put("page", resp.page);
        model.put("size", resp.size);
        model.put("totalPages", resp.totalPages);
        model.put("fromIndex", fromIndex);
        model.put("toIndex", toIndex);

        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            model.put("username", username);
            model.put("access_token", access_token);
        }
        ctx.render("home.ftl", model);
    }

    private String normalizarFecha(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }

        try {
            // Si ya viene en dd/MM/yyyy, devolver igual
            if (raw.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return raw;
            }

            // Si viene en formato texto "Thu Oct 09 00:00:00 ART 2025"
            // Este formato es: EEE MMM dd HH:mm:ss z yyyy
            if (raw.contains("ART") || raw.matches("[A-Za-z]{3} [A-Za-z]{3} \\d{2} \\d{2}:\\d{2}:\\d{2} [A-Za-z]{3,4} \\d{4}")) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = inputFormat.parse(raw);
                return outputFormat.format(date);
            }

            // Si viene en formato yyyy-MM-dd (formato input date)
            if (raw.matches("\\d{4}-\\d{2}-\\d{2}")) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = inputFormat.parse(raw);
                return outputFormat.format(date);
            }

            // Si viene en formato yyyy/MM/dd
            if (raw.matches("\\d{4}/\\d{2}/\\d{2}")) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = inputFormat.parse(raw);
                return outputFormat.format(date);
            }

            return raw;
        } catch (Exception e) {
            System.err.println("Error normalizando fecha: " + raw + " - " + e.getMessage());
            return raw;
        }
    }

    // --- Definición declarativa de filtros ---
    public static class FilterDef {
        private String key;
        private String label;
        private String type;
        private List<String> options;

        // Constructor vacío
        public FilterDef() {
            this.options = new ArrayList<>();
        }

        // Constructor con parámetros
        public FilterDef(String key, String label, String type) {
            this.key = key;
            this.label = label;
            this.type = type;
            this.options = new ArrayList<>();
        }

        // GETTERS PÚBLICOS (crucial para FreeMarker)
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String getType() { return type; }
        public List<String> getOptions() { return options; }

        // SETTERS PÚBLICOS (para poder asignar valores)
        public void setKey(String key) { this.key = key; }
        public void setLabel(String label) { this.label = label; }
        public void setType(String type) { this.type = type; }
        public void setOptions(List<String> options) { this.options = options; }

        // Método helper para agregar opciones
        public FilterDef addOption(String option) {
            this.options.add(option);
            return this;
        }
    }

    private List<FilterDef> buildFilters() {
        List<FilterDef> list = new ArrayList<>();

        // Usando constructor
        FilterDef cat = new FilterDef("categoria", "Categoría", "select");
        HttpUrl.Builder b = HttpUrl.parse(urlPublica + "/categoria").newBuilder();
        String finalUrl = b.build().toString();
        Request request = new Request.Builder().url(finalUrl).get().build();
        try (Response response = client.newCall(request).execute()) {
            List<String> categorias = mapper.readValue(response.body().string(), List.class);
            System.out.println(categorias);

            cat.setOptions(categorias);

            list.add(cat);
        }catch (IOException e) {
            e.printStackTrace();
        }


        // Fechas de carga
        list.add(new FilterDef("fecha_carga_desde", "Fecha carga desde", "date"));
        list.add(new FilterDef("fecha_carga_hasta", "Fecha carga hasta", "date"));

        // Fechas de acontecimiento
        list.add(new FilterDef("fecha_acontecimiento_desde", "Acontecimiento desde", "date"));
        list.add(new FilterDef("fecha_acontecimiento_hasta", "Acontecimiento hasta", "date"));

        // Coordenadas
        list.add(new FilterDef("latitud", "Latitud", "search"));
        list.add(new FilterDef("longitud", "Longitud", "search"));

        return list;
    }

    private String formatDateForInput(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) return "";

        try {
            // Para el input HTML necesitas formato yyyy-MM-dd
            // Pero convertimos desde cualquier formato a yyyy-MM-dd

            // Si ya viene en yyyy-MM-dd, devolver igual
            if (rawDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return rawDate;
            }

            // Si viene en formato FreeMarker/Java Date
            if (rawDate.contains("ART")) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormat.parse(rawDate);
                return outputFormat.format(date);
            }

            // Si viene en formato dd/MM/yyyy (nuestro nuevo formato de salida)
            if (rawDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = inputFormat.parse(rawDate);
                return outputFormat.format(date);
            }

            return rawDate;
        } catch (Exception e) {
            System.err.println("Error formateando fecha para input: " + rawDate);
            return "";
        }
    }
}
