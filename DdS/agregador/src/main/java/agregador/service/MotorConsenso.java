package agregador.service;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.repository.ColeccionRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class MotorConsenso {

    private final ColeccionRepositorio coleccionRepositorio;

    public MotorConsenso(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    public void ejecutar() {
        System.out.println("--- Ejecutando Motor de Consenso ---");
        List<Coleccion> colecciones = coleccionRepositorio.obtenerTodas();

        System.out.println("Iterando colecciones (" + colecciones.size() + "):");
        ObjectMapper o = new ObjectMapper();
        for (Coleccion coleccion : colecciones) {
            System.out.println("Coleccion: " +  coleccion.getTitulo());
            boolean huboCambios = coleccion.ejecutarAlgoritmoDeConsenso();

            if (huboCambios) {
                coleccionRepositorio.actualizar(coleccion);
                System.out.println("Consenso actualizado para colección: " + coleccion.getTitulo());
            }
        }
    }
}
