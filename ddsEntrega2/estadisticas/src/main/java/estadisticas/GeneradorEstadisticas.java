package estadisticas;

import estadisticas.Dominio.*;
import estadisticas.agregador.ConexionAgregador;
import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import estadisticas.agregador.EstadisticasColeccionRepositorio;
import estadisticas.agregador.EstadisticasRepositorio;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GeneradorEstadisticas {
    private final ConexionAgregador conexionAgregador;
    private final EstadisticasRepositorio estadisticasRepositorio;
    private final EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorio;
    private final EstadisticasColeccionRepositorio estadisticasColeccionRepositorio;
    private Estadisticas estadisticasActual = null;

    public GeneradorEstadisticas(ConexionAgregador conexionAgregador, EstadisticasRepositorio estadisticasRepositorioNuevo,
                                 EstadisticasCategoriaRepositorio estadisticasCategoriaRepositorioNuevo, EstadisticasColeccionRepositorio estadisticasColeccionRepositorioNuevo){
        this.conexionAgregador = conexionAgregador;
        this.estadisticasRepositorio = estadisticasRepositorioNuevo;
        this.estadisticasCategoriaRepositorio = estadisticasCategoriaRepositorioNuevo;
        this.estadisticasColeccionRepositorio = estadisticasColeccionRepositorioNuevo;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            this.actualizarEstadisticas();
        }, 0, 1, TimeUnit.DAYS);

    }

    private void actualizarEstadisticas() {
        this.actualizarEstadisticasBase();
        this.actualizarEstadisticasCategoria();
        this.actualizarEstadisticasColeccion();
    }

    private void actualizarEstadisticasBase() {
        int spam = conexionAgregador.obtenerSpamActual();
        String categoria = conexionAgregador.obtenerCategoriaMaxHechos();
        Estadisticas estadisticas = new Estadisticas(UUID.randomUUID(),
                new Date(), spam, categoria);

        estadisticasRepositorio.guardar(estadisticas);

        this.estadisticasActual= estadisticas;
    }

    private void actualizarEstadisticasCategoria() {
        Map<String, String> provinciasPorCategoria = conexionAgregador.obtenerProvinciasPorCategoria();
        Map<String, Integer> horasPorCategoria = conexionAgregador.obtenerHorasPicoPorCategoria();

        for (String categoria : provinciasPorCategoria.keySet()) {
            String provincia = provinciasPorCategoria.get(categoria);
            Integer hora = horasPorCategoria.get(categoria);

            EstadisticasCategoriaId id = new EstadisticasCategoriaId(estadisticasActual.getEstadisticas_id(), categoria);

            EstadisticasCategoria estadisticaCategoria = new EstadisticasCategoria(id, provincia, hora);

            estadisticasCategoriaRepositorio.guardar(estadisticaCategoria);
        }
    }

    private void actualizarEstadisticasColeccion() {
        Map<UUID, String> provinciaPorColeccion = conexionAgregador.obtenerProvinciaPorColeccion();

        for (Map.Entry<UUID, String> entry : provinciaPorColeccion.entrySet()) {
            UUID coleccionId = entry.getKey();
            String provincia = entry.getValue();

            EstadisticasColeccionId id = new EstadisticasColeccionId(
                    estadisticasActual.getEstadisticas_id(),
                    coleccionId
            );

            EstadisticasColeccion estadisticaColeccion = new EstadisticasColeccion(
                    id,
                    provincia
            );

            estadisticasColeccionRepositorio.guardar(estadisticaColeccion);
        }
    }
}
