package ApiAdministrativa;

import ApiAdministrativa.Presentacion.*;
import Persistencia.*;
import io.javalin.Javalin;

public class MainAPIAdmin {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando servidor Javalin en el puerto 8081...");
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
        }).start(8081);

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
