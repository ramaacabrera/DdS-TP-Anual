package Agregador;

import Agregador.Normalizador.NormalizadorMock;
import Agregador.Presentacion.*;
import Agregador.Persistencia.*;
import io.javalin.Javalin;
import utils.IniciadorApp;
import utils.LecturaConfig;

import java.util.Properties;

public class MainAgregador {
    public static void main(String[] args) throws InterruptedException {
        LecturaConfig lector = new LecturaConfig();
        Properties config = lector.leerConfig();
        int puertoAgregador = Integer.parseInt(config.getProperty("puertoAgregador"));
        int puertoEstatico = Integer.parseInt(config.getProperty("puertoEstatico"));
        int puertoDinamico = Integer.parseInt(config.getProperty("puertoDinamico"));
        int puertoProxy = Integer.parseInt(config.getProperty("puertoProxy"));
        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>
        System.out.println("Iniciando servidor Agregador en el puerto "+puertoAgregador);
        IniciadorApp iniciador = new IniciadorApp();
        Javalin app = iniciador.iniciarApp(puertoAgregador, "/");

        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        SolicitudModificacionRepositorio solicitudModificacionRepositorio = new SolicitudModificacionRepositorio();
        SolicitudEliminacionRepositorio solicitudEliminacionRepositorio = new SolicitudEliminacionRepositorio();

        app.get("/hechos", new GetHechosRepoHandler(hechoRepositorio));
        app.get("/colecciones", new GetColeccionesRepoHandler(coleccionRepositorio));
        app.get("/colecciones/{id}", new GetColeccionEspecificaRepoHandler(coleccionRepositorio));


        app.post("/solicitudes", new PostSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));

        app.put("/colecciones/{id}", new PutColeccionRepoHandler(coleccionRepositorio));
        app.delete("/colecciones/{id}", new DeleteColeccionesRepoHandler(coleccionRepositorio));

        app.post("/colecciones/{id}/fuente", new PostFuentesColeccionRepoHandler(coleccionRepositorio));
        app.delete("/colecciones/{id}/fuente", new DeleteFuenteRepoHandler(coleccionRepositorio));

        app.put("/colecciones/{id}/algoritmo", new PutAlgoritmoDeConsensoRepoHandler(coleccionRepositorio));

        app.put("/solicitudes/{id}", new PutSolicitudEliminacionRepoHandler(solicitudEliminacionRepositorio));


        //    <<<<<<<<<-

        NormalizadorMock normalizadorMock = new NormalizadorMock();
        ConexionCargador cargador = new ConexionCargador("http://localhost:"+puertoEstatico, "http://localhost:"+puertoDinamico, "http://localhost:"+puertoProxy);
        Agregador agregador = new Agregador(hechoRepositorio, coleccionRepositorio, solicitudEliminacionRepositorio, solicitudModificacionRepositorio, normalizadorMock, cargador);
    }
}
