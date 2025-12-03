package cargadorDinamico;

import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.controller.*;
import cargadorDinamico.repository.HechoRepositorio;
import cargadorDinamico.service.*;
import cargadorDinamico.domain.fuente.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import cargadorDinamico.conexionAgregador.ClienteDelAgregador;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("PUERTO_DINAMICO"));
        String urlAgregador = config.getProperty("URL_AGREGADOR");

        System.out.println("Iniciando servidor Javalin en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();
        Fuente fuente = new Fuente(TipoDeFuente.DINAMICA, "DINAMICA");
        HechoRepositorio hechoRepositorio = new HechoRepositorio();

        HechosDinamicoService hechosDinamicoService = new HechosDinamicoService(dinamicoRepositorio, hechoRepositorio);
        SolicitudesModificacionService solicitudesModificacionService = new SolicitudesModificacionService(dinamicoRepositorio);
        SolicitudesEliminacionService solicitudesEliminacionService = new SolicitudesEliminacionService(dinamicoRepositorio);

        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDinamica(hechosDinamicoService, solicitudesModificacionService, solicitudesEliminacionService));
        cliente.conectar(fuente);

        app.post("/hechos", new PostHechosHandler(hechosDinamicoService));
        app.post("/solicitudesModificacion", new PostSolicitudesModificacionHandler(solicitudesModificacionService));
        app.post("/solicitudesEliminacion", new PostSolicitudesEliminacionHandler(solicitudesEliminacionService));

        // DEJO LOS POST PORQUE CREO QUE SE USAN PARA CREAR LOS HECHOS DESDE EL FRONT, LOS GET SE USAN MEDIANTE EL CLIENTEDELAGREGADOR
    }
}
