package CargadorProxy;

import Agregador.Criterios.Criterio;
import utils.DTO.HechoDTO;
import java.util.ArrayList;
import java.util.List;

public class FuenteDemo extends FuenteProxy{

    public FuenteDemo(String urlServicioExterno) {
        super(new ConexionDemo(urlServicioExterno,null));

        if (this.conexion instanceof ConexionDemo) {
            ((ConexionDemo) this.conexion).setFuenteAsociada(this);
        } else {
            System.err.println("FuenteDemo: Error de configuración. La conexión no es de tipo ConexionDemo.");
        }
    }
    public List<HechoDTO> obtenerHechos(List<Criterio> criterios) {
        if (this.conexion != null) {
            return this.conexion.obtenerHechos(criterios);
        }
        return new ArrayList<>();
    }
}

