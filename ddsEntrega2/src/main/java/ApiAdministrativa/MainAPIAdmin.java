package ApiAdministrativa;

import ApiAdministrativa.Presentacion.*;
import Persistencia.*;
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

        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.post("/api/colecciones", new PostColeccionHandler(coleccionRepositorio)); //creo coleccion PROBADOOOO
        app.get("/api/colecciones", new GetColeccionesHandler(coleccionRepositorio)); // consulta todas las colecciones PROBADOOOO
        app.get("/api/colecciones/{id}", new GetColeccionHandler(coleccionRepositorio)); // lee una coleccion en particular PROBADOOOOOO
        app.put("/api/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio)); // actualizo una coleccion PROBADOOOOOO
        app.delete("/api/colecciones/{id}", new DeleteColeccionesHandler(coleccionRepositorio)); // borro una coleccion probadoooooo

        app.post("/api/colecciones/{id}/fuente", new PostFuentesColeccionHandler(coleccionRepositorio)); // agrego fuentes probbadisimooooo
        app.delete("/api/colecciones/{id}/fuente", new DeleteFuenteHandler(coleccionRepositorio)); // borro una fuente

        app.put("/api/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio)); // actualizo algoritmo probado

        app.put("/api/solicitudes/{id}", new PutSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // aprobar o denegar solicitud eliminacion
        app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(solicitudEliminacionRepositorio)); // consulta todas las solicitudes
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // consulta una solicitud por id
    }


}
