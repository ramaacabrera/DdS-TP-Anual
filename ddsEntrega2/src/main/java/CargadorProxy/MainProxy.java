package CargadorProxy;

import CargadorProxy.APIMock.DemoAPIMockServer;
import io.javalin.Javalin;

import java.util.Properties;

import utils.*;

public class MainProxy {

    public static void main(String[] args) throws InterruptedException {
        // Codigo para conectarse con el agregador
        new Thread(() -> DemoAPIMockServer.main(new String[]{})).start();
        Thread.sleep(2000); //le doy tiempo a que se levante

        Cargador cargador = new Cargador();
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoProxy"));

        System.out.println("Iniciando servidor Componente Proxy en el puerto "+puerto);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        app.get("/hechos", new GetHechosProxyHandler(cargador));
        app.post("/fuentes", new PostFuentesProxyHandler(cargador));
    }
}
