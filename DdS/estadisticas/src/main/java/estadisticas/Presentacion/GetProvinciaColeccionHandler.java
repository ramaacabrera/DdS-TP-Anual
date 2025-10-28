package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

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

            if (provincia.isEmpty()) {
                ctx.status(404);
            } else {
                ctx.status(200).json(provincia.get());
            }
        } catch (Exception e) {
            System.err.println("Error en buscar coleccion: " + e.getMessage());
            ctx.status(500);
        }

    }
}
