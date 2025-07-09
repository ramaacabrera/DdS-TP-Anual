

import Persistencia.ColeccionRepositorio;
import Persistencia.HechoRepositorio;
import Persistencia.SolicitudEliminacionRepositorio;
import Persistencia.SolicitudModificacionRepositorio;
import io.javalin.Javalin;
import presentacion.*;
import org.example.fuenteProxy.APIMock.DemoAPIMockServer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //inicio el mock server para demo
        new Thread(() -> DemoAPIMockServer.main(new String[]{})).start();
        Thread.sleep(2000);

        System.out.println("Iniciando servidor Javalin en el puerto 8080...");
        Javalin app = Javalin.create(javalinConfig -> {
                            javalinConfig.plugins.enableCors(cors -> {
                                cors.add(it -> it.anyHost());
                            }); // para poder hacer requests de un dominio a otro
                            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
                        }).start(8080);

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio)); // consulto todos los hechos
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler()); //consulta hechos de una coleccion
        app.get("api/colecciones", new GetColeccionesHandler(coleccionRepositorio)); // consulta todas las colecciones
        app.get("api/colecciones/{id}", new GetColeccionHandler(coleccionRepositorio)); // lee una coleccion en particular
        app.post("/api/colecciones", new PostColeccionHandler(coleccionRepositorio)); //creo coleccion
        app.post("/api/hechos", new PostHechoHandler(hechoRepositorio)); //creo hecho
        app.post("/api/solicitudes/modificacion", new PostSolicitudModificacionHandler(solicitudModificacionRepositorio));
        app.post("/api/solicitudes/eliminacion", new PostSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); //creo solicitud
        app.post("api/colecciones/{id}", new PostFuentesColeccionHandler(coleccionRepositorio)); // agrego
        app.put("api/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio)); // actualizo una coleccion
        app.put("api/colecciones/{id}", new PutAlgoritmoConsensoHandler(coleccionRepositorio)); // actualizo algoritmo
        app.put("api/solicitudes/eliminacion/{id}", new PutSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // aprobar o denegar solicitud eliminacion
        app.delete("api/colecciones/{id}", new DeleteFuenteHandler(coleccionRepositorio)); // borro una fuente
        app.delete("api/colecciones", new DeleteColeccionesHandler(coleccionRepositorio)); // borro una coleccion
    }
}
