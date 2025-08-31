package CargadorProxy;

import utils.DTO.HechoDTO;
import java.util.List;

public abstract class ConexionProxy {
    protected String url;

    public abstract List<HechoDTO> obtenerHechos();
}
