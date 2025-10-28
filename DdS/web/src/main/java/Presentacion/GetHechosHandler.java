package Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.DTO.PageDTO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetHechosHandler implements Handler {

    private final String urlPublica;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    public GetHechosHandler(String url) {
        this.urlPublica = url;
    }

    // --- Definición declarativa de filtros ---
    public static class FilterDef {
        public String key, label, type;
        public List<String> options;
        public List<Option> optionObjs;
        public String placeholder;
        public Integer size;
        public static class Option { public String value, label; }
    }

    @Override
    public void handle(Context ctx) throws IOException {
        // 1) Filtros para la UI
        List<FilterDef> filters = buildFilters();

        // 2) Leer filtros y paginación desde la query
        String categoria = ctx.queryParam("categoria");
        String fechaCargaDesde = ctx.queryParam("fecha_carga_desde");
        String fechaCargaHasta = ctx.queryParam("fecha_carga_hasta");
        String fechaAcontecimientoDesde = ctx.queryParam("fecha_acontecimiento_desde");
        String fechaAcontecimientoHasta = ctx.queryParam("fecha_acontecimiento_hasta");
        String latitud = ctx.queryParam("latitud");
        String longitud = ctx.queryParam("longitud");

        int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
        int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

        // 3) Construir URL del backend con los nombres EXACTOS que recibe + paginación
        HttpUrl.Builder b = HttpUrl.parse(urlPublica + "/hechos").newBuilder()
                .addQueryParameter("page", String.valueOf(page))
                .addQueryParameter("size", String.valueOf(size));

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

        // 5) Índices para “Mostrando X–Y”
        int fromIndex = (resp.page - 1) * resp.size;                // 0-based
        int toIndex   = fromIndex + (resp.content != null ? resp.content.size() : 0);

        // 6) Modelo de datos para la vista
        Map<String, Object> model = new HashMap<>();
        model.put("baseHref", "/api/hechos");
        model.put("filters", filters);
        model.put("filterValues", Map.of(
                "categoria", categoria != null ? categoria : "",
                "fecha_carga_desde", fechaCargaDesde != null ? fechaCargaDesde : "",
                "fecha_carga_hasta", fechaCargaHasta != null ? fechaCargaHasta : "",
                "fecha_acontecimiento_desde", fechaAcontecimientoDesde != null ? fechaAcontecimientoDesde : "",
                "fecha_acontecimiento_hasta", fechaAcontecimientoHasta != null ? fechaAcontecimientoHasta : "",
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

        ctx.render("home.ftl", model);
    }

    private String normalizarFecha(String raw) {
        // Si ya viene en dd/MM/yyyy, devolver igual
        if (raw.matches("\\d{2}/\\d{2}/\\d{4}")) return raw;

        // Si viene en yyyy-MM-dd (por ejemplo, <input type="date">)
        if (raw.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse(raw);
                return formato.format(parsed);
            } catch (Exception e) {
                return raw;
            }
        }
        return raw;
    }

    private List<FilterDef> buildFilters() {
        List<FilterDef> list = new ArrayList<>();

        FilterDef cat = new FilterDef();
        cat.key = "categoria";
        cat.label = "Categoría";
        cat.type = "select";
        cat.options = Arrays.asList("Política", "Economía", "Sociedad", "Cultura", "Deportes");
        list.add(cat);

        FilterDef fechaCargaDesde = new FilterDef();
        fechaCargaDesde.key = "fecha_carga_desde";
        fechaCargaDesde.label = "Fecha carga desde";
        fechaCargaDesde.type = "date";
        list.add(fechaCargaDesde);

        FilterDef fechaCargaHasta = new FilterDef();
        fechaCargaHasta.key = "fecha_carga_hasta";
        fechaCargaHasta.label = "Fecha carga hasta";
        fechaCargaHasta.type = "date";
        list.add(fechaCargaHasta);

        FilterDef fechaAcontecimientoDesde = new FilterDef();
        fechaAcontecimientoDesde.key = "fecha_acontecimiento_desde";
        fechaAcontecimientoDesde.label = "Acontecimiento desde";
        fechaAcontecimientoDesde.type = "date";
        list.add(fechaAcontecimientoDesde);

        FilterDef fechaAcontecimientoHasta = new FilterDef();
        fechaAcontecimientoHasta.key = "fecha_acontecimiento_hasta";
        fechaAcontecimientoHasta.label = "Acontecimiento hasta";
        fechaAcontecimientoHasta.type = "date";
        list.add(fechaAcontecimientoHasta);

        FilterDef lat = new FilterDef();
        lat.key = "latitud";
        lat.label = "Latitud";
        lat.type = "search";
        list.add(lat);

        FilterDef lon = new FilterDef();
        lon.key = "longitud";
        lon.label = "Longitud";
        lon.type = "search";
        list.add(lon);

        return list;
    }

    // DTO para parsear respuesta del backend
    public static class HechoDTO {
        public String id;
        public String titulo;
        public String descripcion;
        public String categoria;
        public String fechaDeCarga;
        public String fechaDeAcontecimiento;
        public String latitud;
        public String longitud;
    }
}
