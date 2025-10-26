package ApiAdministrativa.Presentacion;

import utils.Dominio.HechosYColecciones.Coleccion;
import utils.Persistencia.ColeccionRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            colecciones = coleccionRepositorio.obtenerColecciones(pagina, limite);
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                    "error", "Error al obtener colecciones",
                    "detalle", e.getMessage()
            ));
            return;
        }

        ctx.status(200).json(colecciones);
    }

}
