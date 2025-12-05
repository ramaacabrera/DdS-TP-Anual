package web;

import web.controller.*;
import io.javalin.Javalin;
import web.service.*;
import utils.IniciadorApp;
import utils.LecturaConfig;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        String puerto = config.getProperty("PUERTO_WEB");
        System.out.println("Iniciando servidor Web en el puerto "+puerto);

        String urlPublica = config.getProperty("URL_PUBLICA");
        String urlAdmin = config.getProperty("URL_ADMINISTRATIVO");
        String urlEstadisticas = config.getProperty("URL_ESTADISTICAS");
        String cloudinaryUrl = config.getProperty("CLOUDINARY_URL");
        String cloudinaryPreset = config.getProperty("CLOUDINARY_PRESET");


        Map<String, Object> dataCloud = new HashMap<String, Object>();
        dataCloud.put("cloudinaryUrl", cloudinaryUrl);
        dataCloud.put("cloudinaryPreset", cloudinaryPreset);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarAppWeb(Integer.parseInt(puerto), "/");

        // services
        ColeccionService coleccionService = new ColeccionService(urlPublica, urlAdmin);
        HechoService hechoService = new HechoService(urlPublica);
        EstadisticasService estadisticasService = new EstadisticasService(urlEstadisticas);
        CategoriasService categoriasService = new CategoriasService(urlEstadisticas);
        SolicitudService solicitudService = new SolicitudService(urlAdmin);
        UsuarioService usuarioService = new UsuarioService(urlPublica);
        FuenteService fuenteService = new FuenteService(urlPublica);

        // controllers
        ColeccionController coleccionController = new ColeccionController(coleccionService, usuarioService, fuenteService);
        HechoController hechoController = new HechoController(urlPublica, hechoService, dataCloud);
        SolicitudController solicitudController = new SolicitudController(solicitudService);

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
        app.get("/hechos/{id}", hechoController.obtenerHechoPorId); //hecho especifico
        app.get("/hechos", hechoController.listarHechos); //home con hechos
        app.get("/crear", hechoController.obtenerPageCrearHecho); // Para mostrar el formulario de creación

        //colecciones
        app.get("/colecciones", coleccionController.listarColecciones);
        app.get("/colecciones/{id}", coleccionController.obtenerColeccionPorId);
        app.get("/editar-coleccion/{id}", coleccionController.obtenerPageEditarColeccion);
        app.get("/crear-coleccion", coleccionController.obtenerPageCrearColeccion);
        app.post("/colecciones", coleccionController.crearColeccion);
        app.put("/colecciones/{id}", coleccionController.editarColeccion);
        app.delete("/colecciones/{id}", coleccionController.eliminarColeccion);
        //app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(urlPublica));


        // Falta organizar el tema de categorias y colecciones, y aplicar los estilos
        app.get("/estadisticas", new GetEstadisticasHandler(estadisticasService, categoriasService));
        app.get("/estadisticas/descargar", new GetDescargarEstadisticasHandler(estadisticasService));
        app.get("/api/estadisticas/provinciaMax/categorias/{categoria}", new GetBusquedaAPIHandler(categoriasService, GetBusquedaAPIHandler.TipoBusqueda.PROVINCIA));
        app.get("/api/estadisticas/horaMax/categorias/{categoria}", new GetBusquedaAPIHandler(categoriasService, GetBusquedaAPIHandler.TipoBusqueda.HORA));

        //solicitudes

        app.get("/hechos/{id}/eliminar", solicitudController.obtenerFormsEliminarSolicitud);
        //app.get("/api/solicitudes", new GetSolicitudesEliminacionHandler(urlAdmin));
        app.get("/api/solicitudes/{id}", solicitudController.obtenerFormsEliminarSolicitud);

        app.get("/admin/solicitudes", solicitudController.listarSolicitudes);
        app.get("/admin/solicitudes/{tipo}/{id}", solicitudController.obtenerSolicitud);
        app.patch("/admin/solicitudes/{tipo}/{id}", solicitudController.actualizarEstadoSolicitud);


    }
}