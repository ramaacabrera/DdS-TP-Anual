package web.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import web.domain.criterios.TipoDeTexto;
import web.domain.fuente.TipoDeFuente;
import web.domain.hechosycolecciones.Coleccion;
import web.domain.hechosycolecciones.TipoAlgoritmoConsenso;
import web.dto.PageDTO;
import web.service.ColeccionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColeccionController {

    private ColeccionService coleccionService;

    public ColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
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
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Colecciones");
            modelo.put("total", coleccionesPage.totalElements);
            modelo.put("page", coleccionesPage.page);
            modelo.put("size", coleccionesPage.size);
            modelo.put("totalPages", coleccionesPage.totalPages);
            modelo.put("fromIndex", fromIndex);
            modelo.put("toIndex", toIndex);
            modelo.put("colecciones", coleccionesPage.content);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

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

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Detalle de Colección");
            modelo.put("coleccion", coleccion);
            modelo.put("coleccionId", coleccionId);

            if(!ctx.sessionAttributeMap().isEmpty()){
                String username = ctx.sessionAttribute("username");
                System.out.println("Usuario: " + username);
                String access_token = ctx.sessionAttribute("access_token");
                modelo.put("username", username);
                modelo.put("access_token", access_token);
            }

            // 4 Renderizamos la vista
            ctx.render("coleccion.ftl", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar la colección: " + e.getMessage());
        }
    };

    public Handler obtenerPageCrearColeccion  = ctx -> {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Crear nueva colección");
        modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
        modelo.put("fuentes", TipoDeFuente.values());

        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            modelo.put("username", username);
            modelo.put("access_token", access_token);
        }

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

            String username = ctx.sessionAttribute("username");
            String access_token = ctx.sessionAttribute("access_token");

            // 3️ Armamos el modelo para la vista
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Editar colección");
            modelo.put("coleccion", coleccion);
            modelo.put("coleccionId", coleccionId);
            modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
            modelo.put("fuentes", TipoDeFuente.values());

            modelo.put("username", username);
            modelo.put("access_token", access_token);

            // Renderizamos el template
            ctx.render("editar-coleccion.ftl", modelo);

        } catch (Exception e) {
            System.err.println(" ERROR en GetEditarColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    };

    public Handler crearColeccion = ctx -> {
        Map<String, Object> bodyData = new HashMap<>();
        List<Map<String, String>> criteriosDePertenencia = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        this.obtenerCriteriosDePertenencia(map, ctx);
        map.put("@type", ctx.formParam("criteriosDePertenencia"));
        map.put("categoria", "ejemplo");
        criteriosDePertenencia.add(map);
        bodyData.put("titulo", ctx.formParam("titulo"));
        bodyData.put("descripcion", ctx.formParam("descripcion"));
        bodyData.put("algoritmoDeConsenso", ctx.formParam("algoritmo"));
        bodyData.put("criteriosDePertenencia", criteriosDePertenencia);
        //bodyData.put("criteriosDePertenencia", ctx.formParam("criteriosDePertenencia"));
        bodyData.put("fuentes", ctx.formParams("fuentes"));

        if (!ctx.sessionAttributeMap().isEmpty()) {
            bodyData.put("username", ctx.sessionAttribute("username"));
            bodyData.put("access_token", ctx.sessionAttribute("access_token"));
        }

        try {
            coleccionService.crearColeccion(bodyData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private void obtenerCriteriosDePertenencia(Map<String, String> map, Context ctx) {
        map.put("@type", ctx.formParam("criteriosDePertenencia"));
        String resultado = "";
        switch (ctx.formParam("criteriosDePertenencia").toString()) {
            case "CriterioDeTexto":
                switch(ctx.formParam("criterio.tipoDeTexto").toString()){
                    case "titulo":
                        map.put("tipoDeTexto", TipoDeTexto.TITULO.toString());
                        break;
                    case "descripcion":
                        map.put("tipoDeTexto", TipoDeTexto.DESCRIPCION.toString());
                        break;
                    case "categoria":
                        map.put("tipoDeTexto", TipoDeTexto.CATEGORIA.toString());
                        break;
                }
                List<String> palabras = ctx.formParams("criterio.palabras[]");
                resultado = String.join(",", palabras);
                map.put("palabras", resultado);
                break;
            case "CriterioFecha":
                map.put("tipoFecha", ctx.formParams("criterio.tipoDeFecha").toString());
                map.put("fechaInicio", ctx.formParams("criterio.fechaInicio").toString());
                map.put("fechaFin", ctx.formParams("criterio.fechaFin").toString());
                break;
            case "CriterioEtiquetas":
                List<String> etiquetas = ctx.formParams("criterio.etiquetas[]");
                resultado = String.join(",", etiquetas);
                map.put("etiquetas", resultado);
                break;
            case "CriterioContribuyente":
                map.put("nombreContribuyente", ctx.formParams("criterio.contribuyente").toString());
                break;
            case "CriterioUbicacion":
                String jsonBody = String.format("""
                    {
                        "latitud": %s,
                        "longitud": %s
                    }
                    """, ctx.formParams("criterio.ubicacion.latitud").toString(), ctx.formParams("criterio.ubicacion.longitud").toString());
                //map.put("latitud", ctx.formParams("criterio.ubicacion.latitud").toString());
                //map.put("longitud", ctx.formParams("criterio.ubicacion.longitud").toString());
                map.put("ubicacion", jsonBody);
                break;
            case "CriterioTipoMultimedia":
                map.put("tipoContenidoMultimedia", ctx.formParams("criterio.tipoMultimedia").toString());
                break;
            default:
                break;
        }
    }
}
