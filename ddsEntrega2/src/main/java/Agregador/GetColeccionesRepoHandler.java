package Agregador;

import Agregador.HechosYColecciones.Coleccion;
import Persistencia.ColeccionRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GetColeccionesRepoHandler implements Handler {
    private final ColeccionRepositorio repositorio;

    public GetColeccionesRepoHandler(ColeccionRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) {
        List<Coleccion> coleccionOpt = repositorio.obtenerTodas();
        ctx.json(coleccionOpt);

    }
}
