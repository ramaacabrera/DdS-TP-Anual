package ApiPublica;

import io.javalin.Javalin;
import ApiPublica.Presentacion.*;
import utils.IniciadorApp;
import utils.LecturaConfig;
import utils.Persistencia.*;

import java.util.Properties;

public class MainAPIPublica {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiPublica"));
        int puertoDinamica = Integer.parseInt(config.getProperty("puertoDinamico"));

        String urlWeb = config.getProperty("urlWeb");
        String servidorSSO = config.getProperty("urlServidorSSO");

        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("agregador-PU");
        //EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();

        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio));
        app.get("/api/hechos/{id}", new GetHechoEspecificoHandler(hechoRepositorio));
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(coleccionRepositorio));
        app.get("/api/categoria", new GetCategoriaHandler(hechoRepositorio));
        app.post("/api/hechos", new PostHechoHandler(puertoDinamica));
        app.post("/api/solicitudEliminacion", new PostSolicitudEliminacionHandler(puertoDinamica));
        app.post("/api/solicitudeModificacion", new PostSolicitudModificacionHandler(puertoDinamica));

        app.post("/api/login", new PostLoginHandler(usuarioRepositorio, urlWeb, servidorSSO));
        app.post("/api/sign-in", new PostSignInHandler(usuarioRepositorio, urlWeb));
    }
}
