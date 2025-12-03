package cargadorDemo.domain.Conexiones;

import cargadorDemo.dto.HechoDTO;

import java.util.List;

public interface FuenteExternaConexion {

    public abstract List<HechoDTO> obtenerHechos();
}
