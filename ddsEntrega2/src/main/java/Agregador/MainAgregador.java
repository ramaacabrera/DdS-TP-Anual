package Agregador;

import Agregador.Normalizador.NormalizadorMock;
import ApiAdministrativa.Presentacion.*;
import Persistencia.*;
import io.javalin.Javalin;
import utils.LecturaConfig;

import java.util.Properties;

public class MainAgregador {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puerto = Integer.parseInt(config.getProperty("puertoAgregador"));

        System.out.println("Iniciando servidor Componente Estatico en el puerto "+puerto);
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
        }).start(puerto);


        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.get("/hechos", new GetHechosRepoHandler(hechoRepositorio));
        app.get("/colecciones", new GetColeccionesRepoHandler(coleccionRepositorio));
        app.get("/colecciones/{id}", new GetColeccionEspecificaRepoHandler(coleccionRepositorio));


        // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvv       HACER       vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

        app.post("/hechos", new PostHechoRepoHandler(hechoRepositorio));
        app.post("/solicitudes", new PostSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));

        app.put("/colecciones/{id}", new PutColeccionHandler(coleccionRepositorio)); // actualizo una coleccion PROBADOOOOOO
        app.delete("/colecciones/{id}", new DeleteColeccionesHandler(coleccionRepositorio));

        app.post("/colecciones/{id}/fuente", new PostFuentesColeccionHandler(coleccionRepositorio)); // agrego fuentes probbadisimooooo
        app.delete("/colecciones/{id}/fuente", new DeleteFuenteHandler(coleccionRepositorio));

        app.put("/colecciones/{id}/algoritmo", new PutAlgoritmoConsensoHandler(coleccionRepositorio)); // actualizo algoritmo probado

        app.put("/solicitudes/{id}", new PutSolicitudEliminacionHandler(solicitudEliminacionRepositorio)); // aprobar o denegar solicitud eliminacion


        //  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^        HACER       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

        //    <<<<<<<<<-

        NormalizadorMock normalizadorMock = new NormalizadorMock();

        Agregador agregador = new Agregador(hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio, solicitudModificacionRepositorio, normalizadorMock);
    }
}
