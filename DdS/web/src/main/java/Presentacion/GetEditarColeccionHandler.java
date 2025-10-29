package Presentacion;

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.TipoAlgoritmoConsenso;
import utils.Dominio.fuente.TipoDeFuente;
import utils.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.rendering.template.TemplateUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GetEditarColeccionHandler implements Handler {

    private final String urlAdmin;

    public GetEditarColeccionHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String coleccionId = ctx.pathParam("id");
            System.out.println("Abriendo formulario de edición para colección ID: " + coleccionId);

            // Armamos el modelo
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("pageTitle", "Editar colección");
            modelo.put("coleccionId", coleccionId);
            modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
            modelo.put("fuentes", TipoDeFuente.values());
            modelo.put("urlAdmin", urlAdmin);

            // Renderizamos la vista
            ctx.render("editar-coleccion.ftlh", modelo);

        } catch (Exception e) {
            System.err.println("ERROR en GetEditarColeccionHandler: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Error al cargar el formulario: " + e.getMessage());
        }
    }
}




/*Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Editar colección");
        modelo.put("coleccion", coleccion);
        modelo.put("algoritmosDisponibles", TipoAlgoritmoConsenso.values());
        modelo.put("fuentes", TipoDeFuente.values());
        modelo.put("urlAdmin", urlAdmin);
*/
