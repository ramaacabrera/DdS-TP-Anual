import repository.DinamicoRepositorio;
import controller.*;
import service.*;
import domain.fuente.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import conexionAgregador.ClienteDelAgregador;
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

        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDinamica(new HechosDinamicoService(dinamicoRepositorio), new SolicitudesModificacionService(dinamicoRepositorio), new SolicitudesEliminacionService(dinamicoRepositorio)));
        cliente.conectar(fuente);

        //app.get("/hechos", new GetHechosDinamicoHandler(dinamicoRepositorio));
        //app.get("/solicitudesModificacion", new GetSolicitudesModificacionHandler(dinamicoRepositorio));
        //app.get("/solicitudesEliminacion", new GetSolicitudesEliminacionHandler(dinamicoRepositorio));


        app.post("/hechos", new PostHechosHandler(new HechosDinamicoService(dinamicoRepositorio)));
        app.post("/solicitudesModificacion", new PostSolicitudesModificacionHandler(new SolicitudesModificacionService(dinamicoRepositorio)));
        app.post("/solicitudesEliminacion", new PostSolicitudesEliminacionHandler(new SolicitudesEliminacionService(dinamicoRepositorio)));

        // DEJO LOS POST PORQUE CREO QUE SE USAN PARA CREAR LOS HECHOS DESDE EL FRONT, LOS GET SE USAN MEDIANTE EL CLIENTEDELAGREGADOR
    }
}
