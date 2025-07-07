
import io.javalin.Javalin;
import org.example.agregador.Agregador;
import org.example.fuenteProxy.ConexionDemo;
import org.example.fuenteProxy.Quartz.RecopilacionHechosJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import presentacion.*;


import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws InterruptedException, SchedulerException {

        ////////////////////inicio de  cosas para demo
        // 1. Iniciar el API Mock Server en un hilo separado
        System.out.println("Iniciando API Mock de Fuente Demo...");
        new Thread(() -> DemoApiMockServer.main(new String[]{})).start();
        Thread.sleep(2000); // Dar un pequeño tiempo para que el mock server se inicialice

        // 2. Inicializar componentes de tu dominio que el Job necesita
        ConexionDemo conexionDemo = new ConexionDemo();
        FuenteDemo fuenteDemoMetaMapa = new FuenteDemo("Fuente Demo Externa", conexionDemo);
        Agregador agregador = Agregador.getInstance(); // Obtén la instancia Singleton del Agregador

        // 3. Configurar y iniciar el Scheduler de Quartz
        System.out.println("Configurando Scheduler de Quartz...");
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // Crear el Job para recopilación y procesamiento
        JobDetail recopilacionProcesamientoJob = JobBuilder.newJob(RecopilacionHechosJob.class)
                .withIdentity("recopilacionProcesamientoHechosJob", "grupoFuentes")
                .usingJobData("apiUrl", API_MOCK_URL)
                // Inicia la primera consulta con una fecha en el pasado (ej. 1 hora atrás)
                .usingJobData("ultimaConsultaFecha", LocalDateTime.now().minusHours(1))
                .usingJobData("conexionDemoInstance", conexionDemo)
                .usingJobData("fuenteDemoMetaMapaInstance", fuenteDemoMetaMapa)
                .usingJobData("agregadorInstance", agregador) // Pasa la instancia del Agregador
                .build();

        // Crear el Trigger para que el Job se ejecute cada 1 hora
        Trigger recopilacionProcesamientoTrigger = TriggerBuilder.newTrigger()
                .withIdentity("recopilacionProcesamientoHechosTrigger", "grupoFuentes")
                .startNow() // Empieza tan pronto como se inicia la aplicación
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1) // ¡Se ejecutará cada 1 hora!
                        .repeatForever())
                .build();

        // Programar el Job en el Scheduler
        scheduler.scheduleJob(recopilacionProcesamientoJob, recopilacionProcesamientoTrigger);
        scheduler.start(); // Iniciar el Scheduler de Quartz
        ////////////////////////////////////////////////////////aca termina

        System.out.println("Iniciando servidor Javalin en el puerto 8080...");
        Javalin app = Javalin.create(javalinConfig -> {
                            javalinConfig.plugins.enableCors(cors -> {
                                cors.add(it -> it.anyHost());
                            }); // para poder hacer requests de un dominio a otro
                            javalinConfig.staticFiles.add("/"); //recursos estaticos (HTML, CSS, JS, IMG)
                        }).start(8080);

        app.get("/api/hechos", new GetColeccionesHandler()); //consulto coleccion
        app.get("/api/colecciones/{id}/hechos", new GetHechosColeccionHandler()); //consulta hechos
        app.post("/api/colecciones", new PostColeccionHandler()); //creo coleccion
        app.post("/api/hechos", new PostHechoHandler()); //creo hecho
        app.post("/api/solicitudes", new PostSolicitudEliminacionHandler()); //creo solicitud
    }
}
