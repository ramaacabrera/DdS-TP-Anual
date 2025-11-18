package gestorAdministrativo;

import gestorAdministrativo.controller.*;
import gestorAdministrativo.service.*;
import gestorAdministrativo.repository.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiAdmin"));

        System.out.println("Iniciando API Administrativa en el puerto " + puerto);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        // Repositorios
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio(hechoRepositorio);

        // Services
        ColeccionService coleccionService = new ColeccionService(coleccionRepositorio, hechoRepositorio);
        SolicitudEliminacionService solicitudService = new SolicitudEliminacionService(solicitudEliminacionRepositorio);

        // Controllers
        ColeccionController coleccionController = new ColeccionController(coleccionService);
        SolicitudController solicitudController = new SolicitudController(solicitudService);

        // Rutas con controllers
        app.post("/api/colecciones", coleccionController.crearColeccion);
        app.get("/api/colecciones", coleccionController.obtenerTodasLasColecciones);
        app.get("/api/colecciones/{id}", coleccionController.obtenerColeccionPorId);
        app.put("/api/colecciones/{id}", coleccionController.actualizarColeccion);
        app.delete("/api/colecciones/{id}", coleccionController.eliminarColeccion);

        // Rutas específicas
        app.post("/api/colecciones/{id}/agregador.fuente", coleccionController.agregarFuente);
        app.delete("/api/colecciones/{id}/agregador.fuente", coleccionController.borrarFuente);
        app.put("/api/colecciones/{id}/algoritmo", coleccionController.actualizarAlgoritmoConsenso);

        app.post("/api/solicitudes", solicitudController.crearSolicitud);
        app.patch("/api/solicitudes/{id}", solicitudController.procesarSolicitud);
        app.get("/api/solicitudes", solicitudController.obtenerSolicitudes);
        app.get("/api/solicitudes/{id}", solicitudController.obtenerSolicitud);
    }
}
