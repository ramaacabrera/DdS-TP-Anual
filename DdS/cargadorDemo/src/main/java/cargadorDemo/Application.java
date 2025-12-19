package cargadorDemo;

import cargadorDemo.service.DemoLoaderService;
import cargadorDemo.domain.fuente.Fuente;
import cargadorDemo.domain.fuente.TipoDeFuente;
import cargadorDemo.conexionAgregador.ClienteDelAgregador;
import cargadorDemo.domain.Conexiones.Cargador;
import cargadorDemo.service.HechosService;
import cargadorDemo.utils.IniciadorApp;
import com.fasterxml.jackson.core.JsonProcessingException;
import cargadorDemo.controller.ControladorDemo;
import cargadorDemo.utils.LecturaConfig;


// IMPORT NUEVO
import io.javalin.Javalin;

import java.util.Properties;

public class Application {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        int puerto = Integer.parseInt(config.getProperty("PUERTO_DEMO"));

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        app.get("/health", ctx -> {ctx.status(200).result("OK");});

        System.out.println("Monitor de salud escuchando en puerto 8082");


        String urlMock = config.getProperty("URL_MOCK");
        String urlAgregador = config.getProperty("URL_AGREGADOR");

        Cargador cargadorDemo = new Cargador();
        cargadorDemo.agregarConexion(new DemoLoaderService(urlMock));

        Fuente fuente = new Fuente(TipoDeFuente.DEMO, "Demo");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDemo(new HechosService(cargadorDemo)));
        cliente.conectar(fuente);
    }
}