package CargadorDinamica;

import CargadorDinamica.Presentacion.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class MainDinamica {
    public static void main(String[] args) throws InterruptedException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoDinamico"));

        System.out.println("Iniciando servidor Javalin en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

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
