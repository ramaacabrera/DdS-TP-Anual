

import Persistencia.*;
import io.javalin.Javalin;
import org.example.agregador.Agregador;
import org.example.agregador.fuente.Fuente;
import org.example.fuenteDinamica.FuenteDinamica;
import org.example.fuenteEstatica.ConexionEstatica;
import org.example.fuenteEstatica.FuenteEstatica;
import org.example.fuenteProxy.ConexionDemo;
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

        // API Administrativa
        app.post("/api/colecciones", new PostColeccionHandler(coleccionRepositorio)); //creo coleccion
        app.get("api/colecciones", new GetColeccionesHandler(coleccionRepositorio)); // consulta todas las colecciones
        app.get("api/colecciones/{id}", new GetColeccionHandler(coleccionRepositorio)); // lee una coleccion en particular
        app.put("api/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio)); // actualizo una coleccion
        app.delete("api/colecciones", new DeleteColeccionesHandler(coleccionRepositorio)); // borro una coleccion

        app.post("api/colecciones/{id}/fuente", new PostFuentesColeccionHandler(coleccionRepositorio)); // agrego fuentes
        app.put("api/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio)); // actualizo algoritmo

        app.delete("api/colecciones/{id}/fuente", new DeleteFuenteHandler(coleccionRepositorio)); // borro una fuente

        app.put("api/solicitudes/eliminacion/{id}", new PutSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // aprobar o denegar solicitud eliminacion
        // borrar coleccion seria de acuerdo a un id?

        // API publica
        app.get("/api/hechos", new GetHechosHandler(hechoRepositorio)); // consulto todos los hechos
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler()); //consulta hechos de una coleccion (pudiendo mandar criterios o no)
        app.post("/api/hechos", new PostHechoHandler(dinamicoRepositorio)); //creo hecho
        app.post("/api/solicitudes/eliminacion", new PostSolicitudEliminacionHandler(dinamicoRepositorio)); //creo solicitud
        app.post("/api/solicitudes/modificacion", new PostSolicitudModificacionHandler(dinamicoRepositorio));
    }
}
