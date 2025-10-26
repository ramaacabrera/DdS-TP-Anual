package cargadorDinamico.Presentacion;

import cargadorDinamico.DinamicoRepositorio;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import utils.DTO.HechoDTO;
import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;

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
        List<HechoDTO> dtos = repositorio.buscarHechos();
        repositorio.resetearHechos();
        dtos.forEach(dto -> {
           dto.setFuente(new Fuente(TipoDeFuente.DINAMICA, "fuenteDinamica"));
        });
        return dtos;
    }


}
