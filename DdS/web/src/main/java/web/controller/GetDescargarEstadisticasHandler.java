package web.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import web.service.EstadisticasService;

import java.io.InputStream;
import java.time.LocalDate;

public class GetDescargarEstadisticasHandler implements Handler {
    private final EstadisticasService estadisticasService;

    public GetDescargarEstadisticasHandler(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        InputStream csvStream = estadisticasService.descargarReporteCSV();

        ctx.header("Content-Type", "text/csv; charset=UTF-8");
        ctx.header("Content-Disposition", "attachment; filename=\"reporte_metamapa_" + LocalDate.now() + ".csv\"");

        ctx.result(csvStream);
    }
}