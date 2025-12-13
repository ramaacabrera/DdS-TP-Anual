package estadisticas.domain.Consenso;

import estadisticas.domain.Consenso.AlgoritmoConsenso;
import estadisticas.domain.HechosYColecciones.Coleccion;
import estadisticas.domain.HechosYColecciones.Hecho;

import java.util.*;

public class MultiplesMenciones extends AlgoritmoConsenso {
    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion) {
        List<Hecho> hechos = coleccion.getHechos();

        Map<String, List<Hecho>> hechosPorTitulo = new HashMap<>();
        for (Hecho hecho : hechos) {
            hechosPorTitulo.computeIfAbsent(hecho.getTitulo(), k -> new ArrayList<>()).add(hecho);
        }

        List<Hecho> hechosConsensuados = new ArrayList<>();

        for (List<Hecho> grupoHechos : hechosPorTitulo.values()) {
            List<VarianteHecho> variantes = new ArrayList<>();

            for (Hecho hechoActual : grupoHechos) {
                boolean encontrado = false;

                for (VarianteHecho variante : variantes) {
                    if (variante.hecho.tieneMismosAtributosQue(hechoActual)) {
                        variante.fuentes.add(hechoActual.getFuente().getId());
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    VarianteHecho nuevaVariante = new VarianteHecho(hechoActual);
                    nuevaVariante.fuentes.add(hechoActual.getFuente().getId());
                    variantes.add(nuevaVariante);
                }
            }

            if (variantes.size() == 1) {
                VarianteHecho unicaVariante = variantes.get(0);
                if (unicaVariante.fuentes.size() >= 2) {
                    hechosConsensuados.add(unicaVariante.hecho);
                }
            }
        }

        return hechosConsensuados;
    }

    private static class VarianteHecho {
        Hecho hecho;
        Set<UUID> fuentes = new HashSet<>();

        public VarianteHecho(Hecho hecho) {
            this.hecho = hecho;
        }
    }
}