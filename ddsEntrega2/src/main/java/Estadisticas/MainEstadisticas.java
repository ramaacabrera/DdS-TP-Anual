package Estadisticas;

import Estadisticas.Persistencia.EstadisticasColeccionRepositorio;
import Estadisticas.Persistencia.EstadisticasRepositorio;
import Estadisticas.Presentacion.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;


public class MainEstadisticas {
    public static void main(String[] args){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto =  Integer.parseInt(config.getProperty("puertoEstadisticas"));
        int puertoAgregador = Integer.parseInt(config.getProperty("puertoAgregador"));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db-estadisticas");
        EntityManager em = emf.createEntityManager();

        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

        // Ver forma de obtener la ruta del agregador

        String url = "http://localhost:"+puertoAgregador;

        EstadisticasRepositorio estadisticasRepositorio = new EstadisticasRepositorio(em);
        EstadisticasColeccionRepositorio estadisticasColeccionRepositorio = new EstadisticasColeccionRepositorio(em);
        EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorio = new EstadisticasCategoriaRepositorio(em);

        GeneradorEstadisticas generador = new GeneradorEstadisticas(url);

        app.get("/api/estadisticas/provinciaMax/colecciones", new GetProvinciaColeccionHandler(estadisticasColeccionRepositorio));
        app.get("/api/estadisticas/categoriaMax", new GetCategoriaMaxHandler(estadisticasRepositorio));
        app.get("/api/estadisticas/provinciaMax/categorias", new GetProvinciaCategoriaHandler(estadisticasCategoriaRepositorio));
        app.get("/api/estadisticas/horaMax/categorias", new GetHoraMaxCategoriaHandler(estadisticasCategoriaRepositorio));
        app.get("/api/estadisticas/solicitudesSpam", new GetSolicitudesSpamHandler(estadisticasRepositorio));




    }
}
