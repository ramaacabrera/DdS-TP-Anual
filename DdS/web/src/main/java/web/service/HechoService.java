package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import web.domain.hechosycolecciones.Hecho;
import web.dto.HechoDTO;
import web.dto.PageDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HechoService {

    private final String urlPublica;
    private final OkHttpClient client;
    private final ObjectMapper mapper;


    public HechoService(String urlPublica) {
        this.urlPublica = urlPublica;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public Hecho obtenerHechoPorId(String hechoIdString) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            URI uri = null;
            try {
                uri = new URI(urlPublica + "/hechos/" + hechoIdString);
            } catch (URISyntaxException e) {
                System.err.println("URI invalido " + e.getMessage());
                throw new RuntimeException(e);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), new TypeReference<>() {
            });

        } catch (Exception e) {
            System.err.println("Error al obtener el hecho" + e.getMessage());
            return null;
        }
    }

    public PageDTO<HechoDTO> buscarHechos(Map<String, String> filtros, int page, int size) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(urlPublica + "/hechos").newBuilder()
                .addQueryParameter("pagina", String.valueOf(page))
                .addQueryParameter("limite", String.valueOf(size));

        // Aplicar filtros dinámicos
        filtros.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                // Normalizamos fechas antes de enviar si la clave corresponde a una fecha
                if (key.startsWith("fecha_")) {
                    urlBuilder.addQueryParameter(key, normalizarFecha(value));
                } else {
                    urlBuilder.addQueryParameter(key, value);
                }
            }
        });

        String finalUrl = urlBuilder.build().toString();
        System.out.println("📡 Consultando backend: " + finalUrl);

        Request request = new Request.Builder().url(finalUrl).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error backend: " + response.code());
            }
            String body = Objects.requireNonNull(response.body()).string();
            return mapper.readValue(body, new TypeReference<PageDTO<HechoDTO>>() {});
        }
    }

    public List<String> obtenerCategorias() {
        HttpUrl url = HttpUrl.parse(urlPublica + "/categoria");
        if (url == null) return new ArrayList<>();

        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return mapper.readValue(response.body().string(), new TypeReference<List<String>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo categorías: " + e.getMessage());
        }
        return new ArrayList<>();
    }


    public String normalizarFecha(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        try {
            if (raw.matches("\\d{2}/\\d{2}/\\d{4}")) return raw;

            if (raw.matches("\\d{4}-\\d{2}-\\d{2}")) {
                SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy");
                return out.format(in.parse(raw));
            }
            return raw;
        } catch (Exception e) {
            return raw;
        }
    }

    public String formatearFechaParaInput(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) return "";
        try {
            if (rawDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
                SimpleDateFormat in = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");
                return out.format(in.parse(rawDate));
            }
            return rawDate;
        } catch (Exception e) {
            return "";
        }
    }

    public PageDTO<HechoDTO> obtenerHechos(Map<String, Object> queryParams) {
        String finalUrl = this.armarQueryHechos(queryParams);
        System.out.println("📡 Consultando backend: " + finalUrl);

        Request request = new Request.Builder().url(finalUrl).get().build();

        PageDTO<HechoDTO> resp;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
            }
            String body = Objects.requireNonNull(response.body()).string();
            return mapper.readValue(body, new TypeReference<PageDTO<HechoDTO>>() {});
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String armarQueryHechos(Map<String, Object> queryParams) {
        HttpUrl.Builder b = HttpUrl.parse(urlPublica + "/hechos").newBuilder()
                .addQueryParameter("pagina", String.valueOf(queryParams.get("page")))
                .addQueryParameter("limite", String.valueOf(queryParams.get("size")));

        String textoBusqueda = queryParams.get("textoBusqueda").toString();
        String categoria = queryParams.get("categoria").toString();
        String fechaCargaDesde = queryParams.get("fechaCargaDesde").toString();
        String fechaCargaHasta = queryParams.get("fechaCargaHasta").toString();
        String fechaAcontecimientoDesde = queryParams.get("fechaAcontecimientoDesde").toString();
        String fechaAcontecimientoHasta = queryParams.get("fechaAcontecimientoHasta").toString();
        String latitud = queryParams.get("latitud").toString();
        String longitud = queryParams.get("longitud").toString();


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

        return b.build().toString();
    }
}


