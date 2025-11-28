package controller.colecciones;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import domain.Criterios.TipoDeTexto;
import service.ColeccionService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostColeccionHandler implements Handler {
    private String urlAdmin;
    private ColeccionService coleccionService;

    public PostColeccionHandler(String urlAdmin,  ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) {

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
    }

    public void obtenerCriteriosDePertenencia(Map<String, String> map, Context ctx) {
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
