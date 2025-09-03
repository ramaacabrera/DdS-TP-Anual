package CargadorProxy;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import Agregador.fuente.Fuente;

public class PostFuentesProxyHandler implements Handler {
    private final Cargador cargador;

    public PostFuentesProxyHandler(Cargador cargador){this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
        ConexionProxy conexion = context.bodyAsClass(ConexionProxy.class);
        cargador.agregarConexion(conexion);
        context.status(200);
    }
}
