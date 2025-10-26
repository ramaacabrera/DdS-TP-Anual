package utils;

import io.javalin.Javalin;

public class IniciadorApp {

    public io.javalin.Javalin iniciarApp(int puerto, String recursoEstatico) {
        return Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add(recursoEstatico); //recursos estaticos (HTML, CSS, JS, IMG)

            javalinConfig.jetty.wsFactoryConfig(wsFactory -> {
                wsFactory.setMaxTextMessageSize(2 * 1024 * 1024*1024);   // 2 MB
                //sFactory.setMaxBinaryMessageSize(2 * 1024 * 1024); // opcional
            });
        }).start(puerto);
    };
}
