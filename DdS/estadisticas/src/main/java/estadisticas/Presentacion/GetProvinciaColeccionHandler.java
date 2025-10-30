package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GetProvinciaColeccionHandler implements Handler {
    private final EstadisticasColeccionRepositorio repository;

    public GetProvinciaColeccionHandler(EstadisticasColeccionRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            String coleccionParam = ctx.pathParam("coleccion");
            UUID coleccion;

            try {
                coleccion = UUID.fromString(coleccionParam);
            } catch (IllegalArgumentException e) {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("error", "UUID de colección inválido");
                resultado.put("status", 400);
                ctx.status(400).json(resultado);
                return;
            }

            Optional<Map<String, String>> datosColeccion = repository.buscarProvinciaYNombreColeccion(coleccion);

            Map<String, Object> resultado = new HashMap<>();
            if (!datosColeccion.isPresent()) {
                resultado.put("error", "Colección no encontrada");
                resultado.put("status", 404);
                ctx.status(404).json(resultado);
            } else {
                Map<String, String> datos = datosColeccion.get();
                resultado.put("provincia", datos.get("provincia"));
                resultado.put("nombre", datos.get("nombre"));
                ctx.status(200).json(resultado);
            }
        } catch (Exception e) {
            System.err.println("Error en buscar colección: " + e.getMessage());
            ctx.status(500).json(Map.of(
                    "error", "Error interno del servidor",
                    "detalle", e.getMessage()
            ));
        }
    }
}