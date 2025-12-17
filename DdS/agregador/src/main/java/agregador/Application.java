package agregador;

import agregador.controller.*;
import agregador.graphQl.GraphQLProvider;
import agregador.service.*;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.*;
import agregador.service.normalizacion.DiccionarioCategorias;
import agregador.service.normalizacion.MockNormalizador;
import agregador.service.normalizacion.ServicioNormalizacion;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import io.javalin.Javalin;
import agregador.utils.IniciadorApp;
import agregador.utils.LecturaConfig;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

public class Application {
    public static void main(String[] args) throws InterruptedException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        String puertoStr = config.getProperty("PUERTO_AGREGADOR", "8080");
        int tiempoScheduler = Integer.parseInt(config.getProperty("TIEMPO_SCHEDULER"));
        int puertoAgregador;
        try {
            puertoAgregador = Integer.parseInt(puertoStr);
        } catch (NumberFormatException e) {
            System.err.println("El puerto configurado no es válido. Usando 8080 por defecto.");
            puertoAgregador = 8080;
        }

        System.out.println("Iniciando servidor Agregador en el puerto " + puertoAgregador);

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();
        FuenteRepositorio fuenteRepositorio = new FuenteRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        ConexionCargadorRepositorio conexionCargadorRepositorio = new ConexionCargadorRepositorio();

        DiccionarioCategorias diccionarioCategorias = new DiccionarioCategorias();

        // Services
        ConexionCargadorService conexionCargadorService = new ConexionCargadorService(fuenteRepositorio, conexionCargadorRepositorio);
        HechosCargadorService hechosCargadorService = new HechosCargadorService(fuenteRepositorio, conexionCargadorRepositorio);
        ServicioNormalizacion servNorm = new ServicioNormalizacion(new MockNormalizador(diccionarioCategorias));
        MotorConsenso motorConsenso = new MotorConsenso(coleccionRepositorio);
        GestorSolicitudes gestorSol = new GestorSolicitudes(solicitudEliminacionRepositorio, solicitudModificacionRepositorio, hechoRepositorio, usuarioRepositorio);

        AgregadorOrquestador agregador = new AgregadorOrquestador(hechoRepositorio, coleccionRepositorio, fuenteRepositorio,
                servNorm, motorConsenso, gestorSol, hechosCargadorService);

        AgregadorScheduler agregadorScheduler = new AgregadorScheduler(agregador, tiempoScheduler);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoAgregador, "/");

        ExecutorService wsWorkers = java.util.concurrent.Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors()));

        // --- RUTAS ---
        app.get("/health", ctx -> {
            ctx.status(200).result("OK");
        });

        app.ws("/cargador", ws -> {
            ws.onConnect(new OnConnectHandler(conexionCargadorService));
            ws.onMessage(ctx -> {
                String raw = ctx.message();
                wsWorkers.execute(() -> new OnMessageHandler(agregador).handleMessageSeguro(raw, ctx));
            });
            ws.onClose(new OnCloseHandler(conexionCargadorService, fuenteRepositorio));
        });

        // GraphQl
        GraphQLProvider graphQLProvider = new GraphQLProvider();

        app.post("/graphql", ctx -> {
            Map<String, Object> request = ctx.bodyAsClass(Map.class);

            String query = (String) request.get("query");
            Map<String, Object> variables =
                    (Map<String, Object>) request.getOrDefault("variables", Map.of());

            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(variables)
                    .build();

            ExecutionResult executionResult =
                    graphQLProvider.graphQL().execute(executionInput);

            ctx.json(executionResult.toSpecification());
        });

        app.get("/graphql-explorer.html", ctx -> {
            InputStream is = Application.class.getResourceAsStream("/public/graphql-explorer.html");
            if (is != null) {
                ctx.contentType("text/html");
                ctx.result(is);
            } else {
                ctx.status(404).result("Archivo no encontrado");
            }
        });

        app.get("/css/style.css", ctx -> {
            InputStream is = Application.class.getResourceAsStream("/public/css/style.css");
            if (is != null) {
                ctx.contentType("text/css");
                ctx.result(is);
            } else {
                ctx.status(404).result("CSS no encontrado");
            }
        });

        app.get("/js/app.js", ctx -> {
            InputStream is = Application.class.getResourceAsStream("/public/js/app.js");
            if (is != null) {
                ctx.contentType("application/javascript");
                ctx.result(is);
            } else {
                ctx.status(404).result("JavaScript no encontrado");
            }
        });

        // También agrega redirecciones
        app.get("/", ctx -> ctx.redirect("/graphql-explorer.html"));
        app.get("/graphql-explorer", ctx -> ctx.redirect("/graphql-explorer.html"));

    }
}