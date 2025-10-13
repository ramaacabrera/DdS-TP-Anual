package CargadorDinamica.Presentacion;

import CargadorDinamica.DinamicaDto.Hecho_D_DTO;
import CargadorDinamica.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class GetHechosDinamicoHandler implements Handler {
    private final DinamicoRepositorio repositorio;

    public GetHechosDinamicoHandler(DinamicoRepositorio repositorioNuevo) {
        this.repositorio = repositorioNuevo;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        List<Hecho_D_DTO> hechos = this.obtenerHechos();
        ctx.json(hechos);
        ctx.status(200);
    }

    public List<Hecho_D_DTO> obtenerHechos(){
        List<Hecho_D_DTO> repo = repositorio.buscarHechos();

        repositorio.resetearHechos();

        return repo;
    }
}
