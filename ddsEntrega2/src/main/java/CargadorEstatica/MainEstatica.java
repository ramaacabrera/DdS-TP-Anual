package CargadorEstatica;

import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class MainEstatica {
    public static void main(String[] args) throws InterruptedException {
        // lectura archivo de configuraciones
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoEstatico"));
        String fileServer = config.getProperty("fileServer");

        //Limpiar guia
        Path carpeta = Paths.get(fileServer);
        String pathGuia = carpeta.resolve("guia.csv").toString();

        limpiarGuia(pathGuia);

        //Inicio app de javalin
        System.out.println("Iniciando servidor Componente Estatico en el puerto "+ puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        app.get("/hechos", new GetHechosEstaticoHandler(fileServer));
        app.get("/reprocesado/hechos/{nombre}", new GetReprocesadoHechosEstaticoHandler(fileServer));
    }


    public static void limpiarGuia(String path) {
        try {
            Path pathGuia = Paths.get(path);
            // Sobrescribe el archivo con contenido vacío
            Files.write(pathGuia, new byte[0], StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error al limpiar guia.csv: " + e.getMessage());
        }
    }
}

