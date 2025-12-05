package web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import utils.Dominio.HechosYColecciones.Ubicacion;
import web.domain.Fuente.TipoDeFuente; // Solo para el enum de Criterios si se usa
import okhttp3.Request;
import okhttp3.Response;
import web.domain.Criterios.CriterioUbicacion;
import utils.Dominio.Usuario.Usuario;
import web.domain.Criterios.Criterio;
import web.domain.Criterios.TipoDeTexto;
import web.domain.Fuente.TipoDeFuente;
import web.domain.HechosYColecciones.Coleccion;
import web.domain.HechosYColecciones.TipoAlgoritmoConsenso;
import web.dto.Hechos.FuenteDTO; // Importante: Tu DTO
import web.dto.PageDTO;
import web.service.ColeccionService;
import web.service.FuenteService; // Importante: El servicio nuevo
import web.service.UsuarioService;
import web.utils.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColeccionController {

    private ColeccionService coleccionService;
    private UsuarioService usuarioService;
    private FuenteService fuenteService;

    public ColeccionController(ColeccionService coleccionService, UsuarioService usuarioService, FuenteService fuenteService) {
        this.coleccionService = coleccionService;
        this.usuarioService = usuarioService;
        this.fuenteService = fuenteService;
    }

    public Handler listarColecciones = ctx -> {
        try {
            int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
            int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

            PageDTO<Coleccion> coleccionesPage = coleccionService.listarColecciones(page, size);

            int fromIndex = (coleccionesPage.page - 1) * coleccionesPage.size;
            int toIndex = fromIndex + (coleccionesPage.content != null ? coleccionesPage.content.size() : 0);

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Colecciones");
            modelo.put("total", coleccionesPage.totalElements);
            modelo.put("page", coleccionesPage.page);
            modelo.put("size", coleccionesPage.size);
            modelo.put("totalPages", coleccionesPage.totalPages);
            modelo.put("fromIndex", fromIndex);
            modelo.put("toIndex", toIndex);
            modelo.put("colecciones", coleccionesPage.content);

            ctx.render("colecciones.ftl", modelo);

        } catch (Exception e) {
            ctx.status(500).result("Error al cargar las colecciones: " + e.getMessage());
        }
    };

    public Handler obtenerColeccionPorId = ctx -> {
        try {
            String coleccionId = ctx.pathParam("id");
            Coleccion coleccion = coleccionService.obtenerColeccionPorId(coleccionId);

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Detalle de Colección");
            modelo.put("coleccion", coleccion);
            modelo.put("coleccionId", coleccionId);

            ctx.render("coleccion.ftl", modelo);
        } catch (Exception e) {
            ctx.status(500).result("Error al cargar la colección: " + e.getMessage());
        }
    };

    public Handler obtenerPageCrearColeccion = ctx -> {
        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Crear nueva colección");
        modelo.put("algoritmos", TipoAlgoritmoConsenso.values());

        String username = ctx.sessionAttribute("username");
        String token = ctx.sessionAttribute("access_token");

        System.out.println("--- DEBUG CONTROLLER ---");
        System.out.println("Username en sesion: " + username);
        System.out.println("Token en sesion: " + (token != null ? "SI (Longitud: " + token.length() + ")" : "NULL"));

        List<FuenteDTO> listaFuentes = fuenteService.listarFuentes();

        modelo.put("listaFuentes", listaFuentes);
        modelo.put("tiposDeFuente", TipoDeFuente.values());

        ctx.render("crear-coleccion.ftl", modelo);
    };

    public Handler obtenerPageEditarColeccion = ctx -> {
        try {
            String coleccionId = ctx.pathParam("id");

            if(ctx.sessionAttributeMap().isEmpty()){
                ctx.status(500).result("Administrador no identificado");
                return;
            }

            Coleccion coleccion = coleccionService.obtenerColeccionPorId(coleccionId);

            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Editar colección");
            modelo.put("coleccion", coleccion);
            modelo.put("COLECCION_ID", coleccionId);
            modelo.put("algoritmos", TipoAlgoritmoConsenso.values());

            String username = ctx.sessionAttribute("username");
            String token = ctx.sessionAttribute("access_token");
            List<FuenteDTO> listaFuentes = fuenteService.listarFuentes();

            modelo.put("listaFuentes", listaFuentes);
            modelo.put("tiposDeFuente", TipoDeFuente.values());

            ctx.render("editar-coleccion.ftlh", modelo);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    };

    public Handler crearColeccion = ctx -> {
        Map<String, Object> bodyData = new HashMap<>();
        List<Map<String, Object>> criteriosDePertenencia = new ArrayList<>();
        this.extraerCriterios(ctx.formParamMap(), criteriosDePertenencia);

        bodyData.put("titulo", ctx.formParam("titulo"));
        bodyData.put("descripcion", ctx.formParam("descripcion"));
        bodyData.put("algoritmoDeConsenso", ctx.formParam("algoritmo"));

        bodyData.put("fuentes", ctx.formParams("fuentes"));

        bodyData.put("criteriosDePertenencia", criteriosDePertenencia);
        bodyData.put("username", ctx.sessionAttribute("username"));
        bodyData.put("access_token", ctx.sessionAttribute("access_token"));
        bodyData.put("rolUsuario", ctx.sessionAttribute("rolUsuario"));

        try {
            coleccionService.crearColeccion(bodyData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    public Handler editarColeccion = ctx -> {
        String id = ctx.pathParam("id");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(ctx.body());
        JsonNode criteriosNode = rootNode.get("criteriosDePertenencia");

        Map<String, Object> bodyData = new HashMap<>();
        List<Map<String, Object>> criteriosDePertenencia = new ArrayList<>();
        this.extraer(criteriosNode, criteriosDePertenencia);

        bodyData.put("criteriosDePertenencia", criteriosDePertenencia);
        bodyData.put("titulo", rootNode.get("titulo").asText());
        bodyData.put("descripcion", rootNode.get("descripcion").asText());
        bodyData.put("algoritmoDeConsenso", rootNode.get("algoritmoDeConsenso").asText());

        List<String> fuentes = new ArrayList<>();
        for(JsonNode fuente : rootNode.get("fuentes")){
            fuentes.add(fuente.asText());
        }
        bodyData.put("fuentes", fuentes);

        bodyData.put("coleccionId", id);
        bodyData.put("username", ctx.sessionAttribute("username"));
        bodyData.put("access_token", ctx.sessionAttribute("access_token"));
        bodyData.put("rolUsuario", ctx.sessionAttribute("rolUsuario"));

        coleccionService.actualizarColeccion(id, bodyData);
    };

    public Handler eliminarColeccion = ctx -> {
        String id = ctx.pathParam("id");
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("id", id);
        if(!ctx.sessionAttributeMap().isEmpty()){
            bodyData.put("username", ctx.sessionAttribute("username"));
            bodyData.put("access_token", ctx.sessionAttribute("access_token"));
            bodyData.put("rolUsuario", ctx.sessionAttribute("rolUsuario"));
        }
        coleccionService.eliminarColeccion(bodyData);
    };

    public void extraerCriterios(Map<String, List<String>> mapBody, List<Map<String, Object>> criteriosDePertenencia) {
        for(Map.Entry<String, List<String>> entry : mapBody.entrySet()){
            if(entry.getKey().startsWith("criterios[")){
                String key = entry.getKey();
                String criterioId = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                String campo = key.substring(key.indexOf('.') + 1);

                Map<String, Object> criterio = criteriosDePertenencia.stream()
                        .filter(c -> c.get("_id").equals(criterioId))
                        .findFirst()
                        .orElseGet(() -> {
                            Map<String, Object> nuevoCriterio = new HashMap<>();
                            nuevoCriterio.put("_id", criterioId);
                            criteriosDePertenencia.add(nuevoCriterio);
                            return nuevoCriterio;
                        });

                if(campo.equals("palabras") || campo.equals("etiquetas")) {
                    criterio.put(campo, entry.getValue());
                } else{
                    criterio.put(campo, entry.getValue().get(0));
                }
            }
        };

        for (Map<String, Object> criterio : criteriosDePertenencia) {
            criterio.remove("_id");
            if(criterio.get("@type").equals("CriterioUbicacion")){
                System.out.println("Ubicacion: " + criterio.get("ubicacion.descripcion").toString());
                Ubicacion ubicacion = new Ubicacion(0, 0, criterio.get("ubicacion.descripcion").toString());
                System.out.println("Descripcion de la ubicacion: " +  ubicacion.getDescripcion());
                criterio.put("ubicacion", ubicacion);
                criterio.remove("ubicacion.descripcion");
            }
        }
    }

    private void extraer(JsonNode body, List<Map<String, Object>> criterios){
        for(JsonNode criterio : body){
            Map<String, Object> map = new HashMap<>();
            map.put("@type", criterio.get("@type").asText());
            // ... (resto de tu lógica de extracción de JSON se mantiene igual) ...
            switch (criterio.get("@type").asText()){
                case "CriterioDeTexto":
                    map.put("tipoDeTexto", criterio.get("tipoDeTexto").asText());
                    List<String> palabras = new ArrayList<>();
                    JsonNode palabrasNode = criterio.get("palabras");
                    for(JsonNode palabra : palabrasNode){
                        palabras.add(palabra.asText());
                    }
                    map.put("palabras", palabras);
                    break;
                case "CriterioTipoMultimedia":
                    map.put("tipoContenidoMultimedia",criterio.get("tipoContenidoMultimedia").asText());
                    break;
                case "CriterioEtiquetas":
                    List<Map<String, String>> etiquetas = new ArrayList<>();
                    JsonNode etiquetasNode = criterio.get("etiquetas");
                    for(JsonNode etiqueta : etiquetasNode){
                        Map<String, String> etiquetaMap = new HashMap<>();
                        etiquetaMap.put("nombre", etiqueta.get("nombre").asText());
                        etiquetas.add(etiquetaMap);
                    }
                    map.put("etiquetas", etiquetas);
                    break;
                case "CriterioFecha":
                    JsonNode fechaInicioNode = criterio.get("fechaInicio");
                    if (fechaInicioNode != null) map.put("fechaInicio", fechaInicioNode.asLong());
                    JsonNode fechaFinNode = criterio.get("fechaFin");
                    if (fechaFinNode != null) map.put("fechaFin", fechaFinNode.asLong());
                    map.put("tipoFecha", criterio.get("tipoFecha").asText());
                    break;
                case "CriterioUbicacion":
                    JsonNode ubicacionNode = criterio.get("ubicacion");
                    if (ubicacionNode != null) {
                        Map<String, Object> ubicacionMap = new HashMap<>();
                        if (ubicacionNode.get("latitud") != null) ubicacionMap.put("latitud", ubicacionNode.get("latitud").asDouble());
                        if (ubicacionNode.get("longitud") != null) ubicacionMap.put("longitud", ubicacionNode.get("longitud").asDouble());
                        map.put("ubicacion", ubicacionMap);
                    }
                    break;
                case "CriterioContribuyente":
                    map.put("nombreContribuyente",criterio.get("nombreContribuyente").asText());
                    break;
            }
            criterios.add(map);
        }
    }
}