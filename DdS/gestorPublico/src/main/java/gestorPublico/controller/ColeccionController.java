package gestorPublico.controller;

import gestorPublico.dto.FiltroHechosDTO;
import gestorPublico.service.ColeccionService;
import io.javalin.http.Handler;
import gestorPublico.dto.Coleccion.ColeccionDTO;
import gestorPublico.dto.Hechos.HechoDTO;
import gestorPublico.dto.PageDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ColeccionController {

    private ColeccionService coleccionService=null;

    public ColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    public Handler obtenerColecciones = ctx -> {
        try {
            int pagina = ctx.queryParamAsClass("pagina", Integer.class).getOrDefault(1);
            int limite = ctx.queryParamAsClass("limite", Integer.class).getOrDefault(10);

            PageDTO<ColeccionDTO> resultado = coleccionService.obtenerTodasLasColecciones(pagina, limite);
            ctx.status(200).json(resultado);

        } catch (Exception e) {
            ctx.status(500).json("Error interno: " + e.getMessage());
        }
    };

    public Handler obtenerColeccionPorId = ctx -> {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            ColeccionDTO coleccion = coleccionService.obtenerColeccionPorId(id);

            if (coleccion != null) {
                ctx.status(200).json(coleccion);
            } else {
                ctx.status(404).json("Colección no encontrada");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).json("ID inválido");
        }
    };

    public Handler obtenerHechosDeColeccion = ctx -> {
        try {
            UUID id = UUID.fromString(ctx.pathParam("id"));
            String modoNavegacion = ctx.queryParam("modoDeNavegacion");

            // Parsear filtros (Igual que en HechoController)
            FiltroHechosDTO filtro = new FiltroHechosDTO();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            filtro.fechaCargaDesde = parseFecha(ctx.queryParam("fecha_reporte_desde"), fmt);
            filtro.fechaCargaHasta = parseFecha(ctx.queryParam("fecha_reporte_hasta"), fmt);
            filtro.fechaAcontecimientoDesde = parseFecha(ctx.queryParam("fecha_acontecimiento_desde"), fmt);
            filtro.fechaAcontecimientoHasta = parseFecha(ctx.queryParam("fecha_acontecimiento_hasta"), fmt);

            filtro.descripcion = ctx.queryParam("descripcion");

            List<HechoDTO> hechos = coleccionService.obtenerHechosDeColeccion(id, filtro, modoNavegacion);

            if (hechos != null) {
                ctx.status(200).json(hechos);
            } else {
                ctx.status(404).json("Colección no encontrada");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("Error obteniendo hechos de colección: " + e.getMessage());
        }
    };

    private Date parseFecha(String fechaStr, SimpleDateFormat formato) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) return null;
        try {
            return formato.parse(fechaStr);
        } catch (ParseException e) {
            return null;
        }
    }
}