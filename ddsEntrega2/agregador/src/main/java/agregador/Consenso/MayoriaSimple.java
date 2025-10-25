<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Consenso/MayoriaSimple.java
package utils.Dominio.Consenso;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Dominio.fuente.Fuente;
========
package agregador.Consenso;
import agregador.HechosYColecciones.Coleccion;
import agregador.HechosYColecciones.Hecho;
import agregador.fuente.Fuente;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Consenso/MayoriaSimple.java

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

