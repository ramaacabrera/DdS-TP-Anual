package estadisticas.domain.Consenso;

import estadisticas.domain.Consenso.AlgoritmoConsenso;
import estadisticas.domain.HechosYColecciones.Coleccion;
import estadisticas.domain.HechosYColecciones.Hecho;
import estadisticas.domain.fuente.Fuente;

import java.util.*;

public class MayoriaSimple extends AlgoritmoConsenso {
    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion) {
        List<Hecho> hechos = coleccion.getHechos();
        List<Fuente> fuentes = coleccion.getFuente();

        if (fuentes == null || fuentes.isEmpty()) return new ArrayList<>();
        int totalFuentes = fuentes.size();

        Map<String, Set<UUID>> fuentesIdsPorTitulo = new HashMap<>();
        Map<String, Hecho> hechoRepresentativo = new HashMap<>();

        for (Hecho hecho : hechos) {
            String titulo = hecho.getTitulo();
            UUID fuenteId = hecho.getFuente().getId();

            fuentesIdsPorTitulo.putIfAbsent(titulo, new HashSet<>());
            fuentesIdsPorTitulo.get(titulo).add(fuenteId);

            hechoRepresentativo.putIfAbsent(titulo, hecho);
        }

        List<Hecho> hechosConsensuados = new ArrayList<>();

        int mayoriaNecesaria = (totalFuentes / 2) + 1;

        for (String titulo : fuentesIdsPorTitulo.keySet()) {
            int cantidadFuentes = fuentesIdsPorTitulo.get(titulo).size();

            if (cantidadFuentes >= mayoriaNecesaria) {
                hechosConsensuados.add(hechoRepresentativo.get(titulo));
            }
        }

        return hechosConsensuados;
    }
}