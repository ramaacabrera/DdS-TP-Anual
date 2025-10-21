package Agregador.Handlers;

import Agregador.Cargador.ConexionCargador;
import Agregador.Persistencia.FuenteRepositorio;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import org.jetbrains.annotations.NotNull;

public class OnCloseHandler implements WsCloseHandler{
    ConexionCargador conexionCargador;
    FuenteRepositorio fuenteRepositorio;

    public OnCloseHandler(ConexionCargador conexionCargadorNuevo, FuenteRepositorio fuenteRepositorioNuevo) {
        conexionCargador = conexionCargadorNuevo;
        fuenteRepositorio = fuenteRepositorioNuevo;
    }


    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        String cargadorId = ctx.queryParam("idCargador");
        conexionCargador.borrarFuente(cargadorId);
    }
}
