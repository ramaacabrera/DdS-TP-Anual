package web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import web.domain.HechosYColecciones.Coleccion;
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

    public SolicitudController(SolicitudService solicitudService, String urlPublica,  UsuarioService usuarioService) {
        this.solicitudService = solicitudService;
        this.urlPublica = urlPublica;
        this.usuarioService = usuarioService;
    }

    public Handler listarSolicitudesEliminacion = ctx -> {
        try {
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

            // 1. Obtener solicitudes de eliminación
            PageDTO<SolicitudDeEliminacion> solicitudesPage = solicitudService.listarColecciones(username, accessToken, rolUsuario, page, size);
            /*
            List<SolicitudDeEliminacion> solicitudesElim =
                    solicitudService.obtenerSolicitudesEliminacion(username, accessToken, rolUsuario);

             */

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

            // 2. Crear lista simplificada
            List<Map<String, Object>> solicitudesSimplificadas = new ArrayList<>();

            for (SolicitudDeEliminacion solicitud : solicitudesPage.content) {
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
                        String urlCompleta = urlPublica + "/hechos/" + hechoIdStr;

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
            modelo.put("urlPublica", urlPublica);

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


    public Handler listarSolicitudesModificacion = ctx -> {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient http = HttpClient.newHttpClient();
        try {
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

            // 2. Crear lista simplificada (para agregar el título del hecho)
            List<Map<String, Object>> solicitudesSimplificadas = new ArrayList<>();

            for (SolicitudDeModificacion solicitud : solicitudesMod) {
                Map<String, Object> solicitudData = new HashMap<>();

                // Datos básicos
                solicitudData.put("id", solicitud.getId() != null ? solicitud.getId().toString() : "");
                solicitudData.put("justificacion", solicitud.getJustificacion() != null ? solicitud.getJustificacion() : "Sin justificación");

                // Estado: Usar el ENUM como String
                solicitudData.put("estado", solicitud.getEstado() != null ? solicitud.getEstado().name() : "PENDIENTE");

                // Usuario
                if (solicitud.getUsuario() != null) {
                    Map<String, String> usuarioMap = new HashMap<>();
                    usuarioMap.put("username", solicitud.getUsuario().getUsername() != null ? solicitud.getUsuario().getUsername() : "Usuario");
                    solicitudData.put("usuario", usuarioMap);
                } else {
                    solicitudData.put("usuario", null);
                }

                // === Manejo de Hecho (Título) ===
                String hechoTitulo = "Sin título";
                String hechoIdStr = null;

                if (solicitud.getHechoAsociado() != null) {
                    System.out.println("entra a if antes de request");
                    hechoIdStr = solicitud.getHechoAsociado().toString();
                    solicitudData.put("hechoId", hechoIdStr);

                    try {
                        System.out.println("entra a try antes de request");
                        // Obtener el título del hecho del servicio de hechos
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

                // === Manejo de los Cambios (HechoModificado) ===
                // Convertir la entidad HechoModificado a un Map/JSON para que Freemarker lo pueda usar
                if (solicitud.getHechoModificado() != null) {
                    // Aquí se asume que HechoModificado tiene campos que Freemarker listará (como en la respuesta anterior)
                    // Si HechoModificado necesita ser transformado en una lista simple de {campo, anterior, nuevo},
                    // esa lógica de transformación debería ocurrir aquí.

                    // Por ahora, lo pasamos directamente, asumiendo que contiene los datos que necesitamos:
                    Map<String, Object> hechoModificadoMap = mapper.convertValue(solicitud.getHechoModificado(), Map.class);

                    // IMPORTANTÍSIMO: Debemos añadir una lista iterable de cambios (como en el FTL)
                    // Como el modelo de dominio no proporciona una lista de cambios simple,
                    // la lista debe ser construida aquí.
                    // EJEMPLO: (Recomendación: Mueve esta lógica al componente administrativo)
                    List<Map<String, String>> listaDeCambios = new ArrayList<>();

                    // Simulación de la obtención de la lista de cambios a partir del hechoModificado
                    // (Esta parte es altamente dependiente de la estructura JSON que envíe el admin)
                    // Si el componente administrativo ya envía la lista de cambios, no necesitas esta simulación.
                    // Si no, debes iterar sobre las propiedades de 'hechoModificadoMap' y crear la lista.

                    // Aquí usamos el hechoModificado original para pasarlo a Freemarker,
                    // y el template de Freemarker intentará acceder a `solicitud.hechoModificado.cambios`
                    solicitudData.put("hechoModificado", hechoModificadoMap);
                } else {
                    solicitudData.put("hechoModificado", null);
                }


                solicitudesSimplificadas.add(solicitudData);
            }

            modelo.put("solicitudesModificacion", solicitudesSimplificadas);

            // 3. Renderizar el template específico de modificación
            ctx.render("solicitudes-mod.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error listando Solicitudes de Modificación: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar solicitudes de modificación: " + e.getMessage());
        }
    };
    public Handler obtenerFormsModificarSolicitud = ctx -> {
        String accessToken = ctx.sessionAttribute("accessToken");
        if(accessToken == null || accessToken.isEmpty()) {
            ctx.redirect("/login");
        }
        try {
            String hechoId = ctx.pathParam("id");
            String username = ctx.sessionAttribute("username");


            String usuarioId = usuarioService.obtenerId(username); // ID del usuario logueado (UUID)

            System.out.println("ID del usuario: " + usuarioId);

            /*if (username == null || accessToken == null || usuarioId == null) {
                System.out.println("Intento de modificar sin sesión. Redirigiendo.");
                ctx.redirect("/login");
                return;
            }*/

            // 2. Obtener los datos del hecho original
            Map<String, Object> hechoData = solicitudService.obtenerDatosHecho(hechoId);

            if (hechoData == null || hechoData.isEmpty()) {
                ctx.status(404).result("Hecho no encontrado (" + hechoId + ") o no disponible para modificación.");
                return;
            }

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Solicitar Modificación");
            modelo.put("hechoId", hechoId);
            modelo.put("hecho", hechoData); // Contiene el título, descripción, etc. originales
            modelo.put("usuarioId", usuarioId);
            modelo.put("urlPublica", urlPublica);


            ctx.render("crear-solicitud-modificacion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("Error obtenerFormsModificarSolicitud: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar formulario de modificación: " + e.getMessage());
        }
    };

    public Handler crearSolicitudModificacion = ctx -> {
        try {
            System.out.println(ctx.body());
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            String hechoId = (String) body.get("hechoId");
            String usuarioId = (String) body.get("usuarioId");
            String justificacion = (String) body.get("justificacion");

            Map<String, Object> hechoModificado = (Map<String, Object>) body.get("hechoModificado");

            if (hechoId == null || hechoId.isBlank()){ //|| usuarioId == null || usuarioId.isBlank()) {
                ctx.status(400).json(Map.of("error", "Los IDs de hecho y usuario son obligatorios."));
                return;
            }

            if (justificacion == null || justificacion.isBlank()) {
                ctx.status(400).json(Map.of("error", "La justificación es obligatoria."));
                return;
            }

            if (hechoModificado == null || hechoModificado.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Debe proponer una modificación en al menos un campo."));
                return;
            }
            String username = ctx.sessionAttribute("username");
            String accessToken = ctx.sessionAttribute("accessToken");
            String rolUsuario = ctx.sessionAttribute("rolUsuario");

            if (accessToken == null) {
                ctx.status(401).json(Map.of("error", "Sesión expirada. Inicie sesión nuevamente."));
                return;
            }

            int status = solicitudService.crearSolicitudModificacion(
                    hechoId,
                    usuarioId,
                    justificacion,
                    hechoModificado,
                    username,
                    accessToken,
                    rolUsuario
            );

            if (status == 201) {
                ctx.status(201).json(Map.of("mensaje", "Solicitud de modificación enviada exitosamente."));
            } else {
                ctx.status(status).json(Map.of("error", "Error al procesar la solicitud en el servidor publico. HTTP: " + status));
            }

        } catch (Exception e) {
            System.err.println("Error interno al procesar POST de solicitud de modificación: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).json(Map.of("error", "Error interno al procesar la solicitud."));
        }
    };
}