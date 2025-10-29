package ApiPublica;

import io.javalin.Javalin;
import ApiPublica.Presentacion.*;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONArray;
import org.json.JSONObject;
import utils.IniciadorApp;
import utils.KeyCloak.TokenValidator;
import utils.LecturaConfig;
import Agregador.Persistencia.*;
import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MainAPIPublica {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiPublica"));
        int puertoDinamica = Integer.parseInt(config.getProperty("puertoDinamico"));

        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("agregador-PU");
        //EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio));
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler());
        app.post("/api/hechos", new PostHechoHandler(puertoDinamica));
        app.post("/api/solicitudEliminacion", new PostSolicitudEliminacionHandler(puertoDinamica));
        app.post("/api/solicitudeModificacion", new PostSolicitudModificacionHandler(puertoDinamica));

        // Login
        app.get("/api/login", new GetLoginHandler());
        app.post("/api/login", new PostLoginHandler(usuarioRepositorio));

        // Sign in
        app.get("/api/sign-in", new GetSignInHandler());
        app.post("/api/sign-in", new PostSignInHandler(usuarioRepositorio));

        // Home
        app.get("/api/home", new GetHomeHandler());



        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando EntityManager de API Pública...");
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
        }));*/
    }

}
