package cargadorEstatico.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LecturaConfig {

    public Properties leerConfig() {
        return leerConfig("componentes.properties");
    }

    public Properties leerConfig(String nombreArchivo) {
        Properties config = new Properties();

        try (InputStream input = LecturaConfig.class.getClassLoader().getResourceAsStream(nombreArchivo)) {
            if (input != null) {
                config.load(input);
            } else {
                System.out.println("Advertencia: No se encontró '" + nombreArchivo + "' en el classpath. Usando variables de entorno.");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
        }

        cargarEnv(config, "FILE_SERVER");
        cargarEnv(config, "URL_AGREGADOR");
        cargarEnv(config, "PUERTO_CARGADOR");
        cargarEnv(config, "NEW_RELIC_LICENSE_KEY");

        return config;
    }

    private void cargarEnv(Properties prop, String key) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isEmpty()) {
            prop.setProperty(key, envValue);
        }
    }
}