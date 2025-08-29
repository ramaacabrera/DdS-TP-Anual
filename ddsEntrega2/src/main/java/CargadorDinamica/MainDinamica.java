package CargadorDinamica;

import Persistencia.DinamicoRepositorio;
import io.javalin.Javalin;

public class MainDinamica {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando servidor Javalin en el puerto 8083...");
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
        }).start(8083);

        DinamicoRepositorio dinamicoRepositorio = new DinamicoRepositorio();

        // Exposicion API mediante REST para el agregador
        app.get("/hechos", new GetHechosDinamicoHandler(dinamicoRepositorio));
        app.post("/hechos", new PostHechosHandler(dinamicoRepositorio));
    }
}
