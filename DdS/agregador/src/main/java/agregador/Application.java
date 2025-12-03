package agregador;

import agregador.controller.*;
import agregador.service.*;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.repository.*;
import agregador.service.normalizacion.MockNormalizador;
import io.javalin.Javalin;
import agregador.utils.IniciadorApp;
import agregador.utils.LecturaConfig;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

public class Application {
    public static void main(String[] args) throws InterruptedException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puertoAgregador = Integer.parseInt(config.getProperty("PUERTO_AGREGADOR"));
        System.out.println("Iniciando servidor Agregador en el puerto "+puertoAgregador);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoAgregador, "/");

        // Repositorios
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();
        FuenteRepositorio fuenteRepositorio = new FuenteRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        ConexionCargadorRepositorio  conexionCargadorRepositorio = new ConexionCargadorRepositorio();

        // Services
        ConexionCargadorService conexionCargadorService = new ConexionCargadorService(fuenteRepositorio, conexionCargadorRepositorio);
        HechosCargadorService hechosCargadorService = new HechosCargadorService(fuenteRepositorio, conexionCargadorRepositorio);
        ServicioNormalizacion servNorm = new ServicioNormalizacion(new MockNormalizador());
        MotorConsenso motorConsenso = new MotorConsenso(coleccionRepositorio);
        GestorSolicitudes gestorSol = new GestorSolicitudes(solicitudEliminacionRepositorio, solicitudModificacionRepositorio, hechoRepositorio, usuarioRepositorio);

        AgregadorOrquestador agregador = new AgregadorOrquestador(hechoRepositorio, coleccionRepositorio, fuenteRepositorio,
                servNorm, motorConsenso, gestorSol, hechosCargadorService);
        AgregadorScheduler agregadorScheduler = new AgregadorScheduler(agregador);


        ExecutorService wsWorkers = java.util.concurrent.Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors()));

        app.ws("/cargador", ws -> {
            ws.onConnect(new OnConnectHandler(conexionCargadorService, fuenteRepositorio));
            ws.onMessage(ctx -> {
                String raw = ctx.message();
                wsWorkers.execute(() -> new OnMessageHandler(agregador).handleMessageSeguro(raw, ctx));
            });
            ws.onClose(new OnCloseHandler(conexionCargadorService, fuenteRepositorio));
        });
    }
}