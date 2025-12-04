package estadisticas.controller;

import estadisticas.domain.Estadisticas;
import estadisticas.domain.EstadisticasCategoria;
import estadisticas.domain.EstadisticasColeccion;
import estadisticas.repository.EstadisticasCategoriaRepositorio;
import estadisticas.repository.EstadisticasColeccionRepositorio;
import estadisticas.repository.EstadisticasRepositorio;
import estadisticas.utils.EstadisticasCSVTransformer;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GetEstadisticasCSVHandler implements Handler {

    private final EstadisticasRepositorio estadisticasRepo;
    private final EstadisticasCategoriaRepositorio categoriasRepo;
    private final EstadisticasColeccionRepositorio coleccionesRepo;

    public GetEstadisticasCSVHandler(EstadisticasRepositorio estadisticasRepo,
                                     EstadisticasCategoriaRepositorio categoriasRepo,
                                     EstadisticasColeccionRepositorio coleccionesRepo) {
        this.estadisticasRepo = estadisticasRepo;
        this.categoriasRepo = categoriasRepo;
        this.coleccionesRepo = coleccionesRepo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Optional<Estadisticas> ultimaEstadisticaOpt = estadisticasRepo.obtenerUltimaEstadistica();

        if (ultimaEstadisticaOpt.isEmpty()) {
            ctx.status(404).result("No hay estadísticas generadas aún para exportar.");
            return;
        }

        Estadisticas estadistica = ultimaEstadisticaOpt.get();

        List<EstadisticasCategoria> listaCategorias = categoriasRepo.buscarPorEstadisticaPadre(estadistica);
        List<EstadisticasColeccion> listaColecciones = coleccionesRepo.buscarPorEstadisticaPadre(estadistica);

        String contenidoCSV = EstadisticasCSVTransformer.transformarAFormatoCSV(estadistica, listaCategorias, listaColecciones);

        ctx.header("Content-Type", "text/csv");
        ctx.header("Content-Disposition", "attachment; filename=\"reporte_metamapa_" + LocalDate.now() + ".csv\"");

        ctx.result(contenidoCSV);
    }
}