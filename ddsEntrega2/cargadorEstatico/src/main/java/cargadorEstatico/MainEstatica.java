package cargadorEstatico;

import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import com.fasterxml.jackson.core.JsonProcessingException;
import utils.ClienteDelAgregador;
import utils.Controladores.ControladorEstatica;
import utils.LecturaConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class MainEstatica {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // lectura archivo de configuraciones
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        String fileServer = config.getProperty("fileServer");
        String urlAgregador = config.getProperty("urlAgregador");

        Path carpeta = Paths.get(fileServer);
        String pathGuia = carpeta.resolve("guia.csv").toString();

        //limpiarGuia(pathGuia); descomentar solo si se quiere que se carguen nuevamente los hechos cada vez que se runee

        Fuente fuente = new Fuente(TipoDeFuente.ESTATICA, "");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorEstatica(new GetHechosEstaticoHandler(fileServer, fuente)));
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

