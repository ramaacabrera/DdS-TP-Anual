package web.controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;
import utils.Dominio.HechosYColecciones.Hecho;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class GetHechoEspecificoHandler implements Handler {

    private ObjectMapper mapper = new ObjectMapper();
    private final String urlPublica;

    public GetHechoEspecificoHandler(String url) {
        this.urlPublica = url;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // 1. Obtener el ID del hecho de la URL (ruta: /hechos/{id})
        String hechoIdString = ctx.pathParam("id");

        if (hechoIdString == null || hechoIdString.trim().isEmpty()) {
            ctx.status(400).result("Error 400: ID de hecho no proporcionado.");
            return;
        }

        // 2. Buscar la entidad Hecho en la base de datos
        HttpClient httpClient = HttpClient.newHttpClient();

        URI uri = null;
        try{uri = new URI(urlPublica+"/hechos/" + hechoIdString);}
        catch (URISyntaxException e) {
            System.err.println("URI invalido "+e.getMessage());
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Hecho hecho = mapper.readValue(response.body(), new TypeReference<>() {
        });

        //  Crear el modelo de datos para FreeMarker
        Map<String, Object> modelo = TemplateUtil.model("hecho", hecho);

        if(!ctx.sessionAttributeMap().isEmpty()){
            String username = ctx.sessionAttribute("username");
            System.out.println("Usuario: " + username);
            String access_token = ctx.sessionAttribute("access_token");
            modelo.put("username", username);
            modelo.put("access_token", access_token);
        }


        //  Renderizar la plantilla (SSR)
        ctx.render("hecho-especifico.ftl", modelo);
    }
}