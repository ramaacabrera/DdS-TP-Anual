package ApiAdministrativa;

import ApiAdministrativa.Presentacion.*;
import Agregador.Persistencia.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class MainAPIAdmin {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiAdmin"));

        System.out.println("Iniciando servidor Javalin en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        //ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        //SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.post("/api/colecciones", new PostColeccionHandler()); //creo coleccion PROBADOOOO
        app.get("/api/colecciones", new GetColeccionesHandler()); // consulta todas las colecciones PROBADOOOO
        app.get("/api/colecciones/{id}", new GetColeccionHandler()); // lee una coleccion en particular PROBADOOOOOO
        app.put("/api/colecciones/{id}", new PutColeccionHandler()); // actualizo una coleccion PROBADOOOOOO
        app.delete("/api/colecciones/{id}", new DeleteColeccionesHandler()); // borro una coleccion probadoooooo

        app.post("/api/colecciones/{id}/fuente", new PostFuentesColeccionHandler()); // agrego fuentes probbadisimooooo
        app.delete("/api/colecciones/{id}/fuente", new DeleteFuenteHandler()); // borro una fuente

        app.put("/api/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler()); // actualizo algoritmo probado

        app.put("/api/solicitudes/{id}", new PutSolicitudEliminacionHandler()); // aprobar o denegar solicitud eliminacion
        app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler()); // consulta todas las solicitudes
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler()); // consulta una solicitud por id
    }


}
