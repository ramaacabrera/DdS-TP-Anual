package ApiPublica.Presentacion;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import Agregador.Criterios.Criterio;
import Agregador.Criterios.CriterioCategoria;
import Agregador.Criterios.CriterioFecha;
import Agregador.Criterios.CriterioUbicacion;
import Agregador.HechosYColecciones.ModosDeNavegacion;
import Agregador.HechosYColecciones.Ubicacion;
import org.jetbrains.annotations.NotNull;
import Agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;
import Agregador.HechosYColecciones.Hecho;


import java.util.*;
import java.text.SimpleDateFormat;


public class GetHechosColeccionHandler implements Handler {
    private final ColeccionRepositorio coleccionRepo;

    public GetHechosColeccionHandler(ColeccionRepositorio coleccionRepoNuevo) {
        coleccionRepo = coleccionRepoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id"); // parametro de la URL
        String modNav = ctx.queryParam("modoDeNavegacion");
        ModosDeNavegacion modoNavegacion;
        Optional<Coleccion> coleccionOpt = coleccionRepo.buscarPorHandle(handle);

        if (!coleccionOpt.isPresent()) {
            ctx.status(404).result("Colección no encontrada");
            return;
        }
        if (Objects.equals(modNav, "CURADA")) {
            modoNavegacion = ModosDeNavegacion.valueOf(modNav);
        } else {
            modoNavegacion = ModosDeNavegacion.IRRESTRICTA;
        }
        Coleccion coleccion = coleccionOpt.get();
        //List<Hecho> hechos = coleccion.getHechos();  // base de hechos desde esta colección
        List<Criterio> criterios = armarListaDeCriterios(ctx);
        List<Hecho> hechosAMostrar = coleccion.obtenerHechosQueCumplen(criterios, modoNavegacion);

        ctx.status(200).json(hechosAMostrar);
    }

    private List<Criterio> armarListaDeCriterios(Context ctx) {
        List<Criterio> criterios = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        Optional<String> categoria = Optional.ofNullable(ctx.queryParam("categoria"));
        Optional<String> latitud = Optional.ofNullable(ctx.queryParam("latitud"));
        Optional<String> longitud = Optional.ofNullable(ctx.queryParam("longitud"));

        try {
            //busco una fecha de reporte en la url, con !=null veo si se mando
            //(condición) ? valor_si_verdadero : valor_si_falso;
            //si entra la fecha entonces la parsea con simpledateformat, sino la deja en null
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
                throw new RuntimeException("Ubicación inválida", e);
            }
        }

        return criterios;
    }
}