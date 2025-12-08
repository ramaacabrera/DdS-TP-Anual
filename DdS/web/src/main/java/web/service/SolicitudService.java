package web.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SolicitudService {

    private final String urlAdmin;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    public SolicitudService(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    private HttpRequest buildRequestGET(String endpoint, String username, String token, String rolUsuario) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlAdmin + endpoint))
                .GET();

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("access_token", token);
            builder.header("rol_usuario", rolUsuario);
        }

        return builder.build();
    }

    private HttpRequest buildRequestPATCH(String endpoint, String json, String username, String token, String rolUsuario) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlAdmin + endpoint))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json));

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("access_token", token);
            builder.header("rol_usuario", rolUsuario);
        }

        return builder.build();
    }

    public SolicitudDeEliminacion obtenerSolicitudEliminacion(String id, String username, String token, String rolUsuario) {
        try {
            HttpRequest request = buildRequestGET("api/solicitudes/" + id, username, token, rolUsuario);
            System.out.println("Obtener solicitud de eliminacion 1: " + request);

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Obtener solicitud de eliminacion2: ");


            if (response.statusCode() == 200) {
                System.out.println("Obtener solicitud de eliminacion2: ");

                return mapper.readValue(response.body(), SolicitudDeEliminacion.class);
            }

            System.err.println("Error obteniendo solicitud eliminación: " + response.statusCode());
            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SolicitudDeModificacion obtenerSolicitudModificacion(String id, String username, String token, String rolUsuario) {
        try {
            HttpRequest request = buildRequestGET("api/solicitudes/modificacion/" + id, username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
                return mapper.readValue(response.body(), SolicitudDeModificacion.class);

            System.err.println("Error obteniendo solicitud modificación: " + response.statusCode());
            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<SolicitudDeEliminacion> obtenerSolicitudesEliminacion(String username, String token, String rolUsuario) {
        try {
            HttpRequest request = buildRequestGET("api/solicitudes", username, token, rolUsuario);
            System.out.println("Obtener solicitud de eliminacion (lista) 1: " + request);

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Obtener solicitud de eliminacion (lista) 2: " );


            if (response.statusCode() == 200) {
                System.out.println("Obtener solicitud de eliminacion (lista) 3: " );

                return mapper.readValue(response.body(),
                        new TypeReference<List<SolicitudDeEliminacion>>() {
                        });
            }

            System.err.println("Error obteniendo solicitudes eliminación: " + response.statusCode());
            return List.of();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<SolicitudDeModificacion> obtenerSolicitudesModificacion(String username, String token, String rolUsuario) {
        try {
            HttpRequest request = buildRequestGET("api/solicitudes-modificacion", username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
                return mapper.readValue(response.body(),
                        new TypeReference<List<SolicitudDeModificacion>>() {});

            System.err.println("Error obteniendo solicitudes modificación: " + response.statusCode());
            return List.of();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int actualizarEstadoSolicitud(String id, String tipo, String accion, String username, String token, String rolUsuario) {
        try {
            String esMod = tipo.equals("modificacion") ? "modificacion/" : "";
            String endpoint = "api/solicitudes/" + esMod + id;

            String json = mapper.writeValueAsString(Map.of("accion", accion));

            System.out.println(username);
            System.out.println(token);
            System.out.println(rolUsuario);

            HttpRequest request = buildRequestPATCH(endpoint, json, username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long contarPendientesEliminacion() {
        return 0;
    }

    public long contarPendientesModificacion() {
        return 0;
    }
}