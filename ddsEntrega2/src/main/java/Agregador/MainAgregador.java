package Agregador;

import Agregador.PaqueteNormalizador.MockNormalizador;
import Agregador.PaqueteAgregador.Agregador;
import Agregador.Cargador.ConexionCargador;
import Agregador.Presentacion.*;
import Agregador.Persistencia.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

public class MainAgregador {
    public static void main(String[] args) throws InterruptedException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puertoAgregador = Integer.parseInt(config.getProperty("puertoAgregador"));

        System.out.println("Iniciando servidor Agregador en el puerto "+puertoAgregador);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("demo-hibernate-PU");
        EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoAgregador, "/");

        HechoRepositorio hechoRepositorio = new HechoRepositorio(em);
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio(em);
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio(em);
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio(em);

        MockNormalizador mockNormalizador = new MockNormalizador();
        ConexionCargador conexionCargador = new ConexionCargador();

        Agregador agregador = new Agregador(hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio, solicitudModificacionRepositorio, mockNormalizador, conexionCargador);

        app.get("/hechos", new GetHechosRepoHandler(hechoRepositorio));
        app.get("/solicitudesEliminacion", new GetSolicitudesEliminacionRepoHandler(solicitudEliminacionRepositorio));
        app.get("/colecciones", new GetColeccionesRepoHandler(coleccionRepositorio));
        app.get("/colecciones/{id}", new GetColeccionEspecificaRepoHandler(coleccionRepositorio));

        app.post("/colecciones", new PostColeccionRepoHandler(coleccionRepositorio));

        app.post("/solicitudes", new PostSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));

        app.put("/colecciones/{id}", new PutColeccionRepoHandler(coleccionRepositorio));
        app.delete("/colecciones/{id}", new DeleteColeccionesRepoHandler(coleccionRepositorio));

        app.post("/colecciones/{id}/fuente", new PostFuentesColeccionRepoHandler(coleccionRepositorio));
        app.delete("/colecciones/{id}/fuente", new DeleteFuenteRepoHandler(coleccionRepositorio));

        app.put("/colecciones/{id}/algoritmo", new PutAlgoritmoDeConsensoRepoHandler(coleccionRepositorio));

        app.put("/solicitudes/{id}", new PutSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));


        //    <<<<<<<<<-

        app.post("/cargador", new PostFuenteHandler(conexionCargador));

        app.get("/cargador", new GetFuentesHandler(conexionCargador));

        app.delete("/cargador/{id}", new DeleteFuenteHandler(conexionCargador));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando EntityManager y EntityManagerFactory...");
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
        }));
    }


}
