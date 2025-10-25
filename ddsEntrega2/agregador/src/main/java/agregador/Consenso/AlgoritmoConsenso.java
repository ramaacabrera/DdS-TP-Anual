<<<<<<<< HEAD:DdS/utils/src/main/java/utils/Dominio/Consenso/AlgoritmoConsenso.java
package utils.Dominio.Consenso;

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Dominio.HechosYColecciones.Hecho;
========
package agregador.Consenso;

import agregador.HechosYColecciones.Coleccion;
import agregador.HechosYColecciones.Hecho;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/agregador/src/main/java/agregador/Consenso/AlgoritmoConsenso.java

import java.util.List;

public abstract class AlgoritmoConsenso {
    public abstract List<Hecho> obtenerHechosConsensuados(Coleccion coleccion);
}
