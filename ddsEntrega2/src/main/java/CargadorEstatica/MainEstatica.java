package CargadorEstatica;

import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class MainEstatica {
    public static void main(String[] args) throws InterruptedException {
        // lectura archivo de configuraciones
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoEstatico"));
        String fileServer = config.getProperty("fileServer");

        //Inicio app de javalin
        System.out.println("Iniciando servidor Componente Estatico en el puerto "+ puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        app.get("/hechos", new GetHechosEstaticoHandler(fileServer));
        app.get("/reprocesado/hechos/{nombre}", new GetReprocesadoHechosEstaticoHandler(fileServer));
    }
}
