import service.DemoLoaderService;
import service.GetHechosService;
import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;
import utils.ClienteDelAgregador;
import utils.Conexiones.Cargador;
import com.fasterxml.jackson.core.JsonProcessingException;
import controller.ControladorDemo;
import utils.LecturaConfig;

import java.util.Properties;

public class Application {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        String urlMock = config.getProperty("urlMock");
        String urlAgregador = config.getProperty("urlAgregador");

        Cargador cargadorDemo = new Cargador();
        cargadorDemo.agregarConexion(new DemoLoaderService(urlMock));

        Fuente fuente = new Fuente(TipoDeFuente.DEMO, "Demo");
        ClienteDelAgregador cliente = new ClienteDelAgregador(urlAgregador, new ControladorDemo(new GetHechosService(cargadorDemo)));
        cliente.conectar(fuente);
    }
}

