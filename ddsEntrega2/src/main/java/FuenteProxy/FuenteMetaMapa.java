package FuenteProxy;

import Agregador.Criterios.Criterio;
import utils.DTO.HechoDTO;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FuenteMetaMapa extends FuenteProxy {

    public FuenteMetaMapa(URL url) {
        super(new ConexionMetaMapa(url));
    }

    public List<HechoDTO> obtenerHechos(List<Criterio> criterios) {
        if (this.conexion != null) {
            return this.conexion.obtenerHechos(criterios);
        }
        return new ArrayList<>();
    }

}
