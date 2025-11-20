package controller;


import domain.HechosYColecciones.Coleccion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.ColeccionService;
import domain.DTO.ColeccionDTO;
import domain.DTO.PageDTO;

import java.util.*;


public class GetColeccionesHandler implements Handler {
    private final ColeccionService coleccionService;
    private final String urlPublica;
    public GetColeccionesHandler(String urlPublica, ColeccionService coleccionService) {
        this.urlPublica = urlPublica;
        this.coleccionService = coleccionService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            System.out.println("Listando todas las colecciones");

            int page = Math.max(1, ctx.queryParamAsClass("page", Integer.class).getOrDefault(1));
            int size = Math.max(1, ctx.queryParamAsClass("size", Integer.class).getOrDefault(10));

            PageDTO<Coleccion> resp = coleccionService.listarColecciones(page,size);

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
            ctx.status(500).result("Error al cargar las colecciones: " + e.getMessage());
        }
    }
}