package CargadorDinamica;

import Agregador.fuente.TipoDeFuente;
import CargadorDinamica.Presentacion.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import utils.ConexionAlAgregador;
import utils.DTO.FuenteDTO;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class MainDinamica {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoDinamico"));

        System.out.println("Iniciando servidor Javalin en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        ConexionAlAgregador agregador = new ConexionAlAgregador();
        agregador.conectarse(TipoDeFuente.DINAMICA, config.getProperty("puertoDinamico"));

        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();

        // Exposicion API mediante REST para el agregador
        app.get("/hechos", new GetHechosDinamicoHandler(dinamicoRepositorio));
        app.post("/hechos", new PostHechosHandler(dinamicoRepositorio));

        app.get("/solicitudesModificacion", new GetSolicitudesModificacionHandler(dinamicoRepositorio));
        app.post("/solicitudesModificacion", new PostSolicitudesModificacionHandler(dinamicoRepositorio));
        app.get("/solicitudesEliminacion", new GetSolicitudesEliminacionHandler(dinamicoRepositorio));
        app.post("/solicitudesEliminacion", new PostSolicitudesEliminacionHandler(dinamicoRepositorio));
    }
}
