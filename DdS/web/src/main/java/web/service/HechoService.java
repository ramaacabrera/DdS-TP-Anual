package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import web.domain.hechosycolecciones.Hecho;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HechoService {

    private ObjectMapper mapper = new ObjectMapper();
    private final String urlPublica;


    public  HechoService(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    public Hecho obtenerHechoPorId(String hechoIdString) {
        try{
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

            return mapper.readValue(response.body(), new TypeReference<>() {});

        }catch(Exception e){
            System.err.println("Error al obtener el hecho" + e.getMessage());
            return null;
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


