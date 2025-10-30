package ApiPublica.Presentacion;

import utils.Dominio.Criterios.*;
import utils.Persistencia.HechoRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import utils.Dominio.HechosYColecciones.Hecho;
import utils.DTO.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class GetHechosHandler implements Handler {
    private final HechoRepositorio hechoRepositorio;


    public GetHechosHandler(HechoRepositorio hechoRepositorio){
        this.hechoRepositorio = hechoRepositorio;
    }

    public void handle(@NotNull Context ctx) throws IOException, InterruptedException {
        List<Criterio> criterios = this.armarListaDeCriterios(ctx);

        List<Hecho> hechos = hechoRepositorio.buscarHechos(criterios);

        int pagina = ctx.queryParamAsClass("pagina", Integer.class).getOrDefault(1);
        int limite = ctx.queryParamAsClass("limite", Integer.class).getOrDefault(10);
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int total = hechos.size();
        int totalPages = (int) Math.ceil(total / (double) limite);

        int fromIndex = (pagina - 1) * limite;
        if (fromIndex > total) fromIndex = Math.max(0, total - limite);
        int toIndex = Math.min(fromIndex + limite, total);

        //List<Hecho> hechos = hechoRepositorio.buscarHechos(criterios, fromIndex, toIndex);

        List<Hecho> pageContent = hechos.subList(fromIndex, toIndex);

        List<HechoDTO> hechosDTO = pageContent.stream().map(HechoDTO::new).collect(Collectors.toList());

        System.out.println("Hechos: " + hechosDTO);

        ctx.json(new PageDTO<>(hechosDTO, pagina, limite, totalPages, total));
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

        // Contribuyente
        String contribuyente = ctx.queryParam("contribuyente");
        if (contribuyente != null) {
            criterios.add(new CriterioContribuyente(contribuyente));
        }

        //Criterios de texto
        String texto = ctx.queryParam("textoBusqueda");
        System.out.println("📝 Palabras: " + texto);
        if (texto != null) {
            List<String> textos = Arrays.asList(texto.split(" "));
            System.out.println("📝 Palabras separadas: " + textos);
            criterios.add(new CriterioDeTexto(textos, TipoDeTexto.BUSQUEDA));
        }

        return criterios;
    }
}
