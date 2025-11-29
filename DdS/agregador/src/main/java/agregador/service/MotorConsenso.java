package agregador.service;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.repository.ColeccionRepositorio;
import java.util.List;

public class MotorConsenso {

    private final ColeccionRepositorio coleccionRepositorio;

    public MotorConsenso(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    public void ejecutar() {
        System.out.println("--- Ejecutando Motor de Consenso ---");
        List<Coleccion> colecciones = coleccionRepositorio.obtenerTodas();

        for (Coleccion coleccion : colecciones) {
            boolean huboCambios = coleccion.ejecutarAlgoritmoDeConsenso();

            if (huboCambios) {
                coleccionRepositorio.actualizar(coleccion);
                System.out.println("Consenso actualizado para colección: " + coleccion.getTitulo());
            }
        }
    }
}
