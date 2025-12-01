package web.controller;


import web.domain.HechosYColecciones.Coleccion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.ColeccionService;
import web.dto.PageDTO;

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