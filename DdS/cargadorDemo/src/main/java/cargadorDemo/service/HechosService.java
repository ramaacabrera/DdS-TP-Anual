package cargadorDemo.service;

import cargadorDemo.domain.Conexiones.Cargador;
import cargadorDemo.domain.Conexiones.FuenteExternaConexion;
import cargadorDemo.dto.HechoDTO;
import cargadorDemo.domain.fuente.Fuente;
import cargadorDemo.domain.fuente.TipoDeFuente;

import java.util.ArrayList;
import java.util.List;

public class HechosService {

    private final Cargador cargador;

    public HechosService(Cargador cargador){ this.cargador = cargador;}

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
