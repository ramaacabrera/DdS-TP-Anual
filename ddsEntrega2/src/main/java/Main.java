
import io.javalin.Javalin;
import presentacion.*;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(javalinConfig -> {
                            javalinConfig.plugins.enableCors(cors -> {
                                cors.add(it -> it.anyHost());
                            }); // para poder hacer requests de un dominio a otro

                            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
                        }

                )
                .start(8080);

        app.get("/api/hechos", new GetColeccionesHandler()); //consulto coleccion
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler()); //consulta hechos
        app.post("/api/colecciones", new PostColeccionHandler()); //creo coleccion
        app.post("/api/hechos", new PostHechoHandler()); //creo hecho
        app.post("/api/solicitudes", new PostSolicitudEliminacionHandler()); //creo solicitud
    }
}
