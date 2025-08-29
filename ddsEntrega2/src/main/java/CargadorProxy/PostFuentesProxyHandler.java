package CargadorProxy;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import Agregador.fuente.Fuente;

public class PostFuentesProxyHandler implements Handler {
    private Cargador cargador;

    public PostFuentesProxyHandler(Cargador cargador){this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
        Fuente fuente = context.bodyAsClass(Fuente.class);
        cargador.agregarFuente(fuente);
        context.status(200);
    }
}
