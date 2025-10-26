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

        String puertoApiPublica = config.getProperty("puertoApiPublica");
        String puertoApiAdmin = config.getProperty("puertoApiAdmin");
        //si hacemos la pag de las estadisticas
        String puertoEstadisticas = config.getProperty("puertoEstadisticas");

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(Integer.parseInt(puerto), "/");

        // Probar
        app.get("/api/hechos/{id}", new GetHechoEspecificoHandler(puertoApiPublica));

        // Falta
        app.get("/api/hechos", new GetHechosHandler(puertoApiPublica)); //home

        // Falta
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(puertoApiPublica));

        // Falta
        app.get("/crear", new GetCrearHechoHandler(puertoApiPublica)); // Para mostrar el formulario de creación

        // Falta
        app.get("/hecho/{id}/request-delete", new GetSolicitudEliminacionHandler(puertoApiPublica));

        // Falta
        app.get("/api/colecciones", new GetColeccionesHandler(puertoApiPublica));

        // Falta
        app.get("/api/colecciones/{id}", new GetColeccionHandler(puertoApiAdmin));

        // Falta
        app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(puertoApiAdmin));

        // Falta
        app.get("/api/solicitudes/{id}", new GetSolicitudEliminacionHandler(puertoApiAdmin));

        //crear y editar colecciones
        app.get("/editar-coleccion/{id}", new GetEditarColeccionHandler(puertoApiAdmin));

        app.get("/crear-coleccion", new GetCrearColeccionHandler()); //para poder ver el formulario
//se recibe un post en  api/colecciones, crea la coleccion y la mete en la base

        // Incompleto
        app.get("/api/estadisticas", new GetEstadisticasHandler(puertoEstadisticas));

    }
}