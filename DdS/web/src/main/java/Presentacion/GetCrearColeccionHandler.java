package Presentacion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetCrearColeccionHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Map<String, Object> modelo = new HashMap<>();

        modelo.put("algoritmos", TipoAlgoritmoConsenso.values());
        modelo.put("fuentes", TipoDeFuente.values());
        modelo.put("pageTitle", "Crear nueva colección");

        ctx.render("crear-coleccion.ftlh", modelo);
    }
}
