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
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
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
        System.out.println("Pidiendo coleccion al back: " + urlPublica);
        String url = HttpUrl.parse(urlPublica + "/colecciones/"+ id).newBuilder().build().toString();
        Request request = new Request.Builder().url(url).get().build();

        Coleccion resp;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
            }
            String body = Objects.requireNonNull(response.body()).string();
            resp = mapper.readValue(body, new TypeReference<Coleccion>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    public void crearColeccion(Map<String, Object> bodyData) {
        System.out.println("--- INICIO CREAR COLECCION (Service) ---");

        String username = bodyData.get("username") != null ? bodyData.get("username").toString() : null;
        String accessToken = bodyData.get("accessToken") != null ? bodyData.get("accessToken").toString() : null;
        String rolUsuario = bodyData.get("rolUsuario") != null ? bodyData.get("rolUsuario").toString() : null;

        bodyData.remove("username");
        bodyData.remove("accessToken");
        bodyData.remove("rolUsuario");

        String jsonBody = new Gson().toJson(bodyData);
        System.out.println("JSON a enviar: " + jsonBody);

        String baseUrl = urlAdmin.endsWith("/") ? urlAdmin : urlAdmin + "/";
        String finalUrl = baseUrl + "api/colecciones";
        System.out.println("URL Destino: " + finalUrl);

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(finalUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (username != null && accessToken != null && rolUsuario != null) {
                System.out.println("Agregando Headers de Seguridad:");
                System.out.println("   -> username: " + username);
                System.out.println("   -> rolUsuario: " + rolUsuario);
                System.out.println("   -> access_token: [PRESENTE]");

                requestBuilder
                        .header("username", username)
                        .header("accessToken", accessToken)
                        .header("rolUsuario", rolUsuario);
            } else {
                System.err.println("ALERTA: Faltan credenciales, se enviará petición anónima (probablemente falle con 401).");
                System.err.println("   Status: User=" + (username!=null) + ", Token=" + (accessToken!=null) + ", Rol=" + (rolUsuario!=null));
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta del servidor: " + response.statusCode());

            if (response.statusCode() != 201) {
                System.err.println("Cuerpo del error: " + response.body());
                throw new RuntimeException("Error al crear la coleccion, status code: " + response.statusCode());
            } else {
                System.out.println("Colección creada exitosamente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void eliminarColeccion( Map<String, Object> bodyData){
        System.out.println(bodyData);
        String username = null;
        String accessToken = null;
        String rolUsuario = null;
        if(bodyData.containsKey("username") && bodyData.containsKey("accessToken") && bodyData.containsKey("rolUsuario")){
            username = bodyData.get("username").toString();
            accessToken = bodyData.get("accessToken").toString();
            rolUsuario = bodyData.get("rolUsuario").toString();
            bodyData.remove("username");
            bodyData.remove("accessToken");
            bodyData.remove("rolUsuario");
        }
        String id = bodyData.get("id").toString();

        System.out.println("Mandando a: " + urlAdmin);
        HttpClient httpClient = HttpClient.newHttpClient();
        try{
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(urlAdmin + "api/colecciones/"+id))
                    .header("Content-Type", "application/json")
                    .DELETE();

            if (username != null && accessToken != null) {
                requestBuilder
                        .header("username", username)
                        .header("accessToken", accessToken)
                        .header("rolUsuario", rolUsuario);

            }
            HttpRequest request = requestBuilder.build();

            System.out.println("Se armo la request");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Mandamos la request");

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error al eliminar la coleccion, status code: " + response.statusCode());
            } else{
                System.out.println("Coleccion eliminada");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void actualizarColeccion(String id, Map<String, Object> bodyData){
        System.out.println(bodyData);
        String username = null;
        String accessToken = null;
        String rolUsuario = null;
        if(bodyData.containsKey("username") && bodyData.containsKey("accessToken") && bodyData.containsKey("rolUsuario")){
            username = bodyData.get("username").toString();
            accessToken = bodyData.get("accessToken").toString();
            rolUsuario = bodyData.get("rolUsuario").toString();
            bodyData.remove("username");
            bodyData.remove("accessToken");
            bodyData.remove("rolUsuario");
        }

        String jsonBody = new Gson().toJson(bodyData);

        System.out.println("JSON que se va a enviar: " + jsonBody);

        System.out.println("Mandando a: " + urlAdmin + "api/colecciones/"+id);
        HttpClient httpClient = HttpClient.newHttpClient();
        try{
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(urlAdmin + "api/colecciones/"+id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
            System.out.println("token" + accessToken);
            if (accessToken != null) {
                requestBuilder
                        .header("accessToken", accessToken);

            }
            HttpRequest request = requestBuilder.build();

            System.out.println("Se armo la request");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Mandamos la request");

            if (response.statusCode() != 200) {
                throw new RuntimeException("Error al actualizar la coleccion, status code: " + response.statusCode());
            } else{
                System.out.println("Coleccion actualizada");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


