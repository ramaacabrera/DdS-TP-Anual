package agregador;

import agregador.PaqueteAgregador.AgregadorScheduler;
import utils.Persistencia.*;
import utils.PaqueteNormalizador.MockNormalizador;
import agregador.PaqueteAgregador.Agregador;
import agregador.Cargador.ConexionCargador;
import agregador.Handlers.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

public class MainAgregador {
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

        MockNormalizador mockNormalizador = new MockNormalizador();
        ConexionCargador conexionCargador = new ConexionCargador(fuenteRepositorio);

        Agregador agregador = new Agregador(
                hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio,
                solicitudModificacionRepositorio, fuenteRepositorio, mockNormalizador, conexionCargador);
        AgregadorScheduler agregadorScheduler = new AgregadorScheduler(agregador);


        ExecutorService wsWorkers = java.util.concurrent.Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors()));

        app.ws("/cargador", ws -> {
            ws.onConnect(new OnConnectHandler(conexionCargador, fuenteRepositorio));
            ws.onMessage(ctx -> {
                String raw = ctx.message();
                wsWorkers.execute(() -> new OnMessageHandler(agregador).handleMessageSeguro(raw, ctx));
            });
            ws.onClose(new OnCloseHandler(conexionCargador, fuenteRepositorio));
        });
    }
}