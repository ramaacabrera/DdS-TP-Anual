package ApiPublica.Presentacion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.text.SimpleDateFormat;


public class GetHechosColeccionHandler implements Handler {
    ObjectMapper mapper = new ObjectMapper();

    public GetHechosColeccionHandler(){  //ColeccionRepositorio coleccionRepoNuevo) {
        //coleccionRepo = coleccionRepoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String handle = ctx.pathParam("id"); // parametro de la URL
        String modNav = ctx.queryParam("modoDeNavegacion");
        ModosDeNavegacion modoNavegacion;
        //Coleccion coleccion = coleccionOpt.get();

        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/colecciones/" + handle))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Coleccion coleccion = mapper.readValue(response.body(), new TypeReference<>() {
        });

        //    <<<<<<<<<-


        /*Optional<Coleccion> coleccionOpt = coleccionRepo.buscarPorHandle(handle);

        if (!coleccionOpt.isPresent()) {
            ctx.status(404).result("Colección no encontrada");
            return;
        }*/
        if (Objects.equals(modNav, "CURADA")) {
            modoNavegacion = ModosDeNavegacion.valueOf(modNav);
        } else {
            modoNavegacion = ModosDeNavegacion.IRRESTRICTA;
        }


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
                ctx.status(400).result("Ubicación inválida. Revisar parámetros latitud/longitud.");
                return null;
            }
        }

        return criterios;
    }
}