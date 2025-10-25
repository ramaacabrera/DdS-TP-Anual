package ApiPublica.Presentacion;

import utils.Persistencia.HechoRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.Dominio.Criterios.Criterio;
import utils.Dominio.Criterios.CriterioCategoria;
import utils.Dominio.Criterios.CriterioFecha;
import utils.Dominio.Criterios.CriterioUbicacion;
import utils.Dominio.HechosYColecciones.Hecho;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import utils.Dominio.HechosYColecciones.Ubicacion;
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
    private final HechoRepositorio hechoRepositorio;

    //public GetHechosHandler() {}

    public GetHechosHandler(HechoRepositorio hechoRepositorio){
        this.hechoRepositorio = hechoRepositorio;
    }

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

        List<Hecho> repositorio = mapper.readValue(response.body(), new TypeReference<>() {
        });

        //    <<<<<<<<<-

        ctx.json(this.buscarHechos(repositorio, criterios));
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

    public List<Hecho> buscarHechos(List<Hecho> hechos, List<Criterio> criterios) {
        if(criterios == null || criterios.isEmpty()){
            return hechos;
        }
        List<Hecho> hechosADevolver = new ArrayList<Hecho>();
        for (Hecho hecho : hechos) {
            for(Criterio criterio : criterios) {
                if(criterio.cumpleConCriterio(hecho) && hecho.estaActivo()) {
                    hechosADevolver.add(hecho);
                }
            }
        }
        return hechosADevolver;
    }
}
