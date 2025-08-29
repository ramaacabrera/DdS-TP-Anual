package FuenteEstatica;

import io.javalin.Javalin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainEstatica {
    public static void main(String[] args) throws InterruptedException {
        // lectura archivo de configuraciones
        Properties config = new Properties();
        try{
            InputStream input = MainEstatica.class.getClassLoader()
                    .getResourceAsStream("componenteEstatico.properties");

            if (input == null) {
                throw new FileNotFoundException("No se encontró el archivo de propiedades");
            }
            config.load(input);
        }
        catch (IOException e) {
            System.out.println("Error al leer el archivo de propiedades");
            System.err.println(e.getMessage());
        }
        int puerto = Integer.parseInt(config.getProperty("puerto"));
        String fileServer = config.getProperty("fileServer");

        //Inicio app de javalin
        System.out.println("Iniciando servidor Componente Estatico en el puerto "+ puerto);
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
        }).start(puerto);

        app.get("/hechos", new GetHechosEstaticoHandler(fileServer));
    }
}
