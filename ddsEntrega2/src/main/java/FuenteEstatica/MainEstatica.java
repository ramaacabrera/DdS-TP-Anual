package FuenteEstatica;

import io.javalin.Javalin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainEstatica {
    public static void main(String[] args) throws InterruptedException {
        // lectura archivo de configuraciones
        Properties config = new Properties();
        try{
            config.load(new FileInputStream("componenteEstatico.properties"));
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        int puerto = Integer.parseInt(config.getProperty("puerto"));


        //Inicio app de javalin
        System.out.println("Iniciando servidor Componente Estatico en el puerto "+ puerto);
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
        }).start(puerto);

        app.get("/hechos", new GetHechosEstaticoHandler())

    }
}
