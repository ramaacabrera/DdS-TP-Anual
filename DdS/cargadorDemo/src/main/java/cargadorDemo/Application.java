package cargadorDemo;

import cargadorDemo.service.DemoLoaderService;
import cargadorDemo.service.GetHechosService;
import cargadorDemo.domain.fuente.Fuente;
import cargadorDemo.domain.fuente.TipoDeFuente;
import cargadorDemo.conexionAgregador.ClienteDelAgregador;
import cargadorDemo.domain.Conexiones.Cargador;
import com.fasterxml.jackson.core.JsonProcessingException;
import cargadorDemo.controller.ControladorDemo;
import utils.LecturaConfig;

import java.util.Properties;

public class Application {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        String urlMock = config.getProperty("URL_MOCK");
        String urlAgregador = config.getProperty("URL_AGREGADOR");

        Cargador cargadorDemo = new Cargador();
        cargadorDemo.agregarConexion(new DemoLoaderService(urlMock));

        Fuente fuente = new Fuente(TipoDeFuente.DEMO, "Demo");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDemo(new GetHechosService(cargadorDemo)));
        cliente.conectar(fuente);
    }
}

