

import Persistencia.*;
import io.javalin.Javalin;
import org.example.agregador.Agregador;
import org.example.agregador.fuente.Fuente;
import org.example.fuenteDinamica.ControllerSolicitud;
import org.example.fuenteDinamica.ControllerSubirHechos;
import org.example.fuenteEstatica.ConexionEstatica;
import org.example.fuenteEstatica.FuenteEstatica;
import org.example.fuenteProxy.FuenteDemo;
import presentacion.*;
import org.example.fuenteProxy.APIMock.DemoAPIMockServer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //inicio el mock server para demo
        //Para la fuente demo
        final String API_MOCK_URL = "http://localhost:7000/api/hechos";
        new Thread(() -> DemoAPIMockServer.main(new String[]{})).start();
        Thread.sleep(2000);

        System.out.println("Iniciando servidor Javalin en el puerto 8080...");
        Javalin app = Javalin.create(javalinConfig -> {
                            javalinConfig.plugins.enableCors(cors -> {
                                cors.add(it -> it.anyHost());
                            }); // para poder hacer requests de un dominio a otro
                            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
                        }).start(8080);

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        List<Fuente> fuentesDisponibles = new ArrayList<>();
        FuenteDemo fuenteDemo = new FuenteDemo(API_MOCK_URL);
        //FuenteMetapa fuenteMetaMapa= new FuenteMetaMapa();
        FuenteEstatica fuenteEstatica = new FuenteEstatica(new ConexionEstatica("desastres_tecnologicos_argentina.csv"));
        //FuenteDinamica fuenteDinamica = new FuenteDinamica();

        //FALTA INICIALIZAR ESAS 3 FUENTES!!!!!!!!!!!!!!!!

        fuentesDisponibles.add(fuenteDemo);
        //fuentesDisponibles.add(fuenteMetaMapa);
        fuentesDisponibles.add(fuenteEstatica);
        //fuentesDisponibles.add(fuenteDinamica);

        Agregador agregador = new Agregador(hechoRepositorio, coleccionRepositorio,fuentesDisponibles);
        agregador.buscarHechosIniciales();

        //Inicializacion de Controller
        ControllerSubirHechos controllerSubirHechos = new ControllerSubirHechos(dinamicoRepositorio);
        ControllerSolicitud controllerSolicitud = new ControllerSolicitud(dinamicoRepositorio);

        // API Administrativa
        app.post("/api/colecciones", new PostColeccionHandler(coleccionRepositorio)); //creo coleccion PROBADOOOO
        app.get("api/colecciones", new GetColeccionesHandler(coleccionRepositorio)); // consulta todas las colecciones PROBADOOOO
        app.get("api/colecciones/{id}", new GetColeccionHandler(coleccionRepositorio)); // lee una coleccion en particular PROBADOOOOOO
        app.put("api/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio)); // actualizo una coleccion PROBADOOOOOO
        app.delete("api/colecciones/{id}", new DeleteColeccionesHandler(coleccionRepositorio)); // borro una coleccion probadoooooo

        app.post("api/colecciones/{id}/fuente", new PostFuentesColeccionHandler(coleccionRepositorio)); // agrego fuentes probbadisimooooo
        app.delete("api/colecciones/{id}/fuente", new DeleteFuenteHandler(coleccionRepositorio)); // borro una fuente

        app.put("api/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio)); // actualizo algoritmo probado

        app.put("api/solicitudes/{id}", new PutSolicitudEliminacionHandler(controllerSolicitud)); // aprobar o denegar solicitud eliminacion
        app.get("api/solicitudes", new GetSolicitudesEliminacionHandler(solicitudEliminacionRepositorio)); // consulta todas las solicitudes
        app.get("api/solicitudes/{id}", new GetSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // consulta una solicitud por id

        // API publica
        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio)); // consulto todos los hechos PROBADOOOOO
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler(coleccionRepositorio)); //consulta hechos de una coleccion (pudiendo mandar criterios o no)
        app.post("/api/hechos", new PostHechoHandler(controllerSubirHechos)); //creo hecho PROBADOOOOOOOO
        app.post("/api/solicitudes", new PostSolicitudEliminacionHandler(controllerSolicitud)); //creo solicitud PROBADOOOOOOO
    }
}
