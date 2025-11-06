package gestorPublico.controller;

import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.HechoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetHechoEspecificoHandler implements Handler {

    private final HechoRepositorio hechoRepositorio;

    public GetHechoEspecificoHandler(HechoRepositorio repo) {
        this.hechoRepositorio = repo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // 1. Obtener el ID del hecho de la URL (ruta: /hechos/{id})
        String hechoIdString = ctx.pathParam("id");

        // 2. Buscar la entidad Hecho en la base de datos
        Hecho hecho = hechoRepositorio.buscarPorId(hechoIdString);

        if (hecho == null) {
            // Si no se encuentra el hecho, devolver un error 404
            ctx.status(404).result("Error 404: Hecho con ID " + hechoIdString + " no encontrado.");
            return;
        }

        //  Crear el modelo de datos para FreeMarker
        //Map<String, Object> modelo = TemplateUtil.model("hecho", hecho);

        //  Renderizar la plantilla (SSR)
        //ctx.render("hecho-especifico.ftl", modelo);
        ctx.json(hecho);
    }
}