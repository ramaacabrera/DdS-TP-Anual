package gestorPublico;

import gestorPublico.service.*;
import gestorPublico.service.Normalizador.DiccionarioCategorias;
import io.javalin.Javalin;
import gestorPublico.controller.*;
import utils.IniciadorApp;
import gestorPublico.utils.LecturaConfig;
import gestorPublico.repository.*;

import java.util.Properties;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("PUERTO_GESTOR_PUBLICO"));
        String urlDinamica = config.getProperty("URL_DINAMICA");
        String servidorSSO = config.getProperty("URL_KEYCLOAK");

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        FuenteRepositorio fuenteRepositorio = new FuenteRepositorio();

        // Service
        DiccionarioCategorias diccionarioCategorias  = new DiccionarioCategorias();
        HechoService hechoService = new HechoService(hechoRepositorio, diccionarioCategorias, urlDinamica);
        ColeccionService coleccionService = new ColeccionService(coleccionRepositorio);
        SolicitudService solicitudService = new SolicitudService(urlDinamica);
        UsuarioService usuarioService = new UsuarioService(usuarioRepositorio, servidorSSO);
        FuenteService fuenteService = new FuenteService(fuenteRepositorio);



        // Controller
        HechoController hechoController = new HechoController(hechoService, fuenteService);
        ColeccionController coleccionController = new ColeccionController(coleccionService);
        SolicitudController solicitudController = new SolicitudController(solicitudService);
        UsuarioController usuarioController = new UsuarioController(usuarioService);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        app.get("/health", ctx -> { ctx.status(200).result("OK");});

        // Rutas Hechos
        app.get("/api/hechos", hechoController.obtenerHechos);
        app.get("/api/hechos/{id}", hechoController.obtenerHechoPorId);
        app.post("/api/hechos", hechoController.crearHecho);
        app.get("/api/categoria", hechoController.buscarCategorias);
        app.get("/api/fuentes", hechoController.obtenerTodasLasFuentes);
        app.patch("/api/hecho/{id}", hechoController.modificarHecho);

        // Rutas Colecciones
        app.get("/api/colecciones", coleccionController.obtenerColecciones);
        app.get("/api/colecciones/{id}", coleccionController.obtenerColeccionPorId);
        app.get("/api/colecciones/{id}/hechos", coleccionController.obtenerHechosDeColeccion);

        // Rutas Solicitudes
        app.post("/api/solicitudEliminacion", solicitudController.crearSolicitudEliminacion);
        app.post("/api/solicitudModificacion", solicitudController.crearSolicitudModificacion);

        // Rutas de Usuario / Auth
        app.post("/api/usuario/sincronizar", usuarioController.sincronizar);
        app.get("/api/usuario/{username}", usuarioController.obtenerUsuario);

        //app.post("/api/sign-in", usuarioController.registrar);
        //app.get("/api/usuario/{username}", usuarioController.obtenerUsuario);
    }
}
