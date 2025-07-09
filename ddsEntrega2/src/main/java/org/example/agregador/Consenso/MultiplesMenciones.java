package org.example.agregador.Consenso;
import org.example.agregador.HechosYColecciones.Coleccion;
import org.example.agregador.HechosYColecciones.Hecho;
import org.example.agregador.fuente.Fuente;

import java.util.*;

public class MultiplesMenciones extends AlgoritmoConsenso {
    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion){

        List<Hecho> hechos = coleccion.getHechos();
        List<Fuente> fuentes = coleccion.getFuente();

        // Agrupamos los hechos por título
        Map<String, List<Hecho>> hechosPorTitulo = new HashMap<>();

        for (Hecho hecho : hechos) {
            String titulo = hecho.getTitulo();
            hechosPorTitulo.putIfAbsent(titulo, new ArrayList<>());
            hechosPorTitulo.get(titulo).add(hecho);
        }

        List<Hecho> hechosConsensuados = new ArrayList<>();

        // Procesamos cada grupo de hechos con el mismo título
        for (Map.Entry<String, List<Hecho>> entry : hechosPorTitulo.entrySet()) {
            List<Hecho> hechosConMismoTitulo = entry.getValue();

            // Agrupamos hechos equivalentes por sus atributos completos
            Map<Hecho, Set<Fuente>> variantes = new HashMap<>();

            for (Hecho hecho : hechosConMismoTitulo) {
                boolean agregado = false;
                for (Hecho clave : variantes.keySet()) {
                    if (hecho.tieneMismosAtributosQue(clave)) {
                        variantes.get(clave).add(hecho.getFuente());
                        agregado = true;
                        break;
                    }
                }
                if (!agregado) {
                    Set<Fuente> fuentesSet = new HashSet<>();
                    fuentesSet.add(hecho.getFuente());
                    variantes.put(hecho, fuentesSet);
                }
            }

            // Buscar si hay una variante que tenga al menos 2 fuentes y sea la única variante
            for (Map.Entry<Hecho, Set<Fuente>> variante : variantes.entrySet()) {
                if (variante.getValue().size() >= 2 && variantes.size() == 1) {
                    hechosConsensuados.add(variante.getKey());
                    break; // solo una por título
                }
            }
        }

        return hechosConsensuados;
    }

}
