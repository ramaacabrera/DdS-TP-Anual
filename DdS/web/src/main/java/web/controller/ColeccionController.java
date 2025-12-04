package web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.Request;
import okhttp3.Response;
import utils.Dominio.HechosYColecciones.Ubicacion;
import utils.Dominio.Usuario.Usuario;
import web.domain.Criterios.Criterio;
import web.domain.Criterios.TipoDeTexto;
import web.domain.Fuente.TipoDeFuente;
import web.domain.HechosYColecciones.Coleccion;
import web.domain.HechosYColecciones.TipoAlgoritmoConsenso;
import web.domain.Usuario.RolUsuario;
import web.dto.PageDTO;
import web.service.ColeccionService;
import web.service.UsuarioService;
import web.utils.ViewUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColeccionController {

    private ColeccionService coleccionService;
    private UsuarioService usuarioService;

    public ColeccionController(ColeccionService coleccionService, UsuarioService usuarioService) {
        this.coleccionService = coleccionService;
        this.usuarioService = usuarioService;
    }

    public Handler listarColecciones = ctx -> {
        try {
            System.out.println("Listando todas las colecciones");

            int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
            int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

            PageDTO<Coleccion> coleccionesPage = coleccionService.listarColecciones(page,size);

            int fromIndex = (coleccionesPage.page - 1) * coleccionesPage.size;                // 0-based
            int toIndex = fromIndex + (coleccionesPage.content != null ? coleccionesPage.content.size() : 0);

            // Armar modelo para FreeMarker
            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Colecciones");
            modelo.put("total", coleccionesPage.totalElements);
            modelo.put("page", coleccionesPage.page);
            modelo.put("size", coleccionesPage.size);
            modelo.put("totalPages", coleccionesPage.totalPages);
            modelo.put("fromIndex", fromIndex);
            modelo.put("toIndex", toIndex);
            modelo.put("colecciones", coleccionesPage.content);

            // Renderizar plantilla
            ctx.render("colecciones.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetColeccionesHandler: " + e.getMessage());
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

            // 4 Renderizamos la vista
            ctx.render("coleccion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar la colección: " + e.getMessage());
        }
    };

    public Handler obtenerPageCrearColeccion  = ctx -> {
        Map<String, Object> modelo = ViewUtil.baseModel(ctx);
        modelo.put("pageTitle", "Crear nueva colección");
        modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
        modelo.put("fuentes", TipoDeFuente.values());

        ctx.render("crear-coleccion.ftl", modelo);
    };

    public Handler obtenerPageEditarColeccion  = ctx -> {
        try {
            String coleccionId = ctx.pathParam("id");

            Coleccion coleccion;
            try{
                coleccion = coleccionService.obtenerColeccionPorId(coleccionId);
            }
            catch (Exception e){
                throw new RuntimeException("Error al obtener coleccion por id");
            }

            // Valido que el usuario sea un admin
            if(ctx.sessionAttributeMap().isEmpty()){
                ctx.status(500).result("Administrador no identificado");
            }

            // 3️ Armamos el modelo para la vista
            Map<String, Object> modelo = ViewUtil.baseModel(ctx);
            modelo.put("pageTitle", "Editar colección");
            modelo.put("coleccion", coleccion);
            modelo.put("COLECCION_ID", coleccionId);
            modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
            modelo.put("fuentes", TipoDeFuente.values());

            // Renderizamos el template
            ctx.render("editar-coleccion.ftlh", modelo);

        } catch (Exception e) {
            System.err.println(" ERROR en GetEditarColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    };

    public Handler crearColeccion = ctx -> {
        System.out.println("ColeccionHandler");
        Map<String, Object> bodyData = new HashMap<>();

        System.out.println("Formulario: " + ctx.formParamMap());
        List<Map<String, Object>> criteriosDePertenencia = new ArrayList<>();
        this.extraerCriterios(ctx.formParamMap(), criteriosDePertenencia);
        bodyData.put("titulo", ctx.formParam("titulo"));
        bodyData.put("descripcion", ctx.formParam("descripcion"));
        bodyData.put("algoritmoDeConsenso", ctx.formParam("algoritmo"));
        bodyData.put("fuentes", ctx.formParams("fuentes"));
        bodyData.put("criteriosDePertenencia", criteriosDePertenencia);
        bodyData.put("username", ctx.sessionAttributeMap().get("username"));
        bodyData.put("access_token", ctx.sessionAttributeMap().get("access_token"));
        bodyData.put("rolUsuario", ctx.sessionAttribute("rolUsuario"));

        System.out.println(bodyData);

        try {
            coleccionService.crearColeccion(bodyData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    public void extraerCriterios(Map<String, List<String>> mapBody, List<Map<String, Object>> criteriosDePertenencia) {
        for(Map.Entry<String, List<String>> entry : mapBody.entrySet()){
            if(entry.getKey().startsWith("criterios[")){
                // Extraer el ID del criterio y el campo
                String key = entry.getKey();
                String criterioId = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                String campo = key.substring(key.indexOf('.') + 1);

                // Buscar o crear el criterio en la lista
                Map<String, Object> criterio = criteriosDePertenencia.stream()
                        .filter(c -> c.get("_id").equals(criterioId))
                        .findFirst()
                        .orElseGet(() -> {
                            Map<String, Object> nuevoCriterio = new HashMap<>();
                            nuevoCriterio.put("_id", criterioId);
                            criteriosDePertenencia.add(nuevoCriterio);
                            return nuevoCriterio;
                        });

                // Agregar el campo al criterio
                if(campo.equals("palabras") || campo.equals("etiquetas")) {
                    criterio.put(campo, entry.getValue());
                } else{
                    criterio.put(campo, entry.getValue().get(0));
                }
            }
        };

        // Remover el campo _id temporal y limpiar la estructura
        for (Map<String, Object> criterio : criteriosDePertenencia) {
            criterio.remove("_id");
            if(criterio.get("@type").equals("CriterioUbicacion")){
                double latitud = Double.parseDouble(criterio.get("ubicacion.latitud").toString());
                double longitud = Double.parseDouble(criterio.get("ubicacion.longitud").toString());
                Ubicacion ubicacion = new Ubicacion(latitud, longitud);
                criterio.put("ubicacion", ubicacion);
                criterio.remove("ubicacion.latitud");
                criterio.remove("ubicacion.longitud");
            }
        }

        System.out.println("Criterios extraidos: " + criteriosDePertenencia);
    }
    public Handler editarColeccion = ctx -> {
        String id = ctx.pathParam("id");
        System.out.println("PUT COLECCION: " + id);
        System.out.println(ctx.body());

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
        bodyData.put("rolUsuario", ctx.sessionAttributeMap().get("rolUsuario"));

        System.out.println(bodyData);

        coleccionService.actualizarColeccion(id, bodyData);

    };

    private void extraer(JsonNode body, List<Map<String, Object>> criterios){
        for(JsonNode criterio : body){
            Map<String, Object> map = new HashMap<>();
            System.out.println(criterio);
            map.put("@type", criterio.get("@type").asText());
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
                    if (fechaInicioNode != null) {
                        map.put("fechaInicio", fechaInicioNode.asLong());
                    }

                    JsonNode fechaFinNode = criterio.get("fechaFin");
                    if (fechaFinNode != null) {
                        map.put("fechaFin", fechaFinNode.asLong());
                    }

                    map.put("tipoFecha", criterio.get("tipoFecha").asText());
                    break;
                case "CriterioUbicacion":
                    JsonNode ubicacionNode = criterio.get("ubicacion");
                    if (ubicacionNode != null) {
                        Map<String, Object> ubicacionMap = new HashMap<>();

                        JsonNode latitudNode = ubicacionNode.get("latitud");
                        if (latitudNode != null) {
                            ubicacionMap.put("latitud", latitudNode.asDouble());
                        }

                        JsonNode longitudNode = ubicacionNode.get("longitud");
                        if (longitudNode != null) {
                            ubicacionMap.put("longitud", longitudNode.asDouble());
                        }

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

    public Handler eliminarColeccion = ctx -> {
        String id = ctx.pathParam("id");
        System.out.println("Coleccion a eliminar: " + id);
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("id", id);
        if(!ctx.sessionAttributeMap().isEmpty()){
            bodyData.put("username", ctx.sessionAttribute("username"));
            bodyData.put("access_token", ctx.sessionAttribute("access_token"));
            bodyData.put("rolUsuario", ctx.sessionAttributeMap().get("rolUsuario"));
        }
        coleccionService.eliminarColeccion(bodyData);
    };
}
