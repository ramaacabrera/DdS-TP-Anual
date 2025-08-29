package Persistencia;

import Agregador.Criterios.Criterio;
import Agregador.HechosYColecciones.Hecho;
import utils.DTO.HechoDTO;

import java.util.ArrayList;
import java.util.List;

public class DinamicoRepositorio {
    private List<HechoDTO> hechos;

    public DinamicoRepositorio() {
        this.hechos = new ArrayList<>();
    }

    public List<HechoDTO> buscarHechos() {
        return hechos;
    }

    public void guardar(HechoDTO hecho) {
        hechos.add(hecho);
    }

    public void resetear() { hechos = new ArrayList<>();
    }
}
