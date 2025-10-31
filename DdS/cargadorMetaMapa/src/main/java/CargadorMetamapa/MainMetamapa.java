package CargadorMetamapa;

import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;
import CargadorMetamapa.Presentacion.GetHechosHandler;
import CargadorMetamapa.Presentacion.PostFuentesHandler;
import utils.Conexiones.Cargador;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;

import java.util.Properties;

import utils.*;
import CargadorMetamapa.ConexionAgregador.ControladorMetamapa;


public class MainMetamapa {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // Carga de configuración
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        // Asumiendo que has agregado estos puertos a tu config.properties
        int puertoMetamapa = Integer.parseInt(config.getProperty("puertoMetamapa"));
        String urlMetamapa = config.getProperty("urlMetamapaExterna"); // URL de la otra instancia Metamapa
        String urlAgregador = config.getProperty("urlAgregador");

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
        app.get("/hechos", new GetHechosHandler(cargadorMetamapa));
        app.post("/fuentes", new PostFuentesHandler(cargadorMetamapa));
    }
}