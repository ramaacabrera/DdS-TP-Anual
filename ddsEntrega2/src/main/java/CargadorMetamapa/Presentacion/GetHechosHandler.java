package CargadorMetamapa.Presentacion;

import utils.Conexiones.Cargador;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Conexiones.FuenteExternaConexion;
import utils.DTO.*;

import java.util.ArrayList;
import java.util.List;

public class GetHechosHandler implements Handler {

    private final Cargador cargador;

    public GetHechosHandler(Cargador cargador){ this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
        List<FuenteExternaConexion> conexiones = cargador.getConexiones();
        List<HechoDTO> hechos = new ArrayList<>();
        for(FuenteExternaConexion conexion : conexiones) {
            hechos.addAll(conexion.obtenerHechos());
        }
        context.json(hechos);
        context.status(200);
    }

}
