package estadisticas;

import estadisticas.agregador.ConexionAgregador;
import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import estadisticas.agregador.EstadisticasColeccionRepositorio;
import estadisticas.agregador.EstadisticasRepositorio;
import estadisticas.Presentacion.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;


public class MainEstadisticas {
    public static void main(String[] args){
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto =  Integer.parseInt(config.getProperty("puertoEstadisticas"));

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("estadisticas-PU");
        //EntityManager em = emf.createEntityManager();


        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puerto, "/");

//        EstadisticasRepositorio estadisticasRepositorio = new EstadisticasRepositorio(em);
//        EstadisticasColeccionRepositorio estadisticasColeccionRepositorio = new EstadisticasColeccionRepositorio(em);
//        EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorio = new EstadisticasCategoriaRepositorio(em);

        EstadisticasRepositorio estadisticasRepositorio = new EstadisticasRepositorio(emf);
        EstadisticasColeccionRepositorio estadisticasColeccionRepositorio = new EstadisticasColeccionRepositorio(emf);
        EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorio = new EstadisticasCategoriaRepositorio(emf);

        //EntityManagerFactory emfAgregador = Persistence.createEntityManagerFactory("agregador-PU");
        //EntityManager emAgregador = emfAgregador.createEntityManager();
        
        ConexionAgregador conexionAgregador = new ConexionAgregador();
        GeneradorEstadisticas generador = new GeneradorEstadisticas(conexionAgregador,estadisticasRepositorio,estadisticasCategoriaRepositorio,estadisticasColeccionRepositorio);

        app.get("/api/estadisticas/provinciaMax/colecciones/{coleccion}", new GetProvinciaColeccionHandler(estadisticasColeccionRepositorio));
        app.get("/api/estadisticas/categoriaMax", new GetCategoriaMaxHandler(estadisticasRepositorio));
        app.get("/api/estadisticas/provinciaMax/categorias/{categoria}", new GetProvinciaCategoriaHandler(estadisticasCategoriaRepositorio));
        app.get("/api/estadisticas/horaMax/categorias/{categoria}", new GetHoraMaxCategoriaHandler(estadisticasCategoriaRepositorio));
        app.get("/api/estadisticas/solicitudesSpam", new GetSolicitudesSpamHandler(estadisticasRepositorio));




    }
}
