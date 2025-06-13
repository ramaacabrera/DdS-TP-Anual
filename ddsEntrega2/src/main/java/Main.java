
import io.javalin.Javalin;
import presentacion.*;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create().start(4567);

        /*app.get("/api/colecciones", new GetColeccionesHandler()); //consulto coleccion
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler()); //consulta hechos
        app.post("/api/colecciones", new PostColeccionHandler()); //creo coleccion
        app.post("/api/hechos", new PostHechoHandler()); //creo hecho
        app.post("/api/solicitudes", new PostSolicitudEliminacionHandler()); //creo solicitud
   */ }
}
