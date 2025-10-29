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
        try{
            UUID coleccion = UUID.fromString(ctx.pathParam("coleccion"));
            Optional<String> provincia = repository.buscarProvinciaColeccion(coleccion);

            Map<String,Object> resultado = new HashMap<>();
            if (!provincia.isPresent()) {
                resultado.put("error", "Coleccion no encontrada");
                resultado.put("status", 404);
                ctx.status(404).json(resultado);
            } else {
                resultado.put("hora", provincia.get());
                ctx.status(200).json(resultado);
            }
        } catch (Exception e) {
            System.err.println("Error en buscar coleccion: " + e.getMessage());
            ctx.status(500);
        }

    }
}
