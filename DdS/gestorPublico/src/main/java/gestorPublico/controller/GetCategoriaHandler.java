package gestorPublico.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.Persistencia.HechoRepositorio;

import java.util.List;

public class GetCategoriaHandler implements Handler {
    HechoRepositorio hechoRepositorio;

    public GetCategoriaHandler(HechoRepositorio hechoRepositorio) {
        this.hechoRepositorio = hechoRepositorio;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        List<String> categorias = hechoRepositorio.buscarCategorias();

        context.json(categorias);
    }
}
