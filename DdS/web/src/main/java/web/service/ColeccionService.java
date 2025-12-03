package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import web.dto.PageDTO;
import web.domain.HechosYColecciones.Coleccion;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ColeccionService {

    private final String urlPublica;
    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Timeout para establecer conexión
            .readTimeout(120, TimeUnit.SECONDS)     // Timeout para lectura de datos (el que te está fallando)
            .writeTimeout(60, TimeUnit.SECONDS)    // Timeout para escritura
            .build();

    public ColeccionService(String urlPublica, String urlAdmin) {
        this.urlPublica = urlPublica;
        this.urlAdmin = urlAdmin;
    }

    public PageDTO<Coleccion> listarColecciones(int page, int size) {
        System.out.println("Pidiendo colecciones a: " + urlPublica);
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

    public void crearColeccion(Map<String, Object> bodyData) {
        System.out.println(bodyData);
        String username = null;
        String access_token = null;
        if(bodyData.containsKey("username") && bodyData.containsKey("access_token")){
            username = bodyData.get("username").toString();
            access_token = bodyData.get("access_token").toString();
            bodyData.remove("username");
            bodyData.remove("access_token");
        }
        String jsonBody = new Gson().toJson(bodyData);

        System.out.println("Serializacion: " + jsonBody);

        System.out.println("Mandando a: " + urlAdmin);
        HttpClient httpClient = HttpClient.newHttpClient();
        try{
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/api/colecciones"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (username != null && access_token != null) {
                requestBuilder
                        .header("username", username)
                        .header("access_token", access_token);

            }
            HttpRequest request = requestBuilder.build();

            System.out.println("Se armo la request");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Mandamos la request");

            Map<String, Object> modelo = new HashMap<>();
            if (response.statusCode() != 201) {
                throw new RuntimeException("Error al crear la coleccion, status code: " + response.statusCode());
            } else{
                System.out.println("Coleccion creada");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


