package gestorPublico;

import gestorPublico.service.ColeccionService;
import gestorPublico.service.HechoService;
import gestorPublico.service.SolicitudService;
import gestorPublico.service.UsuarioService;
import io.javalin.Javalin;
import gestorPublico.controller.*;
import utils.IniciadorApp;
import utils.LecturaConfig;
import gestorPublico.repository.*;

import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoApiPublica"));
        int puertoDinamica = Integer.parseInt(config.getProperty("puertoDinamico"));

        String urlWeb = config.getProperty("urlWeb");
        String servidorSSO = config.getProperty("urlServidorSSO");

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();

        // Service
        HechoService hechoService = new HechoService(hechoRepositorio, puertoDinamica);
        ColeccionService coleccionService = new ColeccionService(coleccionRepositorio);
        SolicitudService solicitudService = new SolicitudService(puertoDinamica);
        UsuarioService usuarioService = new UsuarioService(usuarioRepositorio, servidorSSO);

        // Controller
        HechoController hechoController = new HechoController(hechoService);
        ColeccionController coleccionController = new ColeccionController(coleccionService);
        SolicitudController solicitudController = new SolicitudController(solicitudService);
        UsuarioController usuarioController = new UsuarioController(usuarioService);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        // Rutas Hechos
        app.get("/api/hechos", hechoController.obtenerHechos);
        app.get("/api/hechos/{id}", hechoController.obtenerHechoPorId);
        app.post("/api/hechos", hechoController.crearHecho);
        app.get("/api/categoria", hechoController.buscarCategorias);

        // Rutas Colecciones
        app.get("/api/colecciones", coleccionController.obtenerColecciones);
        app.get("/api/colecciones/{id}", coleccionController.obtenerColeccionPorId);
        app.get("/api/colecciones/{id}/hechos", coleccionController.obtenerHechosDeColeccion);

        // Rutas Solicitudes
        app.post("/api/solicitudEliminacion", solicitudController.crearSolicitudEliminacion);
        app.post("/api/solicitudModificacion", solicitudController.crearSolicitudModificacion);

        // Rutas de Usuario / Auth
        app.post("/api/login", usuarioController.login);
        app.post("/api/sign-in", usuarioController.registrar);
        app.get("/api/usuario/{username}", usuarioController.obtenerUsuario);
    }
}
