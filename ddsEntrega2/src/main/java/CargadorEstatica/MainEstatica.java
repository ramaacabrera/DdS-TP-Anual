package CargadorEstatica;

import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import utils.ClienteDelAgregador;
import utils.ConexionAlAgregador;
import utils.Controladores.ControladorEstatica;
import utils.DTO.FuenteDTO;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        //int puerto = Integer.parseInt(config.getProperty("puertoEstatico"));
        String fileServer = config.getProperty("fileServer");
        String urlAgregador = config.getProperty("urlAgregador");

        //Limpiar guia
        Path carpeta = Paths.get(fileServer);
        String pathGuia = carpeta.resolve("guia.csv").toString();

        //limpiarGuia(pathGuia); descomentar solo si se quiere que se carguen nuevamente los hechos cada vez que se runee

        //Inicio app de javalin
        //System.out.println("Iniciando servidor Componente Estatico en el puerto "+ puerto);
        //IniciadorApp iniciador = new IniciadorApp();
        //Javalin app = iniciador.iniciarApp(puerto, "/");

        //ConexionAlAgregador agregador = new ConexionAlAgregador();
        //agregador.conectarse(TipoDeFuente.ESTATICA, config.getProperty("puertoEstatico"));

        FuenteDTO fuente = new FuenteDTO(TipoDeFuente.DINAMICA, "");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorEstatica());
        cliente.conectar(fuente);

        //app.get("/hechos", new GetHechosEstaticoHandler(fileServer, fuente));
        //app.get("/reprocesado/hechos/{nombre}", new GetReprocesadoHechosEstaticoHandler(fileServer, fuente));
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

