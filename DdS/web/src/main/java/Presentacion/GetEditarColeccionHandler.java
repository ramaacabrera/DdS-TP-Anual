package Presentacion;

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetEditarColeccionHandler implements Handler {

    private final ColeccionRepositorio coleccionRepositorio;

    public GetEditarColeccionHandler(ColeccionRepositorio repo) {
        this.coleccionRepositorio = repo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String id = ctx.pathParam("id");

        Optional<Coleccion> coleccionOpt = coleccionRepositorio.buscarPorHandle(id);
        if (coleccionOpt.isEmpty()) {
            ctx.status(404).result("Colección no encontrada");
            return;
        }

        Coleccion coleccion = coleccionOpt.get();

        List<String> algoritmosDisponibles = List.of("MAYORIASIMPLE", "ABSOLUTA", "MULTIPLESMENCIONES");

        Map<String, Object> modelo = TemplateUtil.model(
                "coleccion", coleccion,
                "algoritmosDisponibles", algoritmosDisponibles
        );

        ctx.render("editar-coleccion.ftlh", modelo);
    }
}
