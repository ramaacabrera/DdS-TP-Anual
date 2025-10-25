package cargadorDinamico;

import cargadorDinamico.Dominio.fuente.Fuente;
import cargadorDinamico.Dominio.fuente.TipoDeFuente;
import cargadorDinamico.Presentacion.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import utils.ClienteDelAgregador;
import utils.Controladores.ControladorDinamica;
import utils.IniciadorApp;
import utils.LecturaConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

public class MainDinamica {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoDinamico"));
        String urlAgregador = config.getProperty("urlAgregador");

        System.out.println("Iniciando servidor Javalin en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dinamico-PU");
        EntityManager emDinamico = emf.createEntityManager();

        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio(emDinamico);
        Fuente fuente = new Fuente(TipoDeFuente.DINAMICA, "http://localhost:"+puerto);

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
