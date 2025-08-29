package CargadorProxy;

import io.javalin.Javalin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import Agregador.fuente.Fuente;
import java.util.List;

public class MainProxy {

    public static void main(String[] args) throws InterruptedException {
        // Codigo para conectarse con el agregador

        Cargador cargador = new Cargador();

        Properties config = new Properties();
        try{
            InputStream input = MainProxy.class.getClassLoader()
                    .getResourceAsStream("componeneteProxy.properties");
            if(input == null){
                throw new FileNotFoundException("No se encontro el archivo de propiedades");
            }
            config.load(input);
        } catch (IOException e){
            System.out.println("Error al leer el archivo de propiedades");
            System.err.println(e.getMessage());
        }
        int puerto = Integer.parseInt(config.getProperty("puerto"));

        System.out.println("Iniciando servidor Componente Proxy en el puerto 8085");
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        javalinConfig.staticFiles.add("/");
        }).start(8085);

        app.get("/hechos", new GetHechosProxyHandler(cargador));
        app.post("/fuentes", new PostFuentesProxyHandler(cargador));
    }
}
