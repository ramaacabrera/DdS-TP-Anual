package web.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.CategoriasService;
import java.util.Map;

public class GetBusquedaAPIHandler implements Handler {
    private final CategoriasService categoriasService;
    private final TipoBusqueda tipo;

    public enum TipoBusqueda {
        PROVINCIA, HORA
    }

    public GetBusquedaAPIHandler(CategoriasService categoriasService, TipoBusqueda tipo) {
        this.categoriasService = categoriasService;
        this.tipo = tipo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String categoria = ctx.pathParam("categoria");

        Map<String, Object> respuesta;

        if (tipo == TipoBusqueda.PROVINCIA) {
            respuesta = categoriasService.buscarProvinciaPorCategoria(categoria);
        } else {
            respuesta = categoriasService.buscarHoraPorCategoria(categoria);
        }

        ctx.json(respuesta);
    }
}