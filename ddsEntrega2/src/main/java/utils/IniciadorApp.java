package utils;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;

public class IniciadorApp {

    public Javalin iniciarApp(int puerto, String recursoEstatico) {
        return Javalin.create(javalinConfig -> {
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            }); // para poder hacer requests de un dominio a otro
            javalinConfig.staticFiles.add(recursoEstatico);//recursos estaticos (HTML, CSS, JS, IMG)
            freemarker.template.Configuration fmConfig =
                    new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
            // Esto le dice a FreeMarker que busque en src/main/resources/templates/
            fmConfig.setClassForTemplateLoading(IniciadorApp.class, "/templates/");

            // 2. Registrar el motor usando la configuración personalizada
            javalinConfig.fileRenderer(new JavalinFreemarker(fmConfig));

            javalinConfig.staticFiles.add(staticFiles -> {
                staticFiles.directory = "/public";            // carpeta en src/main/resources/public
                staticFiles.hostedPath = "/";                 // se servirán como /css/... /js/... /img/...
                staticFiles.location = Location.CLASSPATH;    // buscar en classpath (resources)
            });
        }).start(puerto);
    };
}
