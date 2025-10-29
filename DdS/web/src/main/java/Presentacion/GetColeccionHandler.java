package Presentacion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class GetColeccionHandler implements Handler {

    private final String urlAdmin;

    public GetColeccionHandler(String urlAdmin) {
        this.urlAdmin = urlAdmin;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String coleccionId = ctx.pathParam("id");

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("pageTitle", "Detalle de Colección");
        modelo.put("coleccionId", coleccionId);
        modelo.put("urlAdmin", urlAdmin);

        ctx.render("coleccion.ftlh", modelo);
    }
}
