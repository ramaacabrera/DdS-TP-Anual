package ApiPublica.Presentacion;

import Agregador.Persistencia.HechoRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import Agregador.Criterios.Criterio;
import Agregador.Criterios.CriterioCategoria;
import Agregador.Criterios.CriterioFecha;
import Agregador.Criterios.CriterioUbicacion;
import Agregador.HechosYColecciones.Hecho;
import Agregador.HechosYColecciones.Ubicacion;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class GetHechosHandler implements Handler {
    //private final HechoRepositorio repositorio;
    ObjectMapper mapper = new ObjectMapper();
    public GetHechosHandler(){} //HechoRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) throws IOException, InterruptedException {
        List<Criterio> criterios = this.armarListaDeCriterios(ctx);

        /*

                CAMBIAR ESTO CUANDO SE IMPLEMENTEN LAS BASES DE DATOS


        */

        //    ->>>>>>>>>

        HttpClient httpClient = HttpClient.newHttpClient();

        URI uri = null;
        try{uri = new URI("http://localhost:8080/hechos");}
        catch (URISyntaxException e) {
            System.err.println("URI invalido "+e.getMessage());
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());





        HechoRepositorio repositorio = mapper.readValue(response.body(), new TypeReference<>() {
        });

        //    <<<<<<<<<-
        List<Hecho> hechosFiltrados = repositorio.buscarHechos(criterios);
        ctx.json(hechosFiltrados);
    }

    private List<Criterio> armarListaDeCriterios(Context ctx) {
        List<Criterio> criterios = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        // Categoria
        String categoria = ctx.queryParam("categoria");
        if (categoria != null && !categoria.trim().isEmpty()) {
            criterios.add(new CriterioCategoria(categoria));
        }

        // Fechas Carga
        Date fechaCargaDesde = null;
        Date fechaCargaHasta = null;
        String rawCargaDesde = ctx.queryParam("fecha_carga_desde");
        String rawCargaHasta = ctx.queryParam("fecha_carga_hasta");

        if (rawCargaDesde != null && !rawCargaDesde.trim().isEmpty()) {
            try { fechaCargaDesde = formato.parse(rawCargaDesde); }
            catch (ParseException e) {
                throw new BadRequestResponse("fecha_carga_desde debe tener formato dd/MM/yyyy");
            }
        }
        if (rawCargaHasta != null && !rawCargaHasta.trim().isEmpty()) {
            try { fechaCargaHasta = formato.parse(rawCargaHasta); }
            catch (ParseException e) {
                throw new BadRequestResponse("fecha_carga_hasta debe tener formato dd/MM/yyyy");
            }
        }
        if (fechaCargaDesde != null || fechaCargaHasta != null) {
            criterios.add(new CriterioFecha(fechaCargaDesde, fechaCargaHasta, "fechaDeCarga"));
        }

        // Fechas Acontecimiento
        Date fechaAcontecimientoDesde = null;
        Date fechaAcontecimientoHasta = null;
        String rawAcontecimientoDesde = ctx.queryParam("fecha_acontecimiento_desde");
        String rawAcontecimientoHasta = ctx.queryParam("fecha_acontecimiento_hasta");

        if (rawAcontecimientoDesde != null && !rawAcontecimientoDesde.trim().isEmpty()) {
            try { fechaAcontecimientoDesde = formato.parse(rawAcontecimientoDesde); }
            catch (ParseException e) {
                throw new BadRequestResponse("fecha_acontecimiento_desde debe tener formato dd/MM/yyyy");
            }
        }
        if (rawAcontecimientoHasta != null && !rawAcontecimientoHasta.trim().isEmpty()) {
            try { fechaAcontecimientoHasta = formato.parse(rawAcontecimientoHasta); }
            catch (ParseException e) {
                throw new BadRequestResponse("fecha_acontecimiento_hasta debe tener formato dd/MM/yyyy");
            }
        }
        if (fechaAcontecimientoDesde != null || fechaAcontecimientoHasta != null) {
            criterios.add(new CriterioFecha(fechaAcontecimientoDesde, fechaAcontecimientoHasta, "fechaDeAcontecimiento"));
        }

        // Ubicacion
        String latitudString = ctx.queryParam("latitud");
        String longitudString = ctx.queryParam("longitud");
        if (latitudString != null && longitudString != null) {
            int latitud = Integer.parseInt(latitudString);
            int longitud = Integer.parseInt(longitudString);
            criterios.add(new CriterioUbicacion(new Ubicacion(latitud, longitud)));

        }

        return criterios;
    }
}
