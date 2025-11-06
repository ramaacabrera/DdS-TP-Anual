package cargadorDinamico;

import cargadorDinamico.repository.DinamicoRepositorio;
import utils.Dominio.fuente.*;
import cargadorDinamico.controller.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import utils.ClienteDelAgregador;
import cargadorDinamico.controller.ControladorDinamica;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoDinamico"));
        String urlAgregador = config.getProperty("urlAgregador");

        System.out.println("Iniciando servidor Javalin en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();
        Fuente fuente = new Fuente(TipoDeFuente.DINAMICA, "DINAMICA");

        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDinamica(new GetHechosDinamicoHandler(dinamicoRepositorio), new GetSolicitudesModificacionHandler(dinamicoRepositorio), new GetSolicitudesEliminacionHandler(dinamicoRepositorio)));
        cliente.conectar(fuente);

        // Exposicion API mediante REST para el agregador
        app.get("/hechos", new GetHechosDinamicoHandler(dinamicoRepositorio));
        app.post("/hechos", new PostHechosHandler(dinamicoRepositorio));

        app.get("/solicitudesModificacion", new GetSolicitudesModificacionHandler(dinamicoRepositorio));
        app.post("/solicitudesModificacion", new PostSolicitudesModificacionHandler(dinamicoRepositorio));
        app.get("/solicitudesEliminacion", new GetSolicitudesEliminacionHandler(dinamicoRepositorio));
        app.post("/solicitudesEliminacion", new PostSolicitudesEliminacionHandler(dinamicoRepositorio));
    }
}
