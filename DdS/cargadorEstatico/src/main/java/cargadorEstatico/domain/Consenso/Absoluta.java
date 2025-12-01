package cargadorEstatico.domain.Consenso;

import cargadorEstatico.domain.Consenso.AlgoritmoConsenso;
import cargadorEstatico.domain.HechosYColecciones.Coleccion;
import cargadorEstatico.domain.HechosYColecciones.Hecho;
import cargadorEstatico.domain.fuente.Fuente;

import java.util.*;

public class Absoluta extends AlgoritmoConsenso {

    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion){
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

        for (Map.Entry<String, Set<UUID>> entry : fuentesIdsPorTitulo.entrySet()) {
            if (entry.getValue().size() == totalFuentes) {
                hechosConsensuados.add(hechoRepresentativo.get(entry.getKey()));
            }
        }

        return hechosConsensuados;
    }
}