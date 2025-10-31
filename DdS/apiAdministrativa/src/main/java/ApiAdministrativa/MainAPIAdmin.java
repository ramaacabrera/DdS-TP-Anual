package ApiAdministrativa;

import ApiAdministrativa.Presentacion.*;
import utils.Persistencia.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

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
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio(hechoRepositorio);
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();

        app.before(new ValidarAdminHandler(usuarioRepositorio));


        app.post("/api/colecciones", new PostColeccionHandler(coleccionRepositorio, hechoRepositorio));
        app.put("/api/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio));
        app.delete("/api/colecciones/{id}", new DeleteColeccionesHandler(coleccionRepositorio));

        app.post("/api/colecciones/{id}/agregador.fuente", new PostFuentesColeccionHandler(coleccionRepositorio));
        app.delete("/api/colecciones/{id}/agregador.fuente", new DeleteFuenteHandler(coleccionRepositorio));
        app.put("/api/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio));

        app.patch("/api/solicitudes/{id}", new PatchSolicitudEliminacionHandler(solicitudEliminacionRepositorio));
        app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(solicitudEliminacionRepositorio));
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(solicitudEliminacionRepositorio));

       /* Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando EntityManager de API Admin...");
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
        }));*/
    }
}
