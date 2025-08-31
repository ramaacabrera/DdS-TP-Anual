package Agregador.Persistencia;

import Agregador.Criterios.Criterio;
import utils.DTO.HechoDTO;

import java.util.ArrayList;
import java.util.List;

public class HechoDTORepositorio {
    private final List<HechoDTO> hechos;

    public HechoDTORepositorio() {
        this.hechos = new ArrayList<>();
    }

    public List<HechoDTO> buscarHechos(List<Criterio> criterios) {
        if(criterios == null || criterios.isEmpty()){
            return hechos;
        }
        List<HechoDTO> hechosADevolver = new ArrayList<HechoDTO>();
        for (HechoDTO hecho : hechos) {
            for(Criterio criterio : criterios) {
                if(criterio.cumpleConCriterio(hecho) && hecho.estaActivo()) {
                    hechosADevolver.add(hecho);
                }
            }
        }
        return hechosADevolver;
    }

    public void guardar(HechoDTO hecho) {
        hechos.add(hecho);
    }

    public void remover(HechoDTO hecho) {
        hechos.remove(hecho);
    }

    public void actualizar(HechoDTO hecho) {
        hechos.set(hechos.indexOf(hecho), hecho);
    }
}
