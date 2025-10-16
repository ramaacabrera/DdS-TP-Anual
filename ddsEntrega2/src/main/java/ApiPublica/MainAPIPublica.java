package ApiPublica;

import io.javalin.Javalin;
import ApiPublica.Presentacion.*;
import utils.IniciadorApp;
import utils.LecturaConfig;
import Agregador.fuente.*;

import java.util.Properties;

public class MainAPIPublica {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiPublica"));
        int puertoDinamica = Integer.parseInt(config.getProperty("puertoDinamico"));

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

//        HechoRepositorio hechoRepositorio = new HechoRepositorio();
//        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();
//        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
//        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
//        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        //Inicializacion de Controller
        //ControllerSubirHechos controllerSubirHechos = new ControllerSubirHechos(dinamicoRepositorio);
        //ControllerSolicitud controllerSolicitud = new ControllerSolicitud(dinamicoRepositorio);

        // API publica
        app.get("/api/hechos", new GetHechosHandler()); // consulto todos los hechos PROBADOOOOO
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler()); // consulta hechos de una coleccion (pudiendo mandar criterios o no)
        app.post("/api/hechos", new PostHechoHandler(puertoDinamica)); //creo hecho PROBADOOOOOOOO
        app.post("/api/solicitudesEliminacion", new PostSolicitudEliminacionHandler(puertoDinamica)); //creo solicitud PROBADOOOOOOO
        app.post("/api/solicitudesModificacion", new PostSolicitudModificacionHandler(puertoDinamica));
    }
}
