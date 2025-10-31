package Presentacion;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Dominio.Criterios.TipoDeTexto;

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

    public PostColeccionHandler(String urlAdmin){this.urlAdmin = urlAdmin;}

    @Override
    public void handle(@NotNull Context ctx) {

        Map<String, Object> bodyData = new HashMap<>();
        List<Map<String, String>> criteriosDePertenencia = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        this.obtenerCriteriosDePertenencia(map, ctx);
        criteriosDePertenencia.add(map);
        bodyData.put("titulo", ctx.formParam("titulo"));
        bodyData.put("descripcion", ctx.formParam("descripcion"));
        bodyData.put("algoritmoDeConsenso", ctx.formParam("algoritmo"));
        bodyData.put("criteriosDePertenencia", criteriosDePertenencia);
        //bodyData.put("criteriosDePertenencia", ctx.formParam("criteriosDePertenencia"));
        bodyData.put("fuentes", ctx.formParams("fuentes"));

        System.out.println(bodyData);

        String jsonBody = new Gson().toJson(bodyData);

        System.out.println("Serializacion: " + jsonBody);

        HttpClient httpClient = HttpClient.newHttpClient();
        try{
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(urlAdmin+"/colecciones"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            if (!ctx.sessionAttributeMap().isEmpty()) {
                requestBuilder
                        .header("username", ctx.sessionAttribute("username"))
                        .header("access_token", ctx.sessionAttribute("access_token"));
            }

            HttpRequest request = requestBuilder.build();

            System.out.println("Se armo la request");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Mandamos la request");

            if (response.statusCode() == 200) {
                System.out.println("Coleccion creada");
            } else {
                System.out.println("Error al cargar coleccion");
            }

        } catch (Exception e){
            System.out.println("Error al cargar coleccion");
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
