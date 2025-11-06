package agregador.controller;

import DominioAgregador.Cargador.ConexionCargador;
import utils.Persistencia.FuenteRepositorio;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import org.jetbrains.annotations.NotNull;

public class OnCloseHandler implements WsCloseHandler {
    ConexionCargador conexionCargador;
    FuenteRepositorio fuenteRepositorio;

    public OnCloseHandler(ConexionCargador conexionCargadorNuevo, FuenteRepositorio fuenteRepositorioNuevo) {
        conexionCargador = conexionCargadorNuevo;
        fuenteRepositorio = fuenteRepositorioNuevo;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        String sessionId = ctx.getSessionId();
        System.out.println("🔌 Conexión cerrada - Session: " + sessionId);

        boolean removido = conexionCargador.borrarFuentePorSession(sessionId);

        if (removido) {
            System.out.println("✅ Fuente removida exitosamente - Session: " + sessionId);
        } else {
            System.out.println("⚠️ No se encontró fuente para remover - Session: " + sessionId);
        }
    }
}