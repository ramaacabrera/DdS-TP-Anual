package estadisticas;

import estadisticas.controller.EstadisticasController;
import estadisticas.domainEstadisticas.EstadisticasScheduler;
import estadisticas.repository.ConexionAgregador;
import estadisticas.repository.EstadisticasCategoriaRepositorio;
import estadisticas.repository.EstadisticasColeccionRepositorio;
import estadisticas.repository.EstadisticasRepositorio;
import estadisticas.service.EstadisticasService;
import estadisticas.service.GeneradorEstadisticas;
import estadisticas.utils.IniciadorApp;
import estadisticas.utils.LecturaConfig;
import io.javalin.Javalin;

import java.util.Properties;

public class Application {
    public static void main(String[] args){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("PUERTO_ESTADISTICAS"));

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        EstadisticasRepositorio estadisticasRepo = new EstadisticasRepositorio();
        EstadisticasColeccionRepositorio coleccionRepo = new EstadisticasColeccionRepositorio();
        EstadisticasCategoriaRepositorio categoriaRepo = new EstadisticasCategoriaRepositorio();
        ConexionAgregador conexionAgregador = new ConexionAgregador();

        EstadisticasService estadisticasService = new EstadisticasService(estadisticasRepo, categoriaRepo, coleccionRepo);

        GeneradorEstadisticas generador = new GeneradorEstadisticas(conexionAgregador);
        EstadisticasScheduler estadisticasScheduler = new EstadisticasScheduler(generador);

        EstadisticasController controller = new EstadisticasController(estadisticasService);

        app.get("/api/estadisticas/provinciaMax/colecciones/{coleccion}", controller::getProvinciaColeccion);
        app.get("/api/estadisticas/categoriaMax", controller::getCategoriaMax);
        app.get("/api/estadisticas/provinciaMax/categorias/{categoria}", controller::getProvinciaCategoria);
        app.get("/api/estadisticas/horaMax/categorias/{categoria}", controller::getHoraMaxCategoria);
        app.get("/api/estadisticas/solicitudesSpam", controller::getSolicitudesSpam);
        app.get("/api/estadisticas/categorias", controller::getCategorias);

        app.get("/api/estadisticas/exportar", controller::exportarCSV);

        System.out.println("Servidor de Estadísticas iniciado en puerto " + puerto);
    }
}