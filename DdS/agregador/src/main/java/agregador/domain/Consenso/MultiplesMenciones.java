package agregador.domain.Consenso;

import agregador.domain.Consenso.AlgoritmoConsenso;
import agregador.domain.HechosYColecciones.Coleccion;
import agregador.domain.HechosYColecciones.Hecho;

import java.util.*;

public class MultiplesMenciones extends AlgoritmoConsenso {
    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion) {
        System.out.println("Se aplicara multiples menciones");
        List<Hecho> hechos = coleccion.getHechos();

        Map<String, List<Hecho>> hechosPorTitulo = new HashMap<>();
        for (Hecho hecho : hechos) {
            System.out.println("Titulo del hecho: " + hecho.getTitulo());
            hechosPorTitulo.computeIfAbsent(hecho.getTitulo(), k -> new ArrayList<>()).add(hecho);
        }

        System.out.println("Coleccion: " + coleccion.getTitulo());
        System.out.println("Hechos por titulo: " + hechosPorTitulo);

        List<Hecho> hechosConsensuados = new ArrayList<>();

        for (List<Hecho> grupoHechos : hechosPorTitulo.values()) {
            List<VarianteHecho> variantes = new ArrayList<>();

            for (Hecho hechoActual : grupoHechos) {
                boolean encontrado = false;

                for (VarianteHecho variante : variantes) {
                    if (variante.hecho.tieneMismosAtributosQue(hechoActual)) {
                        System.out.println("Se encontraron hechos con distintos atributos: " + hechoActual.getTitulo());
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