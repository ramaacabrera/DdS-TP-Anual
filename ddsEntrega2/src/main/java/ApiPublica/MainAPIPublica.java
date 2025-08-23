package ApiPublica;

import FuenteDinamica.ControllerSolicitud;
import FuenteDinamica.ControllerSubirHechos;
import Persistencia.*;
import io.javalin.Javalin;
import ApiPublica.Presentacion.*;

public class MainAPIPublica {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("Iniciando servidor Javalin en el puerto 8080...");
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
        }).start(8080);

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        //Inicializacion de Controller
        ControllerSubirHechos controllerSubirHechos = new ControllerSubirHechos(dinamicoRepositorio);
        ControllerSolicitud controllerSolicitud = new ControllerSolicitud(dinamicoRepositorio);

        // API publica
        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio)); // consulto todos los hechos PROBADOOOOO
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(coleccionRepositorio)); //consulta hechos de una coleccion (pudiendo mandar criterios o no)
        app.post("/api/hechos", new PostHechoHandler(controllerSubirHechos)); //creo hecho PROBADOOOOOOOO
        app.post("/api/solicitudes", new PostSolicitudEliminacionHandler(controllerSolicitud)); //creo solicitud PROBADOOOOOOO
    }
}
