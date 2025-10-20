package ApiPublica;

import io.javalin.Javalin;
import ApiPublica.Presentacion.*;
import utils.IniciadorApp;
import utils.LecturaConfig;
import Agregador.Persistencia.*;
import javax.persistence.*;
import java.util.Properties;

public class MainAPIPublica {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiPublica"));
        int puertoDinamica = Integer.parseInt(config.getProperty("puertoDinamica"));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("demo-hibernate-PU");
        EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        HechoRepositorio hechoRepositorio = new HechoRepositorio(em);
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio(em);

        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio));
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler());
        app.post("/api/hechos", new PostHechoHandler(puertoDinamica));
        app.post("/api/solicitudes", new PostSolicitudEliminacionHandler(puertoDinamica));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando EntityManager de API Pública...");
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
        }));
    }
}
