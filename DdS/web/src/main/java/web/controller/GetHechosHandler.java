package web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import web.dto.Hechos.HechoDTO;
import web.dto.PageDTO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GetHechosHandler implements Handler {

    private final String urlPublica;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    public GetHechosHandler(String url) {
        this.urlPublica = url;
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

        // 2) Leer filtros y paginación desde la query
        String categoria = ctx.queryParam("categoria");
        String fechaCargaDesde = ctx.queryParam("fecha_carga_desde");
        String fechaCargaHasta = ctx.queryParam("fecha_carga_hasta");
        String fechaAcontecimientoDesde = ctx.queryParam("fecha_acontecimiento_desde");
        String fechaAcontecimientoHasta = ctx.queryParam("fecha_acontecimiento_hasta");
        String latitud = ctx.queryParam("latitud");
        String longitud = ctx.queryParam("longitud");
        String textoBusqueda = ctx.queryParam("textoBusqueda");

        int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
        int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

        // 3) Construir URL del backend con los nombres EXACTOS que recibe + paginación
        HttpUrl.Builder b = HttpUrl.parse(urlPublica + "/hechos").newBuilder()
                .addQueryParameter("pagina", String.valueOf(page))
                .addQueryParameter("limite", String.valueOf(size));

        // Agregar búsqueda general si existe
        if (textoBusqueda != null && !textoBusqueda.isBlank()) {
            b.addQueryParameter("textoBusqueda", textoBusqueda);
        }

        if (categoria != null && !categoria.isBlank())
            b.addQueryParameter("categoria", categoria);

        if (fechaCargaDesde != null && !fechaCargaDesde.isBlank())
            b.addQueryParameter("fecha_carga_desde", normalizarFecha(fechaCargaDesde));
        if (fechaCargaHasta != null && !fechaCargaHasta.isBlank())
            b.addQueryParameter("fecha_carga_hasta", normalizarFecha(fechaCargaHasta));

        if (fechaAcontecimientoDesde != null && !fechaAcontecimientoDesde.isBlank())
            b.addQueryParameter("fecha_acontecimiento_desde", normalizarFecha(fechaAcontecimientoDesde));
        if (fechaAcontecimientoHasta != null && !fechaAcontecimientoHasta.isBlank())
            b.addQueryParameter("fecha_acontecimiento_hasta", normalizarFecha(fechaAcontecimientoHasta));

        if (latitud != null && longitud != null && !latitud.isBlank() && !longitud.isBlank()) {
            b.addQueryParameter("latitud", latitud);
            b.addQueryParameter("longitud", longitud);
        }

        String finalUrl = b.build().toString();
        System.out.println("📡 Consultando backend: " + finalUrl);

        // 4) Ejecutar llamada HTTP al backend (espera PageDTO<HechoDTO>)
        Request request = new Request.Builder().url(finalUrl).get().build();

        PageDTO<HechoDTO> resp;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
            }
            String body = Objects.requireNonNull(response.body()).string();
            resp = mapper.readValue(body, new TypeReference<PageDTO<HechoDTO>>() {});
        }

        String json = mapper.writeValueAsString(resp.content);
        System.out.println("Lista en JSON:\n" + json);

        // 5) Índices para “Mostrando X–Y”
        int fromIndex = (resp.page - 1) * resp.size;                // 0-based
        int toIndex   = fromIndex + (resp.content != null ? resp.content.size() : 0);

        // 6) Modelo de datos para la vista
        Map<String, Object> model = new HashMap<>();
        model.put("baseHref", "/hechos");
        model.put("filters", filtersForTemplate); // ← Usar la versión convertida a Map

// Agrega el parámetro de búsqueda general 'q' que falta
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

//        Map<String, Object> model = new HashMap<>();
//        //model.put("usuario", obtenerUsuarioSesion(ctx)); DEPENDE COMO MANEJEMOS LA SESION
//        model.put("hechos", resp.content != null ? resp.content : Collections.emptyList());
//        model.put("categorias", obtenerCategorias());
//        model.put("fuentes", obtenerFuentes());
//        model.put("colecciones", obtenerColecciones());
//        model.put("paginaActual", 1);
//        model.put("totalPaginas", 10);
//        model.put("cargando", false);


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
