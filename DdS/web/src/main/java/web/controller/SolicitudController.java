package web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import web.domain.Solicitudes.SolicitudDeEliminacion;
import web.domain.Solicitudes.SolicitudDeModificacion;
import web.service.SolicitudService;
import web.utils.ViewUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public class SolicitudController {

    private SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    public Handler listarSolicitudes = ctx -> {
        try {
            System.out.println("Listando SOLO solicitudes de eliminación");

            String username = ctx.sessionAttribute("username");
            String accessToken = ctx.sessionAttribute("accessToken");
            String rolUsuario = ctx.sessionAttribute("rolUsuario");

            if (username == null || accessToken == null) {
                ctx.redirect("/login");
                return;
            }

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Solicitudes de Eliminación");

            // 1. Obtener solicitudes de eliminación
            List<SolicitudDeEliminacion> solicitudesElim =
                    solicitudService.obtenerSolicitudesEliminacion(username, accessToken, rolUsuario);

            System.out.println("Número de solicitudes eliminación: " + solicitudesElim.size());

            // 2. Crear lista simplificada
            List<Map<String, Object>> solicitudesSimplificadas = new ArrayList<>();

            for (SolicitudDeEliminacion solicitud : solicitudesElim) {
                Map<String, Object> solicitudData = new HashMap<>();

                // Datos básicos obligatorios
                solicitudData.put("id", solicitud.getId() != null ? solicitud.getId().toString() : "");
                solicitudData.put("justificacion", solicitud.getJustificacion() != null ? solicitud.getJustificacion() : "Sin justificación");
                solicitudData.put("estado", solicitud.getEstado() != null ? solicitud.getEstado() : "PENDIENTE");

                // Usuario - simplificado
                if (solicitud.getUsuario() != null) {
                    Map<String, String> usuarioMap = new HashMap<>();
                    usuarioMap.put("username", solicitud.getUsuario().getUsername() != null ? solicitud.getUsuario().getUsername() : "Usuario");
                    solicitudData.put("usuario", usuarioMap);
                } else {
                    solicitudData.put("usuario", null);
                }

                // Hecho - manejar el título
                String hechoTitulo = "Sin título";
                String hechoIdStr = null;

                if (solicitud.getHechoAsociado() != null) {
                    hechoIdStr = solicitud.getHechoAsociado().toString();
                    solicitudData.put("hechoId", hechoIdStr);

                    // Solo intentar obtener el título si el hecho existe
                    try {
                        // Verificar si el hecho existe primero
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI("http://localhost:8087/api/hechos/" + hechoIdStr))
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

                // Debug
                System.out.println("Solicitud procesada: ID=" + solicitudData.get("id") +
                        ", Estado=" + solicitudData.get("estado") +
                        ", Título hecho=" + hechoTitulo);
            }

            modelo.put("solicitudesEliminacion", solicitudesSimplificadas);

            // Para modificación, simplemente lista vacía por ahora
            modelo.put("solicitudesModificacion", new ArrayList<>());

            ctx.render("solicitudes.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error listando Solicitudes: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar solicitudes: " + e.getMessage());
        }
    };

    public Handler obtenerSolicitud = ctx -> {
        try {
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

                // Convertir la solicitud a un Map para poder agregar campos extras
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> solicitudData = mapper.convertValue(sol, Map.class);

                // Obtener el estado como String del ENUM
                String estadoStr = "PENDIENTE";
                if (sol.getEstado() != null) {
                    estadoStr = sol.getEstado().name(); // Esto convierte el ENUM a String: "ACEPTADA", "RECHAZADA", "PENDIENTE"
                    System.out.println("Estado obtenido (ENUM): " + sol.getEstado() + ", como String: " + estadoStr);
                }

                solicitudData.put("estado", estadoStr);

                System.out.println("DEBUG - Campo 'estado' agregado como String: " + solicitudData.get("estado"));

                // Obtener datos del hecho
                Map<String, Object> hechoData = new HashMap<>();
                if (sol.getHechoAsociado() != null) {
                    try {
                        String hechoIdStr = sol.getHechoAsociado().toString();
                        System.out.println("Buscando hecho para detalle: " + hechoIdStr);

                        HttpClient client = HttpClient.newHttpClient();
                        String urlCompleta = "http://localhost:8087/api/hechos/" + hechoIdStr;
                        System.out.println("URL del hecho: " + urlCompleta);

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(urlCompleta))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println("Response status: " + response.statusCode());

                        if (response.statusCode() == 200) {
                            hechoData = mapper.readValue(response.body(), Map.class);
                            System.out.println("Datos del hecho obtenidos: " + hechoData.keySet());

                            // Formatear la fecha si existe
                            if (hechoData.containsKey("fechaDeCarga")) {
                                Object fechaObj = hechoData.get("fechaDeCarga");
                                if (fechaObj != null) {
                                    try {
                                        if (fechaObj instanceof Number) {
                                            // Si es un número (timestamp), convertirlo a fecha formateada
                                            long timestamp = ((Number) fechaObj).longValue();
                                            Date fecha = new Date(timestamp);
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            hechoData.put("fechaDeCargaFormateada", sdf.format(fecha));
                                        } else if (fechaObj instanceof String) {
                                            // Si ya es string, dejarlo como está
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
                // Similar para modificación
                SolicitudDeModificacion sol = solicitudService.obtenerSolicitudModificacion(id, username, accessToken, rolUsuario);

                if (sol == null) {
                    ctx.status(404).result("Solicitud no encontrada");
                    return;
                }

                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> solicitudData = mapper.convertValue(sol, Map.class);

                Map<String, Object> hechoData = new HashMap<>();
                if (sol.getHechoAsociado() != null) {
                    try {
                        String hechoIdStr = sol.getHechoAsociado().toString();
                        HttpClient client = HttpClient.newHttpClient();
                        String urlCompleta = "http://localhost:8087/api/hechos/" + hechoIdStr;

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(urlCompleta))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
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

        } catch (Exception e) {
            System.err.println("Error obtenerSolicitud: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar la solicitud: " + e.getMessage());
        }
    };


    public Handler obtenerFormsEliminarSolicitud = ctx -> {
        try {
            String hechoId = ctx.pathParam("id");

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Solicitar Eliminación");
            modelo.put("hechoId", hechoId);

            ctx.render("crear-solicitud-eliminacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error obtenerFormsEliminarSolicitud: " + e.getMessage());
            ctx.status(500).result("Error al cargar formulario: " + e.getMessage());
        }
    };

    public Handler actualizarEstadoSolicitud = ctx -> {
        try {
            String id = ctx.pathParam("id");
            String tipo = ctx.pathParam("tipo");

            // Parsear el cuerpo JSON
            String body = ctx.body();
            System.out.println("Body recibido: " + body);

            // Si el body está vacío o no es JSON válido
            if (body == null || body.trim().isEmpty()) {
                ctx.status(400).result("Cuerpo de solicitud vacío");
                return;
            }

            // Intentar parsear como JSON
            String nuevoEstado;
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(body);

                // Intentar obtener la propiedad "accion"
                if (jsonNode.has("accion")) {
                    nuevoEstado = jsonNode.get("accion").asText();
                } else if (jsonNode.isTextual()) {
                    // Si es solo texto (para compatibilidad)
                    nuevoEstado = jsonNode.asText();
                } else {
                    ctx.status(400).result("Propiedad 'accion' no encontrada en JSON");
                    return;
                }
            } catch (Exception e) {
                // Si falla el parseo JSON, tratar el body como texto plano
                System.out.println("No es JSON válido, tratando como texto: " + body);
                nuevoEstado = body.trim();
            }

            System.out.println("ID: " + id + ", Tipo: " + tipo + ", Nuevo estado: " + nuevoEstado);

            // Validar el estado
            if (!"ACEPTADA".equals(nuevoEstado) && !"RECHAZADA".equals(nuevoEstado)) {
                ctx.status(400).result("Estado no válido. Use 'ACEPTADA' o 'RECHAZADA'");
                return;
            }

            // Obtener datos de sesión
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

        } catch (Exception e) {
            System.err.println("Error actualizarEstadoSolicitud: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al actualizar: " + e.getMessage());
        }
    };
}