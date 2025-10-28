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

        // Probar
        app.get("/api/hechos/{id}", new GetHechoEspecificoHandler(urlPublica));

        // Falta
        app.get("/api/hechos", new GetHechosHandler(urlPublica)); //home

        // Falta
        //app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(urlPublica));

        app.get("/crear", new GetCrearHechoHandler(urlPublica)); // Para mostrar el formulario de creación

        // Falta
        app.get("/hechos/{id}/eliminar", new GetSolicitudEliminacionHandler(urlPublica));

        // Falta
        //app.get("/api/colecciones", new GetColeccionesHandler(urlPublica));

        // Falta
        //app.get("/api/colecciones/{id}", new GetColeccionHandler(urlAdmin));

        // Falta
        //app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(urlAdmin));

        // Falta
        //app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(urlAdmin));

        //crear y editar colecciones
        //app.get("/editar-coleccion/{id}", new GetEditarColeccionHandler(urlAdmin));

        //app.get("/crear-coleccion", new GetCrearColeccionHandler()); //para poder ver el formulario
//se recibe un post en  api/colecciones, crea la coleccion y la mete en la base

        // Incompleto
        app.get("/api/estadisticas", new GetEstadisticasHandler(puertoEstadisticas));

    }
}