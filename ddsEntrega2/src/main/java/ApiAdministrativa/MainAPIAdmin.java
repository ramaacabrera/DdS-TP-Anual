package ApiAdministrativa;

import ApiAdministrativa.Presentacion.*;
import Agregador.Persistencia.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

public class MainAPIAdmin {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiAdmin"));

        System.out.println("Iniciando API Administrativa en el puerto " + puerto);

        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("agregador-PU");
        //EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.post("/api/colecciones", new PostColeccionHandler(coleccionRepositorio));
        app.get("/api/colecciones", new GetColeccionesHandler(coleccionRepositorio));
        app.get("/api/colecciones/{id}", new GetColeccionHandler(coleccionRepositorio));
        app.put("/api/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio));
        app.delete("/api/colecciones/{id}", new DeleteColeccionesHandler(coleccionRepositorio));

        app.post("/api/colecciones/{id}/fuente", new PostFuentesColeccionHandler(coleccionRepositorio));
        app.delete("/api/colecciones/{id}/fuente", new DeleteFuenteHandler(coleccionRepositorio));
        app.put("/api/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio));

        app.put("/api/solicitudes/{id}", new PutSolicitudEliminacionHandler(solicitudEliminacionRepositorio));
        app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(solicitudEliminacionRepositorio));
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(solicitudEliminacionRepositorio));

       /* Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando EntityManager de API Admin...");
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
        }));*/
    }
}
