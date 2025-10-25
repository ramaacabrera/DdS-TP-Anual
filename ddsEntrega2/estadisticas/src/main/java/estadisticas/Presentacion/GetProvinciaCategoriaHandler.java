package estadisticas.Presentacion;

import Agregador.HechosYColecciones.Coleccion;
import estadisticas.agregador.EstadisticasCategoriaRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GetProvinciaCategoriaHandler implements Handler {
    private final EstadisticasCategoriaRepositorio repository;

    public GetProvinciaCategoriaHandler(EstadisticasCategoriaRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String categoria = ctx.pathParam("categoria");
        Optional<String> provincia = repository.buscarProvinciaCategoria(categoria);

        if (!provincia.isPresent()) {
            ctx.status(404);
        } else {
            ctx.status(200).json(provincia.get());
        }
    }
}
