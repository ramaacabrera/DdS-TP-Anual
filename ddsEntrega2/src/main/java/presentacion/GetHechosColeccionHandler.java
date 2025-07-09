package presentacion;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import org.example.agregador.Criterios.Criterio;
import org.example.agregador.Criterios.CriterioCategoria;
import org.example.agregador.Criterios.CriterioFecha;
import org.example.agregador.Criterios.CriterioUbicacion;
import org.example.agregador.HechosYColecciones.ModosDeNavegacion;
import org.example.agregador.HechosYColecciones.Ubicacion;
import org.jetbrains.annotations.NotNull;
import org.example.agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;
import org.example.agregador.HechosYColecciones.Hecho;


import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
import java.text.SimpleDateFormat;


public class GetHechosColeccionHandler implements Handler{
    private final ColeccionRepositorio coleccionRepo = new ColeccionRepositorio();

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id"); // parametro de la URL
        String modNav = ctx.queryParam("modNav");
        ModosDeNavegacion modoNavegacion = ModosDeNavegacion.valueOf(modNav);
        Optional<Coleccion> coleccionOpt = coleccionRepo.buscarPorHandle(handle);

        if (!coleccionOpt.isPresent()) {
            ctx.status(404).result("Colección no encontrada");
            return;
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

    private List<Hecho> filtrarHechos(List<Hecho> hechos, List<Criterio> criterios) {
        return hechos.stream()
                .filter(h -> criterios.stream().allMatch(c -> c.cumpleConCriterio(h)))
                .collect(Collectors.toList());
    }

    }


    /*try {
            Optional<Date> fechaReporteDesde = Optional.ofNullable(ctx.queryParam("fecha_reporte_desde") != null ? formato.parse(ctx.queryParam("fecha_reporte_desde")) : null);
            Optional<Date> fechaReporteHasta = Optional.ofNullable(ctx.queryParam("fecha_reporte_hasta") != null ? formato.parse(ctx.queryParam("fecha_reporte_hasta")) : null);
            Optional<Date> fechaAcontecimientoDesde = Optional.ofNullable(ctx.queryParam("fecha_acontecimiento_desde") != null ? formato.parse(ctx.queryParam("fecha_acontecimiento_desde")) : null);
            Optional<Date> fechaAcontecimientoHasta = Optional.ofNullable(ctx.queryParam("fecha_acontecimiento_hasta") != null ? formato.parse(ctx.queryParam("fecha_acontecimiento_hasta")) : null);

            //fechaAcontecimientoDesde.ifPresent(f -> criterios.add(new CriterioFechaAcontecimientoDesde(f)));
            //fechaAcontecimientoHasta.ifPresent(f -> criterios.add(new CriterioFechaAcontecimientoHasta(f)));
            //fechaReporteDesde.ifPresent(f -> criterios.add(new CriterioFechaReporteDesde(f)));
            //fechaReporteHasta.ifPresent(f -> criterios.add(new CriterioFechaReporteHasta(f)));

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear fechas", e);
        } */