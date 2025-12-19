package estadisticas.utils;

import freemarker.template.TemplateException;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;

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

            javalinConfig.routing.contextPath = "/gestor-estadisticas";
        }).start(puerto);
    };

    public Javalin iniciarAppWeb(int puerto, String recursoEstatico) {
        return Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            freemarker.template.Configuration fmConfig =
                    new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
            // Esto le dice a FreeMarker que busque en src/main/resources/templates/
            fmConfig.setClassForTemplateLoading(IniciadorApp.class, "/templates/");

            fmConfig.setDefaultEncoding("UTF-8");
            try {
                fmConfig.setSetting(freemarker.template.Configuration.URL_ESCAPING_CHARSET_KEY, "UTF-8");
                fmConfig.setSetting(freemarker.template.Configuration.OUTPUT_ENCODING_KEY, "UTF-8");
            } catch (TemplateException e) {
                throw new RuntimeException(e);
            }

            // 2. Registrar el motor usando la configuración personalizada
            javalinConfig.fileRenderer(new JavalinFreemarker(fmConfig));

            javalinConfig.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/public";            // carpeta en src/main/resources/public
                staticFiles.hostedPath = "/";                 // se servirán como /css/... /js/... /img/...
                staticFiles.location = Location.CLASSPATH;    // buscar en classpath (resources)
            }); //recursos estaticos (HTML, CSS, JS, IMG)

            javalinConfig.jetty.wsFactoryConfig(wsFactory -> {
                wsFactory.setMaxTextMessageSize(2 * 1024 * 1024*1024);   // 2 MB
                //sFactory.setMaxBinaryMessageSize(2 * 1024 * 1024); // opcional
            });
        }).start(puerto);
    };
}
