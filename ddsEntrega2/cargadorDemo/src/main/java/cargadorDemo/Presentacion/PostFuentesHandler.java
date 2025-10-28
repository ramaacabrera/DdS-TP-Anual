package cargadorDemo.Presentacion;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Conexiones.Cargador;
import utils.Conexiones.FuenteExternaConexion;

public class PostFuentesHandler implements Handler {
    private final Cargador cargador;

    public PostFuentesHandler(Cargador cargador){this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
        FuenteExternaConexion conexion = context.bodyAsClass(FuenteExternaConexion.class);
        cargador.agregarConexion(conexion);
        context.status(200);
    }
}
