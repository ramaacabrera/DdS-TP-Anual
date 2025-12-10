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
import java.util.*;

public class SolicitudService {

    private final String urlAdmin;
    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    public SolicitudService(String urlAdmin,String urlPublica) {
        this.urlPublica = urlPublica;
        this.urlAdmin = urlAdmin;
    }

    private HttpRequest buildRequestGET(String endpoint, String username, String token, String rolUsuario) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlAdmin + endpoint))
                .GET();

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
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
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
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
            //System.out.println("Obtener solicitud de eliminacion (lista) 1: " + request);

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("Obtener solicitud de eliminacion (lista) 2: " );


            if (response.statusCode() == 200) {
                //System.out.println("Obtener solicitud de eliminacion (lista) 3: " );

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
            System.out.println("entra  try 1 service");
            HttpRequest request = buildRequestGET("api/solicitudes/modificacion/listado", username,token, rolUsuario);
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
                return mapper.readValue(response.body(),
                        new TypeReference<List<SolicitudDeModificacion>>() {});

           System.err.println("Error obteniendo solicitudes modificación: " + response.statusCode());
            return List.of();

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
    public int crearSolicitudModificacion(
            String hechoId, String usuarioId, String justificacion,
            Map<String, String> cambiosPropuestos,
            String username, String token, String rolUsuario)
    {
        try {
            // 1. Crear el DTO anidado 'HechoModificadoDTO' (que es un mapa plano de cambios)
            Map<String, String> hechoModificadoDTO = new HashMap<>();
            Map<String, String> ubicacionDTO = new HashMap<>();

            cambiosPropuestos.forEach((campo, valor) -> {
                // Manejo de Ubicación (campos anidados)
                if (campo.startsWith("ubicacion.")) {

                    String subCampo = campo.substring("ubicacion.".length());
                    ubicacionDTO.put(subCampo, valor);
                }
                // Manejo de Multimedia (IGNORAR COMENTARIO MULTIMEDIA AQUÍ - Opcional B)
                else if (campo.equals("multimediaComentario")) {
                    // Ignorar, ya que HechoModificado no tiene este campo.
                    // Si la entidad SolicitudDeModificacion tiene un campo para esto,
                    // se añade al mapa 'solicitudDTO' en el paso 3.
                }
                // Manejo de campos simples (titulo, descripcion, categoria, fechaDeAcontecimiento)
                else {
                    hechoModificadoDTO.put(campo, valor);
                }
            });

            // 3. Añadir la ubicación si se propone algún cambio (lat o lon)
            if (!ubicacionDTO.isEmpty()) {
               // hechoModificadoDTO.put("ubicacion", ubicacionDTO);
            }

            // 4. Crear el DTO principal 'SolicitudDeModificacionDTO'
            Map<String, Object> solicitudDTO = new HashMap<>();
            solicitudDTO.put("hechoId", UUID.fromString(hechoId));
            solicitudDTO.put("usuarioId", UUID.fromString(usuarioId));
            solicitudDTO.put("justificacion", justificacion);
            solicitudDTO.put("hechoModificado", hechoModificadoDTO);


            String jsonBody = mapper.writeValueAsString(solicitudDTO);

            // 3. Construir y enviar la solicitud POST al endpoint del Componente Publico
            // Endpoint: POST /api/solicitudes/modificacion
            HttpRequest request = buildRequestPOST(
                    "api/solicitudes/modificacion",
                    jsonBody,
                    username,
                    token,
                    rolUsuario
            );

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201) {
                System.err.println("Error Admin creando solicitud mod: HTTP " + response.statusCode() + " Body: " + response.body());
            }

            return response.statusCode();

        } catch (Exception e) {
            System.err.println("Error fatal al crear solicitud de modificación: " + e.getMessage());
            throw new RuntimeException("Error al crear solicitud de modificación.", e);
        }
    }

    // Método auxiliar para construir solicitudes POST (similar a buildRequestPATCH)
    private HttpRequest buildRequestPOST(String endpoint, String json, String username, String token, String rolUsuario) throws Exception {
        String finalUrl = urlAdmin;
        if (!finalUrl.endsWith("/")) {
            finalUrl += "/";
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(finalUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        // Asumimos que los headers de seguridad son CamelCase (ajustado en debug)
        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }
        return builder.build();
    }

    public long contarPendientesEliminacion() {
        return 0;
    }

    public long contarPendientesModificacion() {
        return 0;
    }
}