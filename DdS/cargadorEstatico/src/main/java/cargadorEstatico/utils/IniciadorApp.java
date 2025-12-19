package cargadorEstatico.utils;

import io.javalin.Javalin;

import java.time.Duration;

public class IniciadorApp {

    public Javalin iniciarApp(int puerto, String recursoEstatico) {
        return Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro

            javalinConfig.jetty.wsFactoryConfig(wsFactory -> {
                wsFactory.setIdleTimeout(Duration.ofDays(1));
                wsFactory.setMaxTextMessageSize(2L * 1024 * 1024 * 1024); // 2 MB
            });
        }).start(puerto);
    };
}
