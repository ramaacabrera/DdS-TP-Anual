package CargadorMetamapa;

import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import CargadorDinamica.Presentacion.GetSolicitudesEliminacionHandler;
import CargadorMetamapa.Presentacion.GetHechosHandler;
import CargadorMetamapa.Presentacion.PostFuentesHandler;
import utils.Conexiones.Cargador;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;

import java.util.Properties;

import utils.*;
import utils.Controladores.ControladorMetamapa;


public class MainMetamapa {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // Carga de configuración
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();

        // Asumiendo que has agregado estos puertos a tu config.properties
        int puertoMetamapa = Integer.parseInt(config.getProperty("puertoMetamapa"));
        String urlMetamapa = config.getProperty("urlMetamapaExterna"); // URL de la otra instancia Metamapa
        String urlAgregador = config.getProperty("urlAgregador");

        // 1. Inicialización del Cargador con la CONEXIÓN ESPECÍFICA
        Cargador cargadorMetamapa = new Cargador();
        cargadorMetamapa.agregarConexion(new MetamapaLoader(urlMetamapa));

        System.out.println("Iniciando Metamapa Loader en el puerto "+puertoMetamapa);

        // 2. Servidor Javalin
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoMetamapa, "/");

        // 3. Conexión al Agregador
        Fuente fuente = new Fuente(TipoDeFuente.METAMAPA, "");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorMetamapa(new GetHechosHandler(cargadorMetamapa)));
        cliente.conectar(fuente);



        // 4. Endpoints (usando el Cargador ESPECÍFICO)
        app.get("/hechos", new GetHechosHandler(cargadorMetamapa));
        app.post("/fuentes", new PostFuentesHandler(cargadorMetamapa));
    }
}