package controller;

import service.ConexionCargadorService;
import repository.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import org.jetbrains.annotations.NotNull;

public class OnCloseHandler implements WsCloseHandler {
    ConexionCargadorService conexionCargadorService;

    public OnCloseHandler(ConexionCargadorService conexionCargadorServiceNuevo, FuenteRepositorio fuenteRepositorioNuevo) {
        conexionCargadorService = conexionCargadorServiceNuevo;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        String sessionId = ctx.getSessionId();
        conexionCargadorService.borrarFuentePorSession(sessionId);
    }
}