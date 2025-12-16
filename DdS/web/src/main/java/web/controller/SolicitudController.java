package web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.dto.PageDTO;
import web.service.SolicitudService;
import web.service.UsuarioService;
import web.utils.ViewUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public class SolicitudController {

    private SolicitudService solicitudService;
    private UsuarioService usuarioService;
    private String urlPublica;
    private Map<String, Object> dataCloud;

    public SolicitudController(SolicitudService solicitudService, String urlPublica, UsuarioService usuarioService, Map<String, Object> dataCloud) {
        this.solicitudService = solicitudService;
        this.urlPublica = urlPublica;
        this.usuarioService = usuarioService;
        this.dataCloud = dataCloud;
    }

    public Handler listarSolicitudesEliminacion = ctx -> {
        System.out.println("Listando SOLO solicitudes de eliminación");

        int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
        int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

        String username = ctx.sessionAttribute("username");
        String accessToken = ctx.sessionAttribute("accessToken");
        String rolUsuario = ctx.sessionAttribute("rolUsuario");

        if (username == null || accessToken == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Solicitudes de Eliminación");

        PageDTO<SolicitudDeEliminacion> solicitudesPage = solicitudService.listarColecciones(username, accessToken, rolUsuario, page, size);

        System.out.println("Número de solicitudes eliminación: " + solicitudesPage.size);

        int fromIndex = (solicitudesPage.page - 1) * solicitudesPage.size;
        int toIndex = fromIndex + (solicitudesPage.content != null ? solicitudesPage.content.size() : 0);

        modelo.put("baseHref", "/admin/solicitudes/eliminacion");
        modelo.put("total", solicitudesPage.totalElements);
        modelo.put("page", solicitudesPage.page);
        modelo.put("size", solicitudesPage.size);
        modelo.put("totalPages", solicitudesPage.totalPages);
        modelo.put("fromIndex", fromIndex);
        modelo.put("toIndex", toIndex);

        List<Map<String, Object>> solicitudesSimplificadas = new ArrayList<>();

        for (SolicitudDeEliminacion solicitud : solicitudesPage.content) {
            Map<String, Object> solicitudData = new HashMap<>();

            solicitudData.put("id", solicitud.getId() != null ? solicitud.getId().toString() : "");
            solicitudData.put("justificacion", solicitud.getJustificacion() != null ? solicitud.getJustificacion() : "Sin justificación");
            solicitudData.put("estado", solicitud.getEstado() != null ? solicitud.getEstado() : "PENDIENTE");

            if (solicitud.getUsuarioId() != null) {
                Map<String, String> usuarioMap = new HashMap<>();
                usuarioMap.put("username", solicitud.getUsuarioId().getUsername() != null ? solicitud.getUsuarioId().getUsername() : "Usuario");
                solicitudData.put("usuario", usuarioMap);
            } else {
                solicitudData.put("usuario", null);
            }

            String hechoTitulo = "Sin título";
            String hechoIdStr = null;

            if (solicitud.getHechoAsociado() != null) {
                hechoIdStr = solicitud.getHechoAsociado().toString();
                solicitudData.put("hechoId", hechoIdStr);

                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(urlPublica + "/hechos/" + hechoIdStr))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> hechoData = mapper.readValue(response.body(), Map.class);
                        hechoTitulo = (String) hechoData.getOrDefault("titulo", "Sin título");
                    } else {
                        hechoTitulo = "Hecho eliminado o no encontrado";
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo hecho ID " + hechoIdStr + ": " + e.getMessage());
                    hechoTitulo = "Error cargando";
                }
            } else {
                hechoTitulo = "Sin hecho asociado";
            }

            solicitudData.put("hechoTitulo", hechoTitulo);
            solicitudesSimplificadas.add(solicitudData);
        }

        modelo.put("solicitudesEliminacion", solicitudesSimplificadas);
        modelo.put("solicitudesModificacion", new ArrayList<>());

        ctx.render("solicitudes.ftl", modelo);
    };

    public Handler obtenerSolicitud = ctx -> {
        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo");

        System.out.println("Obteniendo solicitud detalle ID: " + id + ", tipo: " + tipo);

        String username = ctx.sessionAttribute("username");
        String accessToken = ctx.sessionAttribute("accessToken");
        String rolUsuario = ctx.sessionAttribute("rolUsuario");

        if (username == null || accessToken == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Detalle de Solicitud");
        modelo.put("tipo", tipo);

        if ("eliminacion".equals(tipo)) {
            SolicitudDeEliminacion sol = solicitudService.obtenerSolicitudEliminacion(id, username, accessToken, rolUsuario);

            System.out.println(new ObjectMapper().writeValueAsString(sol));

            if (sol == null) {
                ctx.status(404).result("Solicitud no encontrada");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> solicitudData = mapper.convertValue(sol, Map.class);

            String estadoStr = "PENDIENTE";
            if (sol.getEstado() != null) {
                estadoStr = sol.getEstado().name();
                System.out.println("Estado obtenido (ENUM): " + sol.getEstado() + ", como String: " + estadoStr);
            }

            solicitudData.put("estado", estadoStr);

            Map<String, Object> hechoData = new HashMap<>();
            if (sol.getHechoAsociado() != null) {
                try {
                    String hechoIdStr = sol.getHechoAsociado().toString();
                    System.out.println("Buscando hecho para detalle: " + hechoIdStr);

                    HttpClient client = HttpClient.newHttpClient();
                    String urlCompleta = urlPublica + "/hechos/" + hechoIdStr;
                    System.out.println("URL del hecho: " + urlCompleta);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(urlCompleta))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Response status: " + response.statusCode());

                    if (response.statusCode() == 200) {
                        hechoData = mapper.readValue(response.body(), Map.class);

                        if (hechoData.containsKey("fechaDeCarga")) {
                            Object fechaObj = hechoData.get("fechaDeCarga");
                            if (fechaObj != null) {
                                try {
                                    if (fechaObj instanceof Number) {
                                        long timestamp = ((Number) fechaObj).longValue();
                                        Date fecha = new Date(timestamp);
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        hechoData.put("fechaDeCargaFormateada", sdf.format(fecha));
                                    } else if (fechaObj instanceof String) {
                                        hechoData.put("fechaDeCargaFormateada", fechaObj.toString());
                                    }
                                } catch (Exception e) {
                                    System.err.println("Error formateando fecha: " + e.getMessage());
                                    hechoData.put("fechaDeCargaFormateada", "Fecha inválida");
                                }
                            }
                        }
                    } else {
                        System.err.println("Error obteniendo hecho: " + response.statusCode());
                        hechoData.put("titulo", "Error al cargar el hecho");
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo hecho para detalle: " + e.getMessage());
                    e.printStackTrace();
                    hechoData.put("titulo", "Error de conexión");
                }
            } else {
                hechoData.put("titulo", "Sin hecho asociado");
            }

            modelo.put("solicitud", solicitudData);
            modelo.put("hecho", hechoData);

        } else if ("modificacion".equals(tipo)) {
            SolicitudDeModificacion sol = solicitudService.obtenerSolicitudModificacion(id, username, accessToken, rolUsuario);

            if (sol == null) {
                ctx.status(404).result("Solicitud no encontrada");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> solicitudData = mapper.convertValue(sol, Map.class);
            System.out.println("Data solicitud modificacion obtenida: " +  mapper.convertValue(sol, Map.class));

            Map<String, Object> hechoData = new HashMap<>();
            if (sol.getHechoAsociado() != null) {
                try {
                    String hechoIdStr = sol.getHechoAsociado().toString();
                    HttpClient client = HttpClient.newHttpClient();
                    String urlCompleta = urlPublica + "/hechos/" + hechoIdStr;

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(urlCompleta))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        System.out.println("Data hecho modificacion obtenida: "+ mapper.readValue(response.body(), Map.class));
                        hechoData = mapper.readValue(response.body(), Map.class);
                    } else {
                        hechoData.put("titulo", "Error al cargar el hecho");
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo hecho para detalle (mod): " + e.getMessage());
                    hechoData.put("titulo", "Error de conexión");
                }
            }

            modelo.put("solicitud", solicitudData);
            modelo.put("hecho", hechoData);
        } else {
            ctx.status(400).result("Tipo inválido");
            return;
        }

        ctx.render("solicitud-detalle.ftl", modelo);
    };

    public Handler obtenerFormsEliminarSolicitud = ctx -> {
        String hechoId = ctx.pathParam("id");

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Solicitar Eliminación");
        modelo.put("hechoId", hechoId);
        modelo.put("urlPublica", urlPublica);

        ctx.render("crear-solicitud-eliminacion.ftl", modelo);
    };

    public Handler actualizarEstadoSolicitud = ctx -> {
        String id = ctx.pathParam("id");
        String tipo = ctx.pathParam("tipo");
        String body = ctx.body();
        System.out.println("Body recibido: " + body);

        if (body == null || body.trim().isEmpty()) {
            ctx.status(400).result("Cuerpo de solicitud vacío");
            return;
        }

        String nuevoEstado;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            if (jsonNode.has("accion")) {
                nuevoEstado = jsonNode.get("accion").asText();
            } else if (jsonNode.isTextual()) {
                nuevoEstado = jsonNode.asText();
            } else {
                ctx.status(400).result("Propiedad 'accion' no encontrada en JSON");
                return;
            }
        } catch (Exception e) {
            System.out.println("No es JSON válido, tratando como texto: " + body);
            nuevoEstado = body.trim();
        }

        System.out.println("ID: " + id + ", Tipo: " + tipo + ", Nuevo estado: " + nuevoEstado);

        if (!"ACEPTADA".equals(nuevoEstado) && !"RECHAZADA".equals(nuevoEstado)) {
            ctx.status(400).result("Estado no válido. Use 'ACEPTADA' o 'RECHAZADA'");
            return;
        }

        String username = ctx.sessionAttribute("username");
        String accessToken = ctx.sessionAttribute("accessToken");
        String rolUsuario = ctx.sessionAttribute("rolUsuario");

        if (username == null || accessToken == null) {
            ctx.status(401).result("Sesión expirada. Inicie sesión nuevamente.");
            return;
        }

        int status = solicitudService.actualizarEstadoSolicitud(id, tipo, nuevoEstado, username, accessToken, rolUsuario);

        if (status >= 200 && status < 300) {
            ctx.status(200).result("Solicitud actualizada");
        } else {
            ctx.status(status).result("Error del servidor administrativo: HTTP " + status);
        }
    };

    public Handler listarSolicitudesModificacion = ctx -> {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient http = HttpClient.newHttpClient();
        System.out.println("Listando SOLO solicitudes de modificación");

        String username = ctx.sessionAttribute("username");
        String accessToken = ctx.sessionAttribute("accessToken");
        String rolUsuario = ctx.sessionAttribute("rolUsuario");
        System.out.println("entra a try 1");
        if (username == null || accessToken == null) {
            ctx.redirect("/login");
            return;
        }

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Solicitudes de Modificación");

        List<SolicitudDeModificacion> solicitudesMod =
                solicitudService.obtenerSolicitudesModificacion(username, accessToken, rolUsuario);

        List<Map<String, Object>> solicitudesSimplificadas = new ArrayList<>();

        for (SolicitudDeModificacion solicitud : solicitudesMod) {
            Map<String, Object> solicitudData = new HashMap<>();

            solicitudData.put("id", solicitud.getId() != null ? solicitud.getId().toString() : "");
            solicitudData.put("justificacion", solicitud.getJustificacion() != null ? solicitud.getJustificacion() : "Sin justificación");
            solicitudData.put("estado", solicitud.getEstado() != null ? solicitud.getEstado().name() : "PENDIENTE");

            if (solicitud.getUsuarioId() != null) {
                Map<String, String> usuarioMap = new HashMap<>();
                usuarioMap.put("username", solicitud.getUsuarioId().getUsername() != null ? solicitud.getUsuarioId().getUsername() : "Usuario");
                solicitudData.put("usuario", usuarioMap);
            } else {
                solicitudData.put("usuario", null);
            }

            String hechoTitulo = "Sin título";
            String hechoIdStr = null;

            if (solicitud.getHechoAsociado() != null) {
                System.out.println("entra a if antes de request");
                hechoIdStr = solicitud.getHechoAsociado().toString();
                solicitudData.put("hechoId", hechoIdStr);

                try {
                    System.out.println("entra a try antes de request");
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(urlPublica + "/hechos/" + hechoIdStr))
                            .GET()
                            .build();

                    HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        Map<String, Object> hechoData = mapper.readValue(response.body(), Map.class);
                        hechoTitulo = (String) hechoData.getOrDefault("titulo", "Sin título");
                    } else {
                        hechoTitulo = "Hecho eliminado o no encontrado (HTTP " + response.statusCode() + ")";
                    }
                } catch (Exception e) {
                    System.err.println("Error obteniendo hecho ID " + hechoIdStr + ": " + e.getMessage());
                    hechoTitulo = "Error de conexión o lectura";
                }
            } else {
                hechoTitulo = "Sin hecho asociado";
            }

            solicitudData.put("hechoTitulo", hechoTitulo);

            if (solicitud.getHechoModificado() != null) {
                Map<String, Object> hechoModificadoMap = mapper.convertValue(solicitud.getHechoModificado(), Map.class);
                solicitudData.put("hechoModificado", hechoModificadoMap);
            } else {
                solicitudData.put("hechoModificado", null);
            }

            solicitudesSimplificadas.add(solicitudData);
        }

        modelo.put("solicitudesModificacion", solicitudesSimplificadas);

        ctx.render("solicitudes-mod.ftl", modelo);
    };

    public Handler obtenerFormsModificarSolicitud = ctx -> {
        String accessToken = ctx.sessionAttribute("accessToken");
        if(accessToken == null || accessToken.isEmpty()) {
            ctx.redirect("/login");
            return;
        }

        String hechoId = ctx.pathParam("id");
        String username = ctx.sessionAttribute("username");

        String usuarioId = usuarioService.obtenerId(username);

        System.out.println("ID UUID del usuario recuperado para el form: " + usuarioId);

        Map<String, Object> hechoData = solicitudService.obtenerDatosHecho(hechoId);

        if (hechoData == null || hechoData.isEmpty()) {
            ctx.status(404).result("Hecho no encontrado (" + hechoId + ") o no disponible para modificación.");
            return;
        }

        List<String> categorias = solicitudService.obtenerCategorias();

        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Solicitar Modificación");
        modelo.put("hechoId", hechoId);
        modelo.put("hecho", hechoData);

        modelo.put("usuarioId", usuarioId);

        modelo.put("categorias", categorias);
        modelo.put("urlPublica", urlPublica);

        if (dataCloud != null) {
            modelo.put("cloudinaryUrl", dataCloud.get("cloudinaryUrl"));
            modelo.put("cloudinaryPreset", dataCloud.get("cloudinaryPreset"));
        }

        ctx.render("crear-solicitud-modificacion.ftl", modelo);
    };

    public Handler crearSolicitudModificacion = ctx -> {
        System.out.println(">>> [WebController] Inicio crearSolicitudModificacion");
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            System.out.println(">>> [WebController] Body parseado correctamente");

            String hechoId = (String) body.get("hechoId");
            String usuarioId = (String) body.get("usuarioId");
            String justificacion = (String) body.get("justificacion");
            Map<String, Object> hechoModificado = (Map<String, Object>) body.get("hechoModificado");

            if (hechoId == null || hechoId.isBlank()){
                ctx.status(400).json(Map.of("error", "Falta hechoId"));
                return;
            }
            if (usuarioId == null || usuarioId.isBlank()){
                ctx.status(400).json(Map.of("error", "Falta usuarioId"));
                return;
            }

            String username = ctx.sessionAttribute("username");
            String accessToken = ctx.sessionAttribute("accessToken");
            String rolUsuario = ctx.sessionAttribute("rolUsuario");

            if (accessToken == null) {
                ctx.status(401).json(Map.of("error", "Sesión expirada"));
                return;
            }

            if (!usuarioId.matches("[0-9a-fA-F-]{36}")) {
                System.out.println(">>> [WebController] UUID inválido ('"+usuarioId+"'). Recuperando...");
                usuarioId = usuarioService.obtenerId(username);
                if (usuarioId == null) {
                    ctx.status(500).json(Map.of("error", "No se pudo recuperar el UUID del usuario"));
                    return;
                }
            }

            System.out.println(">>> [WebController] Llamando a SolicitudService con UUID: " + usuarioId);

            int status = solicitudService.crearSolicitudModificacion(
                    hechoId,
                    usuarioId,
                    justificacion,
                    hechoModificado,
                    username,
                    accessToken,
                    rolUsuario
            );

            System.out.println(">>> [WebController] Respuesta del servicio. Status: " + status);

            if (status >= 200 && status < 300) {
                ctx.status(201).json(Map.of("mensaje", "Solicitud enviada"));
            } else {
                ctx.status(status).json(Map.of("error", "El Gestor respondió con error HTTP " + status));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(">>> [WebController] EXCEPCION: " + e.getMessage());
            ctx.status(500).json(Map.of("error", "Error interno Web: " + e.getMessage()));
        }
    };
}