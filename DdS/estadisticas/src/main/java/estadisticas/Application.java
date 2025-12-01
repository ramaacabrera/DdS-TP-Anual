package estadisticas;

import estadisticas.domain.EstadisticasScheduler;
import estadisticas.repository.ConexionAgregador;
import estadisticas.repository.EstadisticasCategoriaRepositorio;
import estadisticas.repository.EstadisticasColeccionRepositorio;
import estadisticas.repository.EstadisticasRepositorio;
import estadisticas.controller.*;
import estadisticas.service.GeneradorEstadisticas;
import io.javalin.Javalin;
import estadisticas.utils.IniciadorApp;
import estadisticas.utils.LecturaConfig;

import java.util.Properties;


public class Application {
    public static void main(String[] args){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto =  Integer.parseInt(config.getProperty("puertoEstadisticas"));

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        EstadisticasRepositorio estadisticasRepositorio = new EstadisticasRepositorio();
        EstadisticasColeccionRepositorio estadisticasColeccionRepositorio = new EstadisticasColeccionRepositorio();
        EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorio = new EstadisticasCategoriaRepositorio();
        
        ConexionAgregador conexionAgregador = new ConexionAgregador();
        GeneradorEstadisticas generador = new GeneradorEstadisticas(conexionAgregador);
        EstadisticasScheduler estadisticasScheduler = new EstadisticasScheduler(generador);

        app.get("/api/estadisticas/provinciaMax/colecciones/{coleccion}", new GetProvinciaColeccionHandler(estadisticasColeccionRepositorio));
        app.get("/api/estadisticas/categoriaMax", new GetCategoriaMaxHandler(estadisticasRepositorio));
        app.get("/api/estadisticas/provinciaMax/categorias/{categoria}", new GetProvinciaCategoriaHandler(estadisticasCategoriaRepositorio));
        app.get("/api/estadisticas/horaMax/categorias/{categoria}", new GetHoraMaxCategoriaHandler(estadisticasCategoriaRepositorio));
        app.get("/api/estadisticas/solicitudesSpam", new GetSolicitudesSpamHandler(estadisticasRepositorio));
        app.get("/api/estadisticas/categorias", new GetCategoriasHandler(estadisticasCategoriaRepositorio));


    }
}
