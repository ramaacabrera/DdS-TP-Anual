package estadisticas;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EstadisticasScheduler {
    GeneradorEstadisticas generador;

    public EstadisticasScheduler(GeneradorEstadisticas generador){
        this.generador = generador;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(generador::actualizarEstadisticas, 1, 60, TimeUnit.SECONDS);
    }

}
