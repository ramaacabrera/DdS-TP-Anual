package Agregador.Handlers;

import Agregador.Cargador.ConexionCargador;
import Agregador.fuente.Fuente;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.websocket.WsContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class GetFuentesHandler implements Handler {
    private final ConexionCargador conexionCargador;

    public GetFuentesHandler(ConexionCargador conexionCargadorNuevo) { conexionCargador = conexionCargadorNuevo; }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ConcurrentMap<UUID, WsContext> fuentes = conexionCargador.getFuentes();
        ctx.status(200).json(fuentes);
    }
}
