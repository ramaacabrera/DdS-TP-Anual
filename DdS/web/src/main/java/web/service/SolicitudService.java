package web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import web.domain.Solicitudes.EstadoSolicitudEliminacion;
import web.domain.Solicitudes.EstadoSolicitudModificacion;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.dto.PageDTO;

import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SolicitudService {

    private final String urlAdmin;
    private final String urlPublica;
    private final HechoService hechoService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final OkHttpClient http;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    public SolicitudService(String urlAdmin, String urlPublica, HechoService hechoService) {
        this.urlPublica = urlPublica;
        this.urlAdmin = urlAdmin;
        this.hechoService = hechoService;
        this.http = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private Request buildRequestGET(String endpoint, String username, String token, String rolUsuario) {
        System.out.println("Haciendo GET a: " + urlAdmin + endpoint);
        Request.Builder builder = new Request.Builder()
                .url(urlAdmin + endpoint)
                .get();

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }

        return builder.build();
    }

    private Request buildRequestPATCH(String endpoint, String json, String username, String token, String rolUsuario) {
        System.out.println("Consulto a la url: " + urlAdmin + endpoint);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request.Builder builder = new Request.Builder()
                .url(urlAdmin + endpoint)
                .patch(body);

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }

        return builder.build();
    }

    public long contarSolicitudes(String username, String rolUsuario, String accessToken, EstadoSolicitudEliminacion estado) {
        try {
            String url = "api/solicitudes/" + URLEncoder.encode(estado.toString(), "UTF-8") + "/cantidad";
            System.out.println("Pidiendo cantidad a: " + url);
            Request request = buildRequestGET(url, username, accessToken, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    return Integer.parseInt(response.body().string());
                }
            }

        } catch (Exception e) {
            System.err.println("Error al contar solicitudes: " + e.getMessage());
        }

        return 0;
    }

    public long contarSolicitudesModificacion(String username, String rolUsuario, String accessToken, EstadoSolicitudModificacion estado) {
        try {
            String url = "api/solicitudes-modificacion/" + URLEncoder.encode(estado.toString(), "UTF-8") + "/cantidad";
            System.out.println("Pidiendo cantidad a: " + url);
            Request request = buildRequestGET(url, username, accessToken, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    return Integer.parseInt(response.body().string());
                }
            }

        } catch (Exception e) {
            System.err.println("Error al contar solicitudes: " + e.getMessage());
        }

        return 0;
    }

    public SolicitudDeEliminacion obtenerSolicitudEliminacion(String id, String username, String token, String rolUsuario) {
        try {
            Request request = buildRequestGET("api/solicitudes/" + id, username, token, rolUsuario);
            System.out.println("Obtener solicitud de eliminacion 1: " + request);

            try (Response response = http.newCall(request).execute()) {
                System.out.println("Obtener solicitud de eliminacion2: ");

                if (response.code() == 200 && response.body() != null) {
                    System.out.println("Obtener solicitud de eliminacion2: ");
                    return mapper.readValue(response.body().string(), SolicitudDeEliminacion.class);
                }

                System.err.println("Error obteniendo solicitud eliminación: " + response.code());
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SolicitudDeModificacion obtenerSolicitudModificacion(String id, String username, String token, String rolUsuario) {
        try {
            Request request = buildRequestGET("api/solicitudes/modificacion/" + id, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null)
                    return mapper.readValue(response.body().string(), SolicitudDeModificacion.class);

                System.err.println("Error obteniendo solicitud modificación: " + response.code());
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageDTO<SolicitudDeEliminacion> listarSolicitudes(String username, String token, String rolUsuario, int pagina, int size) {
        try {
            System.out.println("Pidiendo solicitudes de eliminacion a: " + urlAdmin);
            Request request = buildRequestGET("api/solicitudes?pagina=" + pagina + "&limite=" + size, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    return mapper.readValue(response.body().string(),
                            new TypeReference<PageDTO<SolicitudDeEliminacion>>() {});
                }

                System.err.println("Error obteniendo solicitudes de eliminacion: " + response.code());
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageDTO<SolicitudDeEliminacion> listarPorEstado(String username, String token, String rolUsuario,
                                                           int pagina, int size, String estado) {
        try {
            System.out.println("Pidiendo solicitudes " + estado + " de eliminacion a: " + urlAdmin);
            String url = "api/solicitudes/estado/" + estado + "?pagina=" + pagina + "&limite=" + size;
            Request request = buildRequestGET(url, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    String bodyString = response.body().string();
                    System.out.println("Las Solicitudes se obtuvieron exitosamente: " + bodyString);

                    return mapper.readValue(bodyString,
                            new TypeReference<PageDTO<SolicitudDeEliminacion>>() {});
                }

                System.err.println("Error obteniendo solicitudes de eliminacion: " + response.code());
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageDTO<SolicitudDeModificacion> obtenerSolicitudesModificacion(String username, String token, String rolUsuario, int page, int size, String estado) {
        try {
            String url = "api/solicitudes/modificacion/listado?estado=" + estado + "&pagina=" + page + "&limite=" + size;
            Request request = buildRequestGET(url, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    String bodyString = response.body().string();
                    System.out.println("Las Solicitudes se obtuvieron exitosamente: " + bodyString);

                    return mapper.readValue(bodyString,
                            new TypeReference<PageDTO<SolicitudDeModificacion>>() {});
                }

                System.err.println("Error obteniendo solicitudes de eliminacion: " + response.code());
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PageDTO<SolicitudDeModificacion> listarSolicitudesModificacion(String username, String token, String rolUsuario, int pagina, int size) {
        try {
            System.out.println("entra  try 1 service");
            Request request = buildRequestGET("api/solicitudes/modificacion/listado?pagina=" + pagina + "&limite=" + size, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                if (response.code() == 200 && response.body() != null) {
                    System.out.println("Solicitudes obtenidas exitosamente");
                    return mapper.readValue(response.body().string(),
                            new TypeReference<PageDTO<SolicitudDeModificacion>>() {});
                }

                System.err.println("Error obteniendo solicitudes modificación: " + response.code());
                return null;
            }

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

            Request request = buildRequestPATCH(endpoint, json, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                return response.code();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int actualizarSolicitud(String id, List<Map<String, String>> cambios, String username, String token, String rolUsuario) {
        try {
            String json = mapper.writeValueAsString(Map.of("cambiosAprobados", cambios));

            Request request = buildRequestPATCH("api/solicitudes-modificacion/" + id, json, username, token, rolUsuario);

            try (Response response = http.newCall(request).execute()) {
                return response.code();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> obtenerDatosHecho(String hechoId) throws Exception {
        String hechoEndpoint = urlPublica;
        if (!hechoEndpoint.endsWith("/")) hechoEndpoint += "/";

        String urlCompleta = hechoEndpoint + "hechos/" + hechoId;

        Request request = new Request.Builder()
                .url(urlCompleta)
                .get()
                .build();

        try (Response response = http.newCall(request).execute()) {
            if (response.code() == 200 && response.body() != null) {
                return mapper.readValue(response.body().string(), new TypeReference<Map<String, Object>>() {});
            }
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

            RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(urlPublica + "/solicitudModificacion")
                    .post(body)
                    .build();

            try (Response response = http.newCall(request).execute()) {
                return response.code();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }

    private Request buildRequestPOST(String endpoint, String json, String username, String token, String rolUsuario) {
        String finalUrl = urlAdmin;
        if (!finalUrl.endsWith("/")) {
            finalUrl += "/";
        }

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request.Builder builder = new Request.Builder()
                .url(finalUrl + endpoint)
                .post(body);

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }
        return builder.build();
    }

    private Request buildRequestPublicaPOST(String endpoint, String json, String username, String token, String rolUsuario) {
        String finalUrl = urlPublica;
        if (!finalUrl.endsWith("/")) {
            finalUrl += "/";
        }

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request.Builder builder = new Request.Builder()
                .url(finalUrl + endpoint)
                .post(body);

        if (username != null && token != null) {
            builder.header("username", username);
            builder.header("accessToken", token);
            builder.header("rolUsuario", rolUsuario);
        }
        return builder.build();
    }

    public int contarPendientesEliminacion(String username, String rolUsuario, String token) throws Exception {
        Request request = buildRequestGET("api/solicitudes/cantidad", username, token, rolUsuario);

        try (Response response = http.newCall(request).execute()) {
            if (response.code() == 200 && response.body() != null) {
                return Integer.parseInt(response.body().string());
            }
        }
        return 0;
    }

    public int contarPendientesModificacion(String username, String rolUsuario, String token) throws Exception {
        Request request = buildRequestGET("api/solicitudes-modificacion/cantidad", username, token, rolUsuario);

        try (Response response = http.newCall(request).execute()) {
            if (response.code() == 200 && response.body() != null) {
                return Integer.parseInt(response.body().string());
            }
        }
        return 0;
    }

    public List<String> obtenerCategorias() {
        return hechoService.obtenerCategorias();
    }
}