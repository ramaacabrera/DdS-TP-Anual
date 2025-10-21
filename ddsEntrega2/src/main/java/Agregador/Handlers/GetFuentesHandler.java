<<<<<<<< HEAD:DdS/agregador/src/main/java/agregador/Handlers/GetFuentesHandler.java
package agregador.Handlers;
========
package Agregador.Handlers;
>>>>>>>> c99213c (Connect y close en agregador implementados):ddsEntrega2/src/main/java/Agregador/Handlers/GetFuentesHandler.java

import agregador.Cargador.ConexionCargador;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.websocket.WsContext;
import org.jetbrains.annotations.NotNull;

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
