package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.DTO.PageDTO;
import domain.HechosYColecciones.Coleccion;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import domain.DTO.ColeccionDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ColeccionService {

    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Timeout para establecer conexión
            .readTimeout(120, TimeUnit.SECONDS)     // Timeout para lectura de datos (el que te está fallando)
            .writeTimeout(60, TimeUnit.SECONDS)    // Timeout para escritura
            .build();

    public ColeccionService(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    public PageDTO<Coleccion> listarColecciones(int page, int size) {
        HttpUrl.Builder b = HttpUrl.parse(urlPublica + "/colecciones").newBuilder()
                .addQueryParameter("pagina", String.valueOf(page))
                .addQueryParameter("limite", String.valueOf(size));

        String finalUrl = b.build().toString();
        Request request = new Request.Builder().url(finalUrl).get().build();

        PageDTO<Coleccion> resp;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
            }
            String body = Objects.requireNonNull(response.body()).string();
            System.out.println(body);
            resp = mapper.readValue(body, new TypeReference<PageDTO<Coleccion>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    public Coleccion obtenerColeccionPorId(String id) {
        String url = HttpUrl.parse(urlPublica + "/colecciones/"+ id).newBuilder().build().toString();
        Request request = new Request.Builder().url(url).get().build();

        Coleccion resp;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
            }
            String body = Objects.requireNonNull(response.body()).string();
            System.out.println(body);
            resp = mapper.readValue(body, new TypeReference<Coleccion>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resp;
    }
}
