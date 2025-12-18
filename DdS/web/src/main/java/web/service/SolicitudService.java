package web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import web.domain.HechosYColecciones.Coleccion;
import web.domain.Solicitudes.EstadoSolicitudEliminacion;
import web.domain.Solicitudes.EstadoSolicitudModificacion;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.dto.PageDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SolicitudService {

    private final String urlAdmin;
    private final String urlPublica;
    private final HechoService hechoService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    public SolicitudService(String urlAdmin,String urlPublica, HechoService hechoService) {
        this.urlPublica = urlPublica;
        this.urlAdmin = urlAdmin;
        this.hechoService = hechoService;
    }

    private HttpRequest buildRequestGET(String endpoint, String username, String token, String rolUsuario) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlAdmin + endpoint))
                .GET();
        System.out.println("Haciendo GET a: " + urlAdmin + endpoint);

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }

        return builder.build();
    }

    private HttpRequest buildRequestPATCH(String endpoint, String json, String username, String token, String rolUsuario) throws Exception {
        System.out.println("Consulto a la url: " + urlAdmin + endpoint);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlAdmin + endpoint))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json));

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }

        return builder.build();
    }

    public long contarSolicitudes(String username, String rolUsuario, String accessToken, EstadoSolicitudEliminacion estado){
        try{
            String url = "api/solicitudes/" + URLEncoder.encode(estado.toString(), "UTF-8") + "/cantidad";
            System.out.println("Pidiendo cantidad a: " + url);
            HttpRequest request = buildRequestGET(url, username, accessToken, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                return Integer.parseInt(response.body());
            }

        } catch (Exception e){
            System.err.println("Error al contar solicitudes: " + e.getMessage());
        }

        return 0;
    }

    public long contarSolicitudesModificacion(String username, String rolUsuario, String accessToken, EstadoSolicitudModificacion estado){
        try{
            String url = "api/solicitudes-modificacion/" + URLEncoder.encode(estado.toString(), "UTF-8") + "/cantidad";
            System.out.println("Pidiendo cantidad a: " + url);
            HttpRequest request = buildRequestGET(url, username, accessToken, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                return Integer.parseInt(response.body());
            }

        } catch (Exception e){
            System.err.println("Error al contar solicitudes: " + e.getMessage());
        }

        return 0;
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


    public PageDTO<SolicitudDeEliminacion> listarSolicitudes(String username, String token, String rolUsuario, int pagina, int size) {
        try {
            System.out.println("Pidiendo solicitudes de eliminacion a: " + urlAdmin);
            HttpRequest request = buildRequestGET("api/solicitudes?pagina=" + pagina + "&limite=" + size, username, token, rolUsuario);

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                return mapper.readValue(response.body(),
                        new TypeReference<PageDTO<SolicitudDeEliminacion>>() {
                        });
            }

            System.err.println("Error obteniendo solicitudes de eliminacion: " +  response.statusCode());

            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageDTO<SolicitudDeEliminacion> listarPorEstado(String username, String token, String rolUsuario,
            int pagina, int size, String estado){
        try {
            System.out.println("Pidiendo solicitudes " + estado + " de eliminacion a: " + urlAdmin);
            String url = "api/solicitudes/estado/" + estado + "?pagina=" + pagina + "&limite=" + size;
            HttpRequest request = buildRequestGET(url, username, token, rolUsuario);

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Las Solicitudes se obtuvieron exitosamente: " + response.body());

                return mapper.readValue(response.body(),
                        new TypeReference<PageDTO<SolicitudDeEliminacion>>() {
                        });
            }

            System.err.println("Error obteniendo solicitudes de eliminacion: " +  response.statusCode());

            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageDTO<SolicitudDeModificacion> obtenerSolicitudesModificacion(String username, String token, String rolUsuario, int page, int size, String estado) {

        try{
            String url = "api/solicitudes/modificacion/listado?estado=" + estado + "&pagina=" + page + "&limite=" + size;
            HttpRequest request = buildRequestGET(url, username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Las Solicitudes se obtuvieron exitosamente: " + response.body());

                return mapper.readValue(response.body(),
                        new TypeReference<PageDTO<SolicitudDeModificacion>>() {
                        });
            }

            System.err.println("Error obteniendo solicitudes de eliminacion: " +  response.statusCode());

            return null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public PageDTO<SolicitudDeModificacion> listarSolicitudesModificacion(String username, String token, String rolUsuario, int pagina, int size) {
        try {
            System.out.println("entra  try 1 service");

            HttpRequest request = buildRequestGET("api/solicitudes/modificacion/listado?pagina=" + pagina + "&limite=" + size, username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Solicitudes obtenidas exitosamente");
                return mapper.readValue(response.body(),
                        new TypeReference<PageDTO<SolicitudDeModificacion>>() {
                        });
            }

            System.err.println("Error obteniendo solicitudes modificación: " + response.statusCode());

            return null;

        } catch (Exception e) {
            System.out.println("fallo acaaa ");
            throw new RuntimeException(e);

        }
    }


    public int actualizarEstadoSolicitud(String id, String tipo, String accion, String username, String token, String rolUsuario) {
        try {
            String esMod = tipo.equals("modificacion") ? "modificacion/" : "";
            String endpoint = "api/solicitudes/" + esMod + id;

            String json = mapper.writeValueAsString(Map.of("accion", accion));

            HttpRequest request = buildRequestPATCH(endpoint, json, username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int actualizarSolicitud(String id, List<Map<String, String>> cambios, String username, String token, String rolUsuario){
        try{

            String json = mapper.writeValueAsString(Map.of("cambiosAprobados", cambios));

            HttpRequest request = buildRequestPATCH("api/solicitudes-modificacion/" + id, json, username, token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> obtenerDatosHecho(String hechoId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient http = HttpClient.newHttpClient();
        String hechoEndpoint = urlPublica;
        if (!hechoEndpoint.endsWith("/")) hechoEndpoint += "/";

        String urlCompleta = hechoEndpoint + "hechos/" + hechoId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlCompleta))
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        }


        return null;
    }
    public int crearSolicitudModificacion(String hechoId,
                                          String usuarioId,
                                          String justificacion,
                                          Map<String, Object> hechoModificado,
                                          String username,
                                          String accessToken,
                                          String rolUsuario) {
        try {
            Map<String, Object> payload = new HashMap<>();

            payload.put("hechoId", hechoId);

            payload.put("usuarioId", usuarioId);

            payload.put("justificacion", justificacion);
            payload.put("hechoModificado", hechoModificado);

            payload.put("username", username);
            payload.put("accessToken", accessToken);

            String jsonBody = mapper.writeValueAsString(payload);

            System.out.println("WEB SERVICE -> Enviando a Gestor: " + jsonBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlPublica + "/solicitudModificacion"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode();

        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }

    private HttpRequest buildRequestPOST(String endpoint, String json, String username, String token, String rolUsuario) throws Exception {
        String finalUrl = urlAdmin;
        if (!finalUrl.endsWith("/")) {
            finalUrl += "/";
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(finalUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }
        return builder.build();
    }

    private HttpRequest buildRequestPublicaPOST(String endpoint, String json, String username, String token, String rolUsuario) throws Exception {
        String finalUrl = urlPublica;
        if (!finalUrl.endsWith("/")) {
            finalUrl += "/";
        }

        System.out.println(new URI(finalUrl + endpoint));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(finalUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }
        return builder.build();
    }

    public int contarPendientesEliminacion(String username, String rolUsuario, String token) throws Exception {
        HttpRequest request = buildRequestGET("api/solicitudes/cantidad", username, token, rolUsuario);
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            return Integer.parseInt(response.body());
        }
        return 0;
    }

    public int contarPendientesModificacion(String username, String rolUsuario, String token) throws Exception {
        HttpRequest request = buildRequestGET("api/solicitudes-modificacion/cantidad", username, token, rolUsuario);
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200) {
            return Integer.parseInt(response.body());
        }
        return 0;
    }

    public List<String> obtenerCategorias() {
        return hechoService.obtenerCategorias();
    }
}