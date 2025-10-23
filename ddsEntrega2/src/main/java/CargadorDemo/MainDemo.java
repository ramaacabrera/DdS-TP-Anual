package CargadorDemo;

import Agregador.fuente.Fuente;
import Agregador.fuente.TipoDeFuente;
import utils.ClienteDelAgregador;
import utils.Conexiones.Cargador;
import CargadorMetamapa.Presentacion.GetHechosHandler;
import CargadorMetamapa.Presentacion.PostFuentesHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import utils.ConexionAlAgregador;
import utils.Controladores.ControladorDemo;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class MainDemo {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        String urlMock = config.getProperty("urlMock");
        String urlAgregador = config.getProperty("urlAgregador");

        Cargador cargadorDemo = new Cargador();
        cargadorDemo.agregarConexion(new DemoLoader(urlMock));

        Fuente fuente = new Fuente(TipoDeFuente.DEMO, "");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDemo(new GetHechosHandler(cargadorDemo)));
        cliente.conectar(fuente);
    }
}

