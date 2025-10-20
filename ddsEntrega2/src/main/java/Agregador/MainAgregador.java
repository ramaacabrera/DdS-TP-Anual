package Agregador;

import Agregador.PaqueteNormalizador.MockNormalizador;
import Agregador.PaqueteAgregador.Agregador;
import Agregador.Cargador.ConexionCargador;
import Agregador.Presentacion.*;
import Agregador.Persistencia.*;
import CargadorDemo.DemoLoader;
import CargadorMetamapa.MetamapaLoader;
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

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("agregador-PU");
        //EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoAgregador, "/");

        HechoRepositorio hechoRepositorio = new HechoRepositorio(emf);
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio(emf);
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio(emf);
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio(emf);
        FuenteRepositorio fuenteRepositorio = new FuenteRepositorio(emf);

        //String urlMetamapaExterna = config.getProperty("urlMetamapaExterna");
        //String urlDemoMock = config.getProperty("urlMock");

        //MetamapaLoader metamapaLoader = new MetamapaLoader(urlMetamapaExterna);
        //DemoLoader demoLoader = new DemoLoader(urlDemoMock);
        MockNormalizador mockNormalizador = new MockNormalizador();
        ConexionCargador conexionCargador = new ConexionCargador();

        Agregador agregador = new Agregador(
                hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio,
                solicitudModificacionRepositorio, fuenteRepositorio, mockNormalizador, conexionCargador);

        app.get("/hechos", new GetHechosRepoHandler(hechoRepositorio));

        //Colecciones
        app.get("/colecciones", new GetColeccionesRepoHandler(coleccionRepositorio));
        app.get("/colecciones/{id}", new GetColeccionEspecificaRepoHandler(coleccionRepositorio));
        app.post("/colecciones", new PostColeccionRepoHandler(coleccionRepositorio));
        app.put("/colecciones/{id}", new PutColeccionRepoHandler(coleccionRepositorio));
        app.delete("/colecciones/{id}", new DeleteColeccionesRepoHandler(coleccionRepositorio));
        app.post("/colecciones/{id}/fuente", new PostFuentesColeccionRepoHandler(coleccionRepositorio));
        app.delete("/colecciones/{id}/fuente", new DeleteFuenteRepoHandler(coleccionRepositorio));
        app.put("/colecciones/{id}/algoritmo", new PutAlgoritmoDeConsensoRepoHandler(coleccionRepositorio));

        //Solicitudes
        app.post("/solicitudes", new PostSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));
        app.put("/solicitudes/{id}", new PutSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));
        app.get("/solicitudesEliminacion", new GetSolicitudesEliminacionRepoHandler(solicitudEliminacionRepositorio));

        //Cargadores
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
