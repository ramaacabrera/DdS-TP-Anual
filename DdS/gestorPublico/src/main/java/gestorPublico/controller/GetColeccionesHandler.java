package gestorPublico.controller;

import utils.DTO.PageDTO;
import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;


public class GetColeccionesHandler implements Handler {
    private final ColeccionRepositorio coleccionRepositorio;

    public GetColeccionesHandler(ColeccionRepositorio coleccionRepositorio) {
        this.coleccionRepositorio = coleccionRepositorio;
    }

    public void handle(@NotNull Context ctx) throws Exception {
        String paginaParam = ctx.queryParam("pagina");
        String limiteParam = ctx.queryParam("limite");

        Integer pagina = null;
        Integer limite = null;

        try {
            if (paginaParam != null && !paginaParam.trim().isEmpty()) {
                pagina = Integer.parseInt(paginaParam);
            }
            if (limiteParam != null && !limiteParam.trim().isEmpty()) {
                limite = Integer.parseInt(limiteParam);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of(
                    "error", "Parámetros de paginación inválidos",
                    "detalle", "pagina y limite deben ser números enteros"
            ));
            return;
        }

        // Validar valores si se proporcionaron
        if (pagina != null && pagina < 1) {
            ctx.status(400).json(Map.of(
                    "error", "Parámetro 'pagina' debe ser mayor a 0"
            ));
            return;
        }

        if (limite != null && limite < 1) {
            ctx.status(400).json(Map.of(
                    "error", "Parámetro 'limite' debe ser mayor a 0"
            ));
            return;
        }

        // Obtener colecciones del repositorio con paginación
        List<Coleccion> colecciones;
        try {
            colecciones = coleccionRepositorio.obtenerTodas();
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                    "error", "Error al obtener colecciones",
                    "detalle", e.getMessage()
            ));
            return;
        }

        int total = colecciones.size();
        int totalPages = (int) Math.ceil(total / (double) limite);

        int fromIndex = (pagina - 1) * limite;
        if (fromIndex > total) fromIndex = Math.max(0, total - limite);
        int toIndex = Math.min(fromIndex + limite, total);

        colecciones = colecciones.subList(fromIndex, toIndex);

        ctx.json(new PageDTO<>(colecciones, pagina,limite, totalPages,total));
    }

}
