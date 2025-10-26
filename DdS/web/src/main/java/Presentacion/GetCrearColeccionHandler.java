package Presentacion;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetCrearColeccionHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ctx.render("crear-coleccion.ftlh");
    }
}
