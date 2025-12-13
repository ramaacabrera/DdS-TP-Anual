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

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        String fileServer = config.getProperty("FILE_SERVER", "/app/csv_files");

        String urlAgregador = config.getProperty("URL_AGREGADOR", "ws://agregador:8070/cargador");

        int puerto = Integer.parseInt(config.getProperty("PUERTO_CARGADOR", "8090"));

        System.out.println("   -> File Server Path: " + fileServer);
        System.out.println("   -> URL Agregador: " + urlAgregador);

        Path carpeta = Paths.get(fileServer);
        // String pathGuia = carpeta.resolve("guia.csv").toString();

        Javalin app = Javalin.create().start(puerto);

        app.get("/health", ctx -> {
            ctx.status(200).result("OK");
        });


        Fuente fuente = new Fuente(TipoDeFuente.ESTATICA, "ESTATICA");

        HechosEstaticoService service = new HechosEstaticoService(fileServer, fuente);
        ControladorEstatica controlador = new ControladorEstatica(service);

        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, controlador);

        try {
            cliente.conectar(fuente);
            System.out.println("Cliente WebSocket iniciado.");
        } catch (Exception e) {
            System.err.println("Error al conectar con el Agregador: " + e.getMessage());
        }
    }

    public static void limpiarGuia(String path) {
        try {
            Path pathGuia = Paths.get(path);
            Files.write(pathGuia, new byte[0], StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error al limpiar guia.csv: " + e.getMessage());
        }
    }
}