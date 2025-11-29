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

        app.get("/", ctx -> {
            ctx.redirect("/home");
        });
        app.get("/home", new GetHomeHandler(urlPublica));
        app.get("/login", new GetLoginHandler(urlPublica));
        app.post("/login", new PostLoginHandler(urlPublica));

        app.get("/sign-in", new GetSignInHandler(urlPublica));

        app.get("/logout", new GetLogOutHandler());

        app.get("/hechos/{id}", new GetHechoEspecificoHandler(urlPublica, hechoService)); //hecho especifico

        // Falta
        app.get("/hechos", new GetHechosHandler(hechoService)); //home con hechos

        // Falta
        //app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(urlPublica));

        app.get("/crear", new GetCrearHechoHandler(urlPublica)); // Para mostrar el formulario de creación

        // Falta
        app.get("/hechos/{id}/eliminar", new GetSolicitudEliminacionHandler(urlPublica));

        app.get("/colecciones", new GetColeccionesHandler(urlPublica, coleccionService));

        app.get("/colecciones/{id}", new GetColeccionHandler(urlPublica, coleccionService));

        // Falta
        //app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(urlAdmin));

        // Falta
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(urlPublica));

        app.get("/editar-coleccion/{id}", new GetEditarColeccionHandler(urlAdmin, coleccionService));

        app.get("/crear-coleccion", new GetCrearColeccionHandler(urlAdmin));

        app.post("/colecciones", new PostColeccionHandler(urlAdmin, coleccionService));

        // Falta organizar el tema de categorias y colecciones, y aplicar los estilos
        app.get("/estadisticas", new GetEstadisticasHandler(estadisticasService, categoriasService));

        app.get("/admin/solicitudes", new GetSolicitudesAdminHandler(solicitudService));
        app.get("/admin/solicitudes/{tipo}/{id}", new GetSolicitudAdminHandler(solicitudService));
        app.patch("/admin/solicitudes/{tipo}/{id}", new PatchSolicitudEstadoHandler(urlAdmin));


    }
}