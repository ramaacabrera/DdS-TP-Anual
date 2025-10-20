package CargadorDemo;

import Agregador.fuente.TipoDeFuente;
import utils.Conexiones.Cargador;
import CargadorMetamapa.Presentacion.GetHechosHandler;
import CargadorMetamapa.Presentacion.PostFuentesHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import utils.ConexionAlAgregador;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class MainDemo {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // Codigo para conectarse con el agregador

        LecturaConfig lector = new LecturaConfig();

        Properties config = lector.leerConfig();
        int puertoDemo = Integer.parseInt(config.getProperty("puertoDemo"));
        String urlMock = config.getProperty("urlMock");

        Cargador cargadorDemo = new Cargador();
        cargadorDemo.agregarConexion(new DemoLoader(urlMock));

        System.out.println("Iniciando servidor Componente Proxy en el puerto "+puertoDemo);

        //Javalin
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoDemo, "/");

        ConexionAlAgregador agregador = new ConexionAlAgregador();
        agregador.conectarse(TipoDeFuente.DEMO, config.getProperty("puertoDemo"));

        app.get("/hechos", new GetHechosHandler(cargadorDemo));
        app.post("/fuentes", new PostFuentesHandler(cargadorDemo));
    }
}

