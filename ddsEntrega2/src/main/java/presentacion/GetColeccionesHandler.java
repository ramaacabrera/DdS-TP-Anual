package presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.agregador.*;
import org.jetbrains.annotations.NotNull;


public class GetColeccionesHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public GetColeccionesHandler(ColeccionRepositorio colecciones) { repositorio = colecciones; }

    public void handle(@NotNull Context ctx) throws Exception {
        ctx.json(repositorio);
    }

}
