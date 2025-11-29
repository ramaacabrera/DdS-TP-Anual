package agregador;

import agregador.controller.OnCloseHandler;
import agregador.controller.OnConnectHandler;
import agregador.controller.OnMessageHandler;
import agregador.domain.Agregador.AgregadorScheduler;
import agregador.repository.ConexionCargadorRepositorio;
import agregador.service.HechosCargadorService;
//import utils.Persistencia.*;
import agregador.repository.*;
import agregador.domain.PaqueteNormalizador.MockNormalizador;
import agregador.domain.Agregador.Agregador;
import agregador.service.ConexionCargadorService;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

public class Application {
    public static void main(String[] args) throws InterruptedException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puertoAgregador = 8070;
        System.out.println("Iniciando servidor Agregador en el puerto "+puertoAgregador);

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoAgregador, "/");

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio(hechoRepositorio);
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio(hechoRepositorio);
        FuenteRepositorio fuenteRepositorio = new FuenteRepositorio();
        ConexionCargadorRepositorio  conexionCargadorRepositorio = new ConexionCargadorRepositorio();

        MockNormalizador mockNormalizador = new MockNormalizador();
        ConexionCargadorService conexionCargadorService = new ConexionCargadorService(fuenteRepositorio, conexionCargadorRepositorio);
        HechosCargadorService hechosCargadorService = new HechosCargadorService(fuenteRepositorio, conexionCargadorRepositorio);

        Agregador agregador = new Agregador(
                hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio,
                solicitudModificacionRepositorio, fuenteRepositorio, mockNormalizador, conexionCargadorService, hechosCargadorService);
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