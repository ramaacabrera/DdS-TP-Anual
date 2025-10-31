package Presentacion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.DTO.PageDTO;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.Solicitudes.SolicitudDeEliminacion;
import utils.Dominio.Solicitudes.SolicitudDeModificacion;
import utils.Persistencia.SolicitudEliminacionRepositorio;
import utils.Persistencia.SolicitudModificacionRepositorio;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GetColeccionesHandler implements Handler {

    private final String urlPublica;
    private final ObjectMapper mapper = new ObjectMapper();

    public GetColeccionesHandler(String urlPublica) {
        this.urlPublica = urlPublica;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            System.out.println("Listando todas las colecciones desde " + urlPublica);

            int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
            int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

            HttpUrl.Builder b = HttpUrl.parse(urlPublica + "/colecciones").newBuilder()
                    .addQueryParameter("pagina", String.valueOf(page))
                    .addQueryParameter("limite", String.valueOf(size));


            String finalUrl = b.build().toString();
            Request request = new Request.Builder().url(finalUrl).get().build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)  // Timeout para establecer conexión
                    .readTimeout(120, TimeUnit.SECONDS)     // Timeout para lectura de datos (el que te está fallando)
                    .writeTimeout(60, TimeUnit.SECONDS)    // Timeout para escritura
                    .build();
            PageDTO<Coleccion> resp;
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Error al llamar al backend: " + response.code() + " " + response.message());
                }
                String body = Objects.requireNonNull(response.body()).string();
                System.out.println(body);
                resp = mapper.readValue(body, new TypeReference<PageDTO<Coleccion>>() {
                });
            }

            int fromIndex = (resp.page - 1) * resp.size;                // 0-based
            int toIndex = fromIndex + (resp.content != null ? resp.content.size() : 0);

            // Armar modelo para FreeMarker
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Colecciones");
            modelo.put("total", resp.totalElements);
            modelo.put("page", resp.page);
            modelo.put("size", resp.size);
            modelo.put("totalPages", resp.totalPages);
            modelo.put("fromIndex", fromIndex);
            modelo.put("toIndex", toIndex);
            modelo.put("colecciones", resp.content);
            modelo.put("urlAdmin", urlPublica);

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
            e.printStackTrace();
            ctx.status(500).result("Error al cargar las colecciones: " + e.getMessage());
        }
    }
}