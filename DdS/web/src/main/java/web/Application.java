package web;

import web.controller.*;
import io.javalin.Javalin;
import web.controller.loginKeycloak.AuthCallbackHandler;
import web.controller.loginKeycloak.GetLogOutHandler;
import web.controller.loginKeycloak.GetLoginHandler;
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
        String keycloakUrl = config.getProperty("URL_SERVIDOR_SSO");
        String clientId = config.getProperty("CLIENT_ID");
        String redirectUrl = config.getProperty("REDIRECT_URL");
        String clientSecret = config.getProperty("CLIENT_SECRET");
        String urlWeb = config.getProperty("URL_WEB");

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
        SolicitudService solicitudService = new SolicitudService(urlAdmin,urlPublica);
        UsuarioService usuarioService = new UsuarioService(urlPublica);
        FuenteService fuenteService = new FuenteService(urlPublica);

        // controllers
        ColeccionController coleccionController = new ColeccionController(coleccionService, usuarioService, fuenteService);
        HechoController hechoController = new HechoController(urlPublica, urlAdmin,hechoService, dataCloud);
        SolicitudController solicitudController = new SolicitudController(solicitudService, urlPublica, usuarioService, dataCloud);
        AdministradorController administradorController = new AdministradorController(solicitudService);

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();

            ctx.status(500);

            Map<String, Object> model = new HashMap<>();
            model.put("mensaje", e.getMessage());
            ctx.render("error.ftl", model);
        });

        app.error(404, ctx -> {
            ctx.render("no_encontrado.ftl");
        });

        app.get("/probar-error", ctx -> {
            throw new RuntimeException("Error de prueba para verificar estilos CSS");
        });

        app.get("/", ctx -> {
            ctx.redirect("/home");
        });
        app.get("/home", new GetHomeHandler(urlPublica));

        //login
        app.get("/login", new GetLoginHandler(keycloakUrl, clientId, redirectUrl));
        app.get("/auth/callback", new AuthCallbackHandler(usuarioService,clientSecret, keycloakUrl ,clientId, redirectUrl));
        app.get("/logout", new GetLogOutHandler(urlWeb, keycloakUrl));


        //app.post("/login", new PostLoginHandler(urlPublica));
        //app.get("/sign-in", new GetSignInHandler(urlPublica));


        //hechos
        app.get("/hechos/{id}", hechoController.obtenerHechoPorId); //hecho especifico
        app.get("/hechos", hechoController.listarHechos); //home con hechos
        app.get("/crear", hechoController.obtenerPageCrearHecho); // Para mostrar el formulario de creación

        //colecciones
        app.get("/colecciones", coleccionController.listarColecciones);
        app.get("/colecciones/{id}", coleccionController.obtenerColeccionPorId);
        app.get("/editar-coleccion/{id}", coleccionController.obtenerPageEditarColeccion);
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
        app.get("/api/solicitudes/{id}", solicitudController.obtenerFormsEliminarSolicitud);

        app.get("/hechos/{id}/modificar", solicitudController.obtenerFormsModificarSolicitud);
        app.post("/api/solicitar-modificacion", solicitudController.crearSolicitudModificacion);
        //app.get("/api/solicitudes/{id}", solicitudController.obtenerFormsModificarSolicitud);

        app.get("/admin/solicitudes/eliminacion", solicitudController.listarSolicitudesEliminacion);
        app.get("/admin/solicitudes/modificacion", solicitudController.listarSolicitudesModificacion);
        app.get("/admin/solicitudes/{tipo}/{id}", solicitudController.obtenerSolicitud);
        app.patch("/admin/solicitudes/{tipo}/{id}", solicitudController.actualizarEstadoSolicitud);

        app.get("/admin/crear-coleccion", coleccionController.obtenerPageCrearColeccion);

        app.get("/admin/panel", administradorController.obtenerPagePanel);
    }
}