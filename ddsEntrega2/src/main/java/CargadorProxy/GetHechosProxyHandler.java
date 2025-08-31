package CargadorProxy;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import Agregador.fuente.Fuente;
import Agregador.HechosYColecciones.Hecho;
import utils.DTO.*;

import java.util.ArrayList;
import java.util.List;

public class GetHechosProxyHandler implements Handler {

    private Cargador cargador;

    public GetHechosProxyHandler(Cargador cargador){ this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
        List<Fuente> fuentes = cargador.getFuentes();
        List<HechoDTO> hechos = new ArrayList<>();
        for(Fuente fuente : fuentes) {
            hechos.addAll(fuente.obtenerHechos());
        }
        context.json(hechos);
        context.status(200);
    }

}
