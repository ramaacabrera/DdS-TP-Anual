package CargadorProxy;

import Agregador.fuente.TipoDeFuente;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;

import java.util.Properties;

import utils.*;

public class MainProxy {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        // Codigo para conectarse con el agregador
        Cargador cargador = new Cargador();
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoProxy"));

        // Lee la url del mock del archivo de configuración
        String urlMock = config.getProperty("urlMock");
        cargador.agregarConexion(new ConexionDemo(urlMock));

        System.out.println("Iniciando servidor Componente Proxy en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        ConexionAlAgregador agregador = new ConexionAlAgregador();
        agregador.conectarse(TipoDeFuente.PROXY, config.getProperty("puertoProxy"));

        app.get("/hechos", new GetHechosProxyHandler(cargador));
        app.post("/fuentes", new PostFuentesProxyHandler(cargador));
    }
}
