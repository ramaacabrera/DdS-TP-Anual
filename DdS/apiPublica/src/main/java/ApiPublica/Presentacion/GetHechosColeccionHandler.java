package ApiPublica.Presentacion;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import utils.Dominio.HechosYColecciones.Ubicacion;
import utils.Dominio.Criterios.Criterio;
import utils.Dominio.Criterios.CriterioCategoria;
import utils.Dominio.Criterios.CriterioFecha;
import utils.Dominio.Criterios.CriterioUbicacion;
import utils.Dominio.HechosYColecciones.ModosDeNavegacion;
import org.jetbrains.annotations.NotNull;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.ColeccionRepositorio;

import java.util.*;
import java.text.SimpleDateFormat;


public class GetHechosColeccionHandler implements Handler {
    ColeccionRepositorio repositorio;

    public GetHechosColeccionHandler(ColeccionRepositorio coleccionRepoNuevo) {
        repositorio = coleccionRepoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id"); // parametro de la URL
        String modNav = ctx.queryParam("modoDeNavegacion");
        ModosDeNavegacion modoNavegacion;

        if (Objects.equals(modNav, "CURADA")) {
            modoNavegacion = ModosDeNavegacion.valueOf(modNav);
        } else {
            modoNavegacion = ModosDeNavegacion.IRRESTRICTA;
        }


        List<Criterio> criterios = armarListaDeCriterios(ctx);
        List<Hecho> hechosAMostrar = repositorio.obtenerHechosEspecificos(handle, criterios, modoNavegacion);

        ctx.status(200).json(hechosAMostrar);
    }

    private List<Criterio> armarListaDeCriterios(Context ctx) {
        List<Criterio> criterios = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        Optional<String> categoria = Optional.ofNullable(ctx.queryParam("categoria"));
        Optional<String> latitud = Optional.ofNullable(ctx.queryParam("latitud"));
        Optional<String> longitud = Optional.ofNullable(ctx.queryParam("longitud"));

        try {
            Date fechaRepDesde = ctx.queryParam("fecha_reporte_desde") != null
                    ? formato.parse(ctx.queryParam("fecha_reporte_desde")) : null;
            Date fechaRepHasta = ctx.queryParam("fecha_reporte_hasta") != null
                    ? formato.parse(ctx.queryParam("fecha_reporte_hasta")) : null;
            Date fechaAconDesde = ctx.queryParam("fecha_acontecimiento_desde") != null
                    ? formato.parse(ctx.queryParam("fecha_acontecimiento_desde")) : null;
            Date fechaAconHasta = ctx.queryParam("fecha_acontecimiento_hasta") != null
                    ? formato.parse(ctx.queryParam("fecha_acontecimiento_hasta")) : null;

            if (fechaRepDesde != null || fechaRepHasta != null)
                criterios.add(new CriterioFecha(fechaRepDesde, fechaRepHasta, "fechaDeCarga"));

            if (fechaAconDesde != null || fechaAconHasta != null)
                criterios.add(new CriterioFecha(fechaAconDesde, fechaAconHasta, "fechaDeAcontecimiento"));

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear fechas. Formato esperado: yyyy-MM-dd", e);
        }

        categoria.ifPresent(c -> criterios.add(new CriterioCategoria(c)));

        if (latitud.isPresent() && longitud.isPresent()) {
            try {
                double lat = Integer.parseInt(latitud.get());
                double lon = Integer.parseInt(longitud.get());
                criterios.add(new CriterioUbicacion(new Ubicacion(lat, lon)));
            } catch (NumberFormatException e) {
                ctx.status(400).result("Ubicación inválida. Revisar parámetros latitud/longitud.");
                return null;
            }
        }

        return criterios;
    }
}