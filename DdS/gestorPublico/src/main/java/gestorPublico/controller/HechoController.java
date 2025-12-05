package gestorPublico.controller;

import gestorPublico.dto.FiltroHechosDTO;
import gestorPublico.service.FuenteService;
import gestorPublico.service.HechoService;
import io.javalin.http.Handler;
import io.javalin.http.BadRequestResponse;
import gestorPublico.dto.Hechos.HechoDTO;
import gestorPublico.dto.PageDTO;
import gestorPublico.dto.Hechos.FuenteDTO;

import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HechoController {

    private HechoService hechoService = null;
    private FuenteService fuenteService = null;

    public HechoController(HechoService hechoService, FuenteService fuenteService) {
        this.hechoService = hechoService;
        this.fuenteService = fuenteService;
    }

    public Handler obtenerHechos = ctx -> {
        try {
            FiltroHechosDTO filtro = new FiltroHechosDTO();

            filtro.categoria = ctx.queryParam("categoria");
            filtro.contribuyente = ctx.queryParam("contribuyente");
            filtro.textoBusqueda = ctx.queryParam("textoBusqueda");

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            filtro.fechaCargaDesde = parseFecha(ctx.queryParam("fecha_carga_desde"), formato);
            filtro.fechaCargaHasta = parseFecha(ctx.queryParam("fecha_carga_hasta"), formato);
            filtro.fechaAcontecimientoDesde = parseFecha(ctx.queryParam("fecha_acontecimiento_desde"), formato);
            filtro.fechaAcontecimientoHasta = parseFecha(ctx.queryParam("fecha_acontecimiento_hasta"), formato);

            filtro.descripcion = ctx.queryParam("descripcion");

            filtro.pagina = ctx.queryParamAsClass("pagina", Integer.class).getOrDefault(1);
            filtro.limite = ctx.queryParamAsClass("limite", Integer.class).getOrDefault(10);

            PageDTO<HechoDTO> resultado = hechoService.buscarHechos(filtro);

            ctx.status(200).json(resultado);

        } catch (IllegalArgumentException e) {
            ctx.status(400).json("Error en parámetros: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno: " + e.getMessage());
        }
    };

    public Handler obtenerHechoPorId = ctx -> {
        String idString = ctx.pathParam("id");
        try {
            UUID id = UUID.fromString(idString);
            HechoDTO hecho = hechoService.obtenerHechoPorId(id);

            if (hecho != null) {
                ctx.status(200).json(hecho);
            } else {
                ctx.status(404).json("Hecho no encontrado");
            }

        } catch (IllegalArgumentException e) {
            ctx.status(400).json("ID inválido");
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo hecho: " + e.getMessage());
        }
    };

    public Handler crearHecho = ctx -> {
        try {
            String body = ctx.body();
            // Llamamos al servicio que actúa de proxy
            HttpResponse<String> response = hechoService.crearHechoProxy(body);

            // Devolvemos exactamente lo que respondió el otro servicio
            ctx.status(response.statusCode()).result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno reenviando solicitud: " + e.getMessage());
        }
    };

    public Handler buscarCategorias = ctx -> {
        try {
            List<String> categorias = hechoService.buscarCategorias();

            ctx.status(200).json(categorias);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error interno reenviando solicitud: " + e.getMessage());
        }
    };

    private Date parseFecha(String fechaStr, SimpleDateFormat formato) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) return null;
        try {
            return formato.parse(fechaStr);
        } catch (ParseException e) {
            throw new BadRequestResponse("Formato de fecha inválido (dd/MM/yyyy): " + fechaStr);
        }
    }

    public Handler obtenerTodasLasFuentes = ctx -> {
        try {
            List<FuenteDTO> fuentes = fuenteService.obtenerTodasLasFuentes();
            ctx.status(200).json(fuentes);
        } catch (Exception e) {
            ctx.status(500).json("Error obteniendo fuentes: " + e.getMessage());
        }
    };
}