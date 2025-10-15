package Estadisticas.Presentacion;

import Estadisticas.GeneradorEstadisticas;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetSolicitudesSpamHandler implements Handler {
    GeneradorEstadisticas generador;

    public GetSolicitudesSpamHandler(GeneradorEstadisticas generador) {
        this.generador = generador;
    }

    public void handle(@NotNull Context ctx) throws Exception {
        ctx.json(generador.)
    }
}
