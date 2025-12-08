package cargadorEstatico;

import cargadorEstatico.domain.fuente.Fuente;
import cargadorEstatico.domain.fuente.TipoDeFuente;
import cargadorEstatico.service.HechosEstaticoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import cargadorEstatico.conexionAgregador.ClienteDelAgregador;
import cargadorEstatico.controller.ControladorEstatica;
import cargadorEstatico.utils.LecturaConfig;
import io.javalin.Javalin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // lectura archivo de configuraciones
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        String fileServer = config.getProperty("FILE_SERVER");
        String urlAgregador = config.getProperty("URL_AGREGADOR");

        Path carpeta = Paths.get(fileServer);
        String pathGuia = carpeta.resolve("guia.csv").toString();

        //limpiarGuia(pathGuia); descomentar solo si se quiere que se carguen nuevamente los hechos cada vez que se runee

        Javalin app = Javalin.create().start(8083);

        app.get("/health", ctx -> {ctx.status(200).result("OK");});

        System.out.println("Monitor de salud escuchando en puerto 8082");

        Fuente fuente = new Fuente(TipoDeFuente.ESTATICA, "ESTATICA");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorEstatica(new HechosEstaticoService(fileServer, fuente)));
        cliente.conectar(fuente);
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

