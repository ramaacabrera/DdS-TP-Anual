package estadisticas.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LecturaConfig {

    public Properties leerConfig() {
        Properties config = new Properties();
        try {
            InputStream input = LecturaConfig.class.getClassLoader()
                    .getResourceAsStream("componentes.properties");

            if (input == null) {
                throw new FileNotFoundException("No se encontró el archivo componentes.properties en el classpath");
            }

            config.load(input);
            input.close(); // IMPORTANTE: cerrar el stream
            return config;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de propiedades: " + e.getMessage());
            return null;
        }
    }

    public Properties leerConfig(String nombreArchivo) {
        Properties config = new Properties();
        try {
            InputStream input = LecturaConfig.class.getClassLoader()
                    .getResourceAsStream(nombreArchivo);

            if (input == null) {
                throw new FileNotFoundException("No se encontró el archivo " + nombreArchivo + " en el classpath");
            }

            config.load(input);
            input.close();
            return config;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
            return null;
        }
    }
}