package utils.Conexiones;

import utils.DTO.HechoDTO;
import java.util.List;

public interface FuenteExternaConexion {

    public abstract List<HechoDTO> obtenerHechos();
}
