package ApiPublica;

import io.javalin.Javalin;
import ApiPublica.Presentacion.*;
import utils.IniciadorApp;
import utils.LecturaConfig;
<<<<<<<< HEAD:DdS/apiPublica/src/main/java/ApiPublica/MainAPIPublica.java
import utils.Persistencia.*;
========
import Agregador.Persistencia.*;
>>>>>>>> 198c43e (Pruebas):ddsEntrega2/apiPublica/src/main/java/ApiPublica/MainAPIPublica.java

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

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();

        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio));
        app.get("/api/hechos/{id}", new GetHechoEspecificoHandler(hechoRepositorio));
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(coleccionRepositorio));
        app.post("/api/hechos", new PostHechoHandler(puertoDinamica));
        app.post("/api/solicitudEliminacion", new PostSolicitudEliminacionHandler(puertoDinamica));
        app.post("/api/solicitudeModificacion", new PostSolicitudModificacionHandler(puertoDinamica));
    }
}
