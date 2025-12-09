package cargadorMetamapa;

import cargadorMetamapa.controller.MetamapaLoader;
import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;
import cargadorMetamapa.controller.GetHechosHandler;
import cargadorMetamapa.controller.PostFuentesHandler;
import utils.Conexiones.Cargador;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;

import java.util.Properties;

import utils.*;
import cargadorMetamapa.controller.ControladorMetamapa;


public class Application {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // Carga de configuración
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        // Asumiendo que has agregado estos puertos a tu config.properties
        int puertoMetamapa = Integer.parseInt(config.getProperty("PUERTO_METAMAPA"));
        String urlMetamapa = config.getProperty("URL_METAMAPA_EXTERNO"); // URL de la otra instancia Metamapa
        String urlAgregador = config.getProperty("URL_AGREGADOR");

        // 1. Inicialización del agregador.Cargador con la CONEXIÓN ESPECÍFICA
        Cargador cargadorMetamapa = new Cargador();
        cargadorMetamapa.agregarConexion(new MetamapaLoader(urlMetamapa));

        System.out.println("Iniciando Metamapa Loader en el puerto "+puertoMetamapa);

        // 2. Servidor Javalin
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoMetamapa, "/");

        // 3. Conexión al Agregador
        Fuente fuente = new Fuente(TipoDeFuente.METAMAPA, "METAMAPA");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorMetamapa(new GetHechosHandler(cargadorMetamapa)));
        cliente.conectar(fuente);



        // 4. Endpoints (usando el agregador.Cargador ESPECÍFICO)

        // Health check
        app.get("/health", ctx -> { ctx.status(200).result("OK");});

        app.get("/hechos", new GetHechosHandler(cargadorMetamapa));
        app.post("/fuentes", new PostFuentesHandler(cargadorMetamapa));
    }
}