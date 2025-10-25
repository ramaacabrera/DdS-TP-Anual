<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Consenso/Absoluta.java
package utils.Dominio.Consenso;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.fuente.Fuente;
========
package agregador.Consenso;
import agregador.HechosYColecciones.Coleccion;
import agregador.HechosYColecciones.Hecho;
import agregador.fuente.Fuente;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Consenso/Absoluta.java

import java.util.*;

public class Absoluta extends AlgoritmoConsenso {

    @Override
    public List<Hecho> obtenerHechosConsensuados(Coleccion coleccion){
        List<Hecho> hechos = coleccion.getHechos();
        List<Fuente> fuentes = coleccion.getFuente(); // fuentes distintas
        int totalFuentes = fuentes.size();

        // título → set de fuentes que mencionan ese título
        Map<String, Set<Fuente>> fuentesPorTitulo = new HashMap<>();

        // título → un Hecho representativo con ese título
        Map<String, Hecho> hechoPorTitulo = new HashMap<>();

        for (Hecho hecho : hechos) {
            String titulo = hecho.getTitulo();
            Fuente fuente = hecho.getFuente();

            fuentesPorTitulo.putIfAbsent(titulo, new HashSet<>());
            fuentesPorTitulo.get(titulo).add(fuente);

            hechoPorTitulo.putIfAbsent(titulo, hecho);
        }

        // Solo quedan los hechos cuyos títulos fueron mencionados por TODAS las fuentes
        List<Hecho> hechosConsensuados = new ArrayList<>();

        for (Map.Entry<String, Set<Fuente>> entry : fuentesPorTitulo.entrySet()) {
            String titulo = entry.getKey();
            Set<Fuente> fuentesQueLoTienen = entry.getValue();

            if (fuentesQueLoTienen.size() == totalFuentes) {
                hechosConsensuados.add(hechoPorTitulo.get(titulo));
            }
        }

        return hechosConsensuados;
    }
}
