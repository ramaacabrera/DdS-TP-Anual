package gestorAdministrativo;

import gestorAdministrativo.controller.*;
import gestorAdministrativo.service.*;
import gestorAdministrativo.repository.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;
import utils.Keycloak.TokenValidator;

import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("PUERTO_GESTOR_ADMINISTRATIVO"));

        System.out.println("Iniciando API Administrativa en el puerto " + puerto);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        FuenteRepositorio fuenteRepositorio = new FuenteRepositorio();

        // 2. Services
        ColeccionService coleccionService = new ColeccionService(coleccionRepositorio, hechoRepositorio, fuenteRepositorio);
        SolicitudEliminacionService solicitudEliminacionService = new SolicitudEliminacionService(solicitudEliminacionRepositorio, hechoRepositorio, usuarioRepositorio);
        SolicitudModificacionService solicitudModificacionService = new SolicitudModificacionService(solicitudModificacionRepositorio, hechoRepositorio, usuarioRepositorio);
        HechoService hechoService = new HechoService(hechoRepositorio);
        // 3. Controllers
        ColeccionController coleccionController = new ColeccionController(coleccionService);
        SolicitudController solicitudController = new SolicitudController(solicitudEliminacionService, solicitudModificacionService);
        VerificacionController verificacionController = new VerificacionController();
        HechoController hechoController = new HechoController(hechoService);
        // RUTAS
        app.before("/api/*", verificacionController.verificarAdministrador);

        //Hechos

        app.patch("/api/hecho/{hechoId}/etiquetas", hechoController.agregarEtiquetas);

        // Health check
        app.get("/health", ctx -> { ctx.status(200).result("OK");});

        // Rutas de Colecciones
        app.post("/api/colecciones", coleccionController.crearColeccion);
        app.put("/api/colecciones/{id}", coleccionController.actualizarColeccion);
        app.delete("/api/colecciones/{id}", coleccionController.eliminarColeccion);

        // Sub-rutas de Colecciones
        app.post("/api/colecciones/{id}/agregador.fuente", coleccionController.agregarFuente);
        app.delete("/api/colecciones/{id}/agregador.fuente", coleccionController.borrarFuente);
        app.put("/api/colecciones/{id}/algoritmo", coleccionController.actualizarAlgoritmoConsenso);
        app.get("/api/fuentes", coleccionController.obtenerTodasLasFuentes);

        // Rutas Solicitud Eliminacion
        //app.post("/api/solicitudes", solicitudController.crearSolicitud);
        app.patch("/api/solicitudes/{id}", solicitudController.procesarSolicitud);
        app.get("/api/solicitudes", solicitudController.obtenerSolicitudes);
        app.get("/api/solicitudes/cantidad", solicitudController.obtenerCantidadPendientesEliminacion);
        app.get("/api/solicitudes/{id}", solicitudController.obtenerSolicitud);

        // Rutas Solicitud Modificacion
        //app.post("/api/solicitudes/modificacion", solicitudController.crearSolicitudModificacion);
        app.patch("/api/solicitudes/modificacion/{id}", solicitudController.procesarSolicitudModificacion);
        app.get("/api/solicitudes/modificacion/listado", solicitudController.obtenerSolicitudesModificacion);
        app.get("/api/solicitudes/modificacion/cantidad", solicitudController.obtenerCantidadPendientesModificacion);
        app.get("/api/solicitudes/modificacion/{id}", solicitudController.obtenerSolicitudModificacion);
    }
}