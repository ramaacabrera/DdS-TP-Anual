package cargadorDemo.service;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Conexiones.Cargador;
import utils.Conexiones.FuenteExternaConexion;
import utils.DTO.HechoDTO;
import cargadorDemo.domain.fuente.Fuente;
import cargadorDemo.domain.fuente.TipoDeFuente;

import java.util.ArrayList;
import java.util.List;

public class GetHechosService implements Handler {

    private final Cargador cargador;

    public GetHechosService(Cargador cargador){ this.cargador = cargador;}

    @Override
    public void handle(@NotNull Context context){
//        List<FuenteExternaConexion> conexiones = cargador.getConexiones();
        List<HechoDTO> hechos = this.obtenerHechos();
        context.json(hechos);
        context.status(200);
    }

    public List<HechoDTO> obtenerHechos(){
        List<FuenteExternaConexion> conexiones = cargador.getConexiones();
        List<HechoDTO> hechos = new ArrayList<>();
        for(FuenteExternaConexion conexion : conexiones) {
            hechos.addAll(conexion.obtenerHechos());
            hechos.forEach(hechoDTO -> {
                hechoDTO.setFuente(new Fuente(TipoDeFuente.DEMO, "fuenteDemo"));
            });
        }
        return hechos;
    }
}
