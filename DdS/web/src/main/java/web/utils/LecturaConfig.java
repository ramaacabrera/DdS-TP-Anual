package web.utils;

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

        cargarEnv(config, "PUERTO_WEB");
        cargarEnv(config, "URL_ADMINISTRATIVO");
        cargarEnv(config, "URL_PUBLICA");
        cargarEnv(config, "URL_ESTADISTICAS");
        cargarEnv(config, "URL_WEB");
        cargarEnv(config, "CLOUDINARY_URL");
        cargarEnv(config, "CLOUDINARY_PRESET");
        cargarEnv(config, "CLIENT_ID");
        cargarEnv(config, "CLIENT_SECRET");
        cargarEnv(config, "REDIRECT_URL");
        cargarEnv(config, "URL_KEYCLOAK");

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