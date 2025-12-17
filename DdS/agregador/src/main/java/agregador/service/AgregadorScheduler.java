package agregador.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgregadorScheduler {

    private final AgregadorOrquestador agregador;

    public AgregadorScheduler(AgregadorOrquestador agregadorNuevo, int tiempoScheduler) {
        this.agregador = agregadorNuevo;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            agregador.iniciarBusquedaAgregador();
        }, 1, tiempoScheduler, TimeUnit.SECONDS);

        long delayInicial = calcularDelayHastaHora(2);
        scheduler.scheduleAtFixedRate(() -> {
            agregador.ejecutarAlgoritmoDeConsenso();
        }, delayInicial, 24, TimeUnit.HOURS);
    }

    private long calcularDelayHastaHora(int horaObjetivo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaEjecucion = ahora.withHour(horaObjetivo).withMinute(0).withSecond(0).withNano(0);

        if (ahora.compareTo(proximaEjecucion) >= 0) {
            proximaEjecucion = proximaEjecucion.plusDays(1);
        }

        Duration duration = Duration.between(ahora, proximaEjecucion);
        return duration.toHours();
    }
}