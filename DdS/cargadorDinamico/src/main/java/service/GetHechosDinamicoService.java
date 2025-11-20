package service;

import repository.DinamicoRepositorio;
import utils.DTO.HechoDTO;
import utils.Dominio.fuente.Fuente;
import utils.Dominio.fuente.TipoDeFuente;

import java.util.List;

public class GetHechosDinamicoService {
    private final DinamicoRepositorio repositorio;

    public GetHechosDinamicoService(DinamicoRepositorio repositorioNuevo) {
        this.repositorio = repositorioNuevo;
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
