package cargadorDinamico.domain.Consenso;
import cargadorDinamico.domain.HechosYColecciones.Coleccion;
import cargadorDinamico.domain.HechosYColecciones.Hecho;
import cargadorDinamico.domain.fuente.Fuente;

import java.util.*;

public class MayoriaSimple extends AlgoritmoConsenso {
    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion) {
        List<Hecho> hechos = coleccion.getHechos();
        List<Fuente> fuentes = coleccion.getFuente(); // fuentes distintas de la colección
        int totalFuentes = fuentes.size();

        // 1. Diccionario: título → fuentes que lo mencionan
        Map<String, Set<Fuente>> fuentesPorTitulo = new HashMap<>();

        // 2. Diccionario: título → un ejemplo de Hecho con ese título
        Map<String, Hecho> hechoPorTitulo = new HashMap<>();

        for (Hecho hecho : hechos) {
            String titulo = hecho.getTitulo();
            Fuente fuente = hecho.getFuente();

            // si aún no existe el título en el mapa, lo agrego con un set vacío
            fuentesPorTitulo.putIfAbsent(titulo, new HashSet<>());
            fuentesPorTitulo.get(titulo).add(fuente);

            // guardo un ejemplo del hecho para devolverlo luego
            hechoPorTitulo.putIfAbsent(titulo, hecho);
        }

        // 3. Filtrar los hechos consensuados
        List<Hecho> hechosConsensuados = new ArrayList<>();

        for (String titulo : fuentesPorTitulo.keySet()) {
            int cantidadFuentes = fuentesPorTitulo.get(titulo).size();

            // mayoría simple: al menos la mitad de las fuentes (redondeado para arriba)
            if (cantidadFuentes >= (totalFuentes + 1) / 2) {
                Hecho hecho = hechoPorTitulo.get(titulo);
                hechosConsensuados.add(hecho);
            }
        }

        return hechosConsensuados;
    }
}

