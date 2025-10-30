package web;

import Presentacion.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;


import java.util.Properties;

public class MainWeb {
    public static void main(String[] args) {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        String puerto = config.getProperty("puertoWeb");
        System.out.println("Iniciando servidor Web en el puerto "+puerto);

        String urlPublica = config.getProperty("urlPublica");
        String urlAdmin = config.getProperty("urlAdmin");
        //si hacemos la pag de las estadisticas
        String puertoEstadisticas = config.getProperty("puertoEstadisticas");

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarAppWeb(Integer.parseInt(puerto), "/");

        app.get("/", ctx -> {
            ctx.redirect("/hechos");
        });
        app.get("/home", new GetHomeHandler(urlPublica));
        app.get("/login", new GetLoginHandler(urlPublica));
        app.post("/login", new PostLoginHandler(urlPublica));

        app.get("/sign-in", new GetSignInHandler(urlPublica));

        app.get("/logout", new GetLogOutHandler());

        app.get("/hechos/{id}", new GetHechoEspecificoHandler(urlPublica)); //hecho especifico

        // Falta
        app.get("/hechos", new GetHechosHandler(urlPublica)); //home con hechos

        // Falta
        //app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(urlPublica));

        app.get("/crear", new GetCrearHechoHandler(urlPublica)); // Para mostrar el formulario de creación

        // Falta
        app.get("/hechos/{id}/eliminar", new GetSolicitudEliminacionHandler(urlPublica));

        app.get("/colecciones", new GetColeccionesHandler(urlPublica));

        app.get("/colecciones/{id}", new GetColeccionHandler(urlPublica));

        // Falta
        //app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(urlAdmin));

        // Falta
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(urlAdmin));

        app.get("/editar-coleccion/{id}", new GetEditarColeccionHandler(urlAdmin));

        app.get("/crear-coleccion", new GetCrearColeccionHandler(urlAdmin));

        // Falta organizar el tema de categorias y colecciones, y aplicar los estilos
        app.get("/estadisticas", new GetEstadisticasHandler(puertoEstadisticas));

    }
}