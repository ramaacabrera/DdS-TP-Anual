package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.HechoDTO;
import java.util.List;

public class GetHechosDinamicoHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public GetHechosDinamicoHandler(DinamicoRepositorio repositorioNuevo) {
        this.repositorio = repositorioNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        List<HechoDTO> hechos = this.obtenerHechos();
        ctx.json(hechos);
        ctx.status(200);
    }

    public List<HechoDTO> obtenerHechos(){
        List<HechoDTO> repo = repositorio.buscarHechos();

        repositorio.resetearHechos();

        return repo;
    }
}
