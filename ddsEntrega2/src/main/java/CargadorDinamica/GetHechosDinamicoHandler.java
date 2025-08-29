package CargadorDinamica;

import CargadorEstatica.ConexionEstatica;
import Persistencia.DinamicoRepositorio;
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
        ctx.status(200).result("Hechos de fuente dinamica entregados al Agregador.");
    }

    public List<HechoDTO> obtenerHechos(){
        List<HechoDTO> repo = repositorio.buscarHechos();

        repositorio.resetear();

        return repo;
    }
}
