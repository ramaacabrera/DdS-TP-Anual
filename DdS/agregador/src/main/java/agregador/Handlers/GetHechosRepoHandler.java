package agregador.Handlers;

import utils.Dominio.HechosYColecciones.Hecho;
import utils.Persistencia.HechoRepositorio;
import org.jetbrains.annotations.NotNull;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.List;

public class GetHechosRepoHandler implements Handler {

    private final HechoRepositorio repositorio;

    public GetHechosRepoHandler(HechoRepositorio hechos) { repositorio = hechos; }

    public void handle(@NotNull Context ctx) throws Exception {
        List<Hecho> hechos = repositorio.getHechos();
        System.out.println(hechos.get(0).getFuente());
        ctx.json(hechos);
    }
}
