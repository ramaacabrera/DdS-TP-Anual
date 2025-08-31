package CargadorProxy;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.*;

import java.util.ArrayList;
import java.util.List;

public class GetHechosProxyHandler implements Handler {

    private Cargador cargador;

    public GetHechosProxyHandler(Cargador cargador){ this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
        List<ConexionProxy> conexiones = cargador.getConexiones();
        List<HechoDTO> hechos = new ArrayList<>();
        for(ConexionProxy conexion : conexiones) {
            hechos.addAll(conexion.obtenerHechos());
        }
        context.json(hechos);
        context.status(200);
    }

}
