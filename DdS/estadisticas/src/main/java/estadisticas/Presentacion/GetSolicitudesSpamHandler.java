package estadisticas.Presentacion;

import estadisticas.agregador.EstadisticasRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetSolicitudesSpamHandler implements Handler {
    private final EstadisticasRepositorio repository;

    public GetSolicitudesSpamHandler(EstadisticasRepositorio repoNuevo) {
        this.repository = repoNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        Optional<Integer> spamCount = repository.buscarSpam();

        Map<String,Object> resultado = new HashMap<>();
        if (!spamCount.isPresent()) {
            resultado.put("error", "Cantidad de Spam no encontrada");
            resultado.put("status", 404);
            ctx.status(404).json(resultado);
        } else {
            resultado.put("spamCount", spamCount.get());
            ctx.status(200).json(resultado);
        }
    }
}
