package Persistencia;

import Agregador.Criterios.Criterio;
import Agregador.HechosYColecciones.Hecho;
import utils.DTO.HechoDTO;

import java.util.ArrayList;
import java.util.List;

public class HechoRepositorio {
    private final List<Hecho> hechos;

    public HechoRepositorio() {
        this.hechos = new ArrayList<>();
    }

    public List<Hecho> buscarHechos(List<Criterio> criterios) {
        if(criterios == null || criterios.isEmpty()){
            return hechos;
        }
        List<Hecho> hechosADevolver = new ArrayList<Hecho>();
        for (Hecho hecho : hechos) {
            for(Criterio criterio : criterios) {
                if(criterio.cumpleConCriterio(hecho) && hecho.estaActivo()) {
                    hechosADevolver.add(hecho);
                }
            }
        }
        return hechosADevolver;
    }

    public void guardar(Hecho hecho) {
        hechos.add(hecho);
    }

    public void guardar(HechoDTO hecho) {
        hechos.add(new Hecho(hecho));
    }

    public void remover(Hecho hecho) {
        hechos.remove(hecho);
    }

    public void actualizar(Hecho hecho) {
        hechos.set(hechos.indexOf(hecho), hecho);
    }
}
