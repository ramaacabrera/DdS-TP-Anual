package web;

import web.controller.*;
import io.javalin.Javalin;
import web.service.*;
import utils.IniciadorApp;
import utils.LecturaConfig;


import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        String puerto = config.getProperty("puertoWeb");
        System.out.println("Iniciando servidor Web en el puerto "+puerto);

        String urlPublica = config.getProperty("urlPublica");
        String urlAdmin = config.getProperty("urlAdmin");
        //si hacemos la pag de las estadisticas
        String urlEstadisticas = config.getProperty("urlEstadisticas");

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarAppWeb(Integer.parseInt(puerto), "/");

        // services

        ColeccionService coleccionService = new ColeccionService(urlPublica, urlAdmin);
        HechoService hechoService = new HechoService(urlPublica);
        EstadisticasService estadisticasService = new EstadisticasService(urlEstadisticas);
        CategoriasService categoriasService = new CategoriasService(urlEstadisticas);
        SolicitudService solicitudService = new SolicitudService(urlAdmin);

        // controllers

        ColeccionController coleccionController = new ColeccionController(coleccionService);

        app.get("/", ctx -> {
            ctx.redirect("/home");
        });

        app.get("/home", new GetHomeHandler(urlPublica));

        //login
        app.get("/login", new GetLoginHandler(urlPublica));
        app.post("/login", new PostLoginHandler(urlPublica));

        app.get("/sign-in", new GetSignInHandler(urlPublica));

        app.get("/logout", new GetLogOutHandler());

        //hechos
        app.get("/hechos/{id}", new GetHechoEspecificoHandler(hechoService)); //hecho especifico
        app.get("/hechos", new GetHechosHandler(hechoService)); //home con hechos
        app.get("/crear", new GetCrearHechoHandler(urlPublica)); // Para mostrar el formulario de creación

        // Falta
        app.get("/hechos/{id}/eliminar", new GetSolicitudEliminacionHandler(urlPublica));
        // Falta
        //app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(urlAdmin));
        // Falta
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(urlPublica));

        app.get("/colecciones", coleccionController.listarColecciones);
        app.get("/colecciones/{id}", coleccionController.obtenerColeccionPorId);
        app.get("/editar-coleccion/{id}", coleccionController.obtenerPageEditarColeccion);
        app.get("/crear-coleccion", coleccionController.obtenerPageCrearColeccion);
        app.post("/colecciones", coleccionController.crearColeccion);
        //app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(urlPublica));

        // Falta organizar el tema de categorias y colecciones, y aplicar los estilos
        app.get("/estadisticas", new GetEstadisticasHandler(estadisticasService, categoriasService));

        app.get("/admin/solicitudes", new GetSolicitudesAdminHandler(solicitudService));
        app.get("/admin/solicitudes/{tipo}/{id}", new GetSolicitudAdminHandler(solicitudService));
        app.patch("/admin/solicitudes/{tipo}/{id}", new PatchSolicitudEstadoHandler(urlAdmin));


    }
}