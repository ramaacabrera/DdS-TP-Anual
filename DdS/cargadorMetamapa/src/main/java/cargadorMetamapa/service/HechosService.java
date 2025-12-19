package cargadorMetamapa.service;

import utils.Conexiones.Cargador;
import utils.Conexiones.FuenteExternaConexion;
import utils.DTO.*;

import java.util.ArrayList;
import java.util.List;

public class HechosService{

    private final Cargador cargador;

    public HechosService(Cargador cargador){ this.cargador = cargador;}


    public List<HechoDTO> obtenerHechos(){
        List<FuenteExternaConexion> conexiones = cargador.getConexiones();
        List<HechoDTO> hechos = new ArrayList<>();
        for(FuenteExternaConexion conexion : conexiones) {
            hechos.addAll(conexion.obtenerHechos());
        }
        return hechos;
    }
}
