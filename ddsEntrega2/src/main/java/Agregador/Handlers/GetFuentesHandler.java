package Agregador.Handlers;

import Agregador.Cargador.ConexionCargador;
import Agregador.fuente.Fuente;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetFuentesHandler implements Handler {
    private final ConexionCargador conexionCargador;

    public GetFuentesHandler(ConexionCargador conexionCargadorNuevo) { conexionCargador = conexionCargadorNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        List<Fuente> fuentes = conexionCargador.getFuentes();
        ctx.status(200).json(fuentes);
    }
}
