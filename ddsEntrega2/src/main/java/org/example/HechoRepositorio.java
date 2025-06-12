package org.example;

import java.util.ArrayList;
import java.util.List;

public class HechoRepositorio {
    private List<Hecho> hechos;

    public HechoRepositorio() {
        this.hechos = new ArrayList<>();
    }

    public List<Hecho> buscarHechos(List<Criterio> criterios) {
        List<Hecho> hechosADevolver = new ArrayList<Hecho>();
        for (Hecho hecho : hechos) {
            for(Criterio criterio : criterios) {
                if(criterio.cumpleConCriterio(hecho)) {
                    hechosADevolver.add(hecho);
                }
            }
        }
        return hechosADevolver;
    }

    public void guardar(Hecho hecho) {
        hechos.add(hecho);
    }

    public void remover(Hecho hecho) {
        hechos.remove(hecho);
    }

    public void actualizar(Hecho hecho) {
        hechos.set(hechos.indexOf(hecho), hecho);
    }
}
