package Agregador;

import Agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GetColeccionEspecificaRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public GetColeccionEspecificaRepoHandler(ColeccionRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) {
        String handle = ctx.pathParam("id");
        Optional<Coleccion> coleccionOpt = repositorio.buscarPorHandle(handle);

        if (!coleccionOpt.isPresent()) {
            ctx.status(404).result("Colección no encontrada");
            return;
        } else {
            ctx.json(coleccionOpt);
        }
    }
}
