

import ApiAdministrativa.Presentacion.*;
import Persistencia.*;
import io.javalin.Javalin;
import Agregador.Agregador;
import Agregador.fuente.Fuente;
import CargadorEstatica.ConexionEstatica;
import CargadorEstatica.FuenteEstatica;
import CargadorProxy.FuenteDemo;
import CargadorProxy.FuenteMetaMapa;
import CargadorProxy.APIMock.DemoAPIMockServer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainViejo {
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
        FuenteEstatica fuenteEstatica = new FuenteEstatica(new ConexionEstatica("src/csv_files/desastres_tecnologicos_argentina.csv"));
        DinamicoRepositorio baseDeDatos = new DinamicoRepositorio();
        URL urlInstancia = null;
        try {
            urlInstancia = new URL("http://localhost:8081");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        FuenteMetaMapa fuenteMetaMapa = new FuenteMetaMapa(urlInstancia);


        fuentesDisponibles.add(fuenteDemo);
        //fuentesDisponibles.add(fuenteMetaMapa);
        fuentesDisponibles.add(fuenteEstatica);
        fuentesDisponibles.add(fuenteDinamica);

        Agregador agregador = new Agregador(hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio, fuentesDisponibles);
        agregador.buscarHechosIniciales();

        //Inicializacion de Controller
        ControllerSubirHechos controllerSubirHechos = new ControllerSubirHechos(dinamicoRepositorio);
        ControllerSolicitud controllerSolicitud = new ControllerSolicitud(dinamicoRepositorio);

        // API Administrativa
        //app.exception(UnauthorizedException.class,(e,contexto)->{contexto.status(401).result("No Autorizado");});
        app.before("/api/admin/*", ctx -> {

            // Verificar si la ruta comienza con /api/admin/
            if (ctx.path().startsWith("/api/admin/")) {
                System.out.println("entro al app before 1");
                String token = ctx.header("Autorizacion");

                if (token == null || token.trim().isEmpty()) {
                    System.out.println("entro al app before 2");
                    ctx.status(401).result("No Autorizado - Header de Autorizacion requerido");
                    //throw new UnauthorizedException();
                }
                System.out.println("entro al app before 3");

            }
        });

        //app.post("/api/admin/colecciones", contexto->{contexto.result("Requiere token");});
        app.post("/api/admin/colecciones", new PostColeccionHandler(coleccionRepositorio)); //creo coleccion PROBADOOOO
        app.get("/api/admin/colecciones", new GetColeccionesHandler(coleccionRepositorio)); // consulta todas las colecciones PROBADOOOO
        app.get("/api/admin/colecciones/{id}", new GetColeccionHandler(coleccionRepositorio)); // lee una coleccion en particular PROBADOOOOOO
        app.put("/api/admin/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio)); // actualizo una coleccion PROBADOOOOOO
        app.delete("/api/admin/colecciones/{id}", new DeleteColeccionesHandler(coleccionRepositorio)); // borro una coleccion probadoooooo

        app.post("/api/admin/colecciones/{id}/fuente", new PostFuentesColeccionHandler(coleccionRepositorio)); // agrego fuentes probbadisimooooo
        app.delete("/api/admin/colecciones/{id}/fuente", new DeleteFuenteHandler(coleccionRepositorio)); // borro una fuente

        app.put("/api/admin/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio)); // actualizo algoritmo probado

        app.put("/api/admin/solicitudes/{id}", new PutSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // aprobar o denegar solicitud eliminacion
        app.get("/api/admin/solicitudes", new GetSolicitudesEliminacionHandler(solicitudEliminacionRepositorio)); // consulta todas las solicitudes
        app.get("/api/admin/solicitudes/{id}", new GetSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // consulta una solicitud por id
    }
}
