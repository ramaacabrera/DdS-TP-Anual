package cargadorDemo;

import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;
import utils.ClienteDelAgregador;
import utils.Conexiones.Cargador;
import cargadorDemo.controller.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import cargadorDemo.ConexionAgregador.ControladorDemo;
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

        Fuente fuente = new Fuente(TipoDeFuente.DEMO, "Demo");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDemo(new GetHechosHandler(cargadorDemo)));
        cliente.conectar(fuente);
    }
}

