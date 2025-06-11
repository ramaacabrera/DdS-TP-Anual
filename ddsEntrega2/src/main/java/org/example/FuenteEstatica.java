package org.example;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FuenteEstatica extends Fuente {

    private String rutaArchivoCSV;

    public FuenteEstatica(String rutaArchivoCSV) {
        this.rutaArchivoCSV = rutaArchivoCSV;
    }

    @Override
    public List<Hecho> obtenerHechos() {
        // Leer el CSV y convertir cada línea en un Hecho??
        // USAR CONEXION CSV Q PUSIMOS EN EL D DE CLASES
        // this.conexion.obtenerHecho();

        return new ArrayList<>(); //
    }
}

