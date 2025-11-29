package cargadorDinamico.service;

import cargadorDinamico.domain.DinamicaDto.Hecho_D_DTO;
import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.domain.DinamicaDto.HechoDTO;
import cargadorDinamico.domain.fuente.Fuente;
import cargadorDinamico.domain.fuente.TipoDeFuente;

import java.util.List;

public class HechosDinamicoService {
    private final DinamicoRepositorio repositorio;

    public HechosDinamicoService(DinamicoRepositorio repositorioNuevo) {
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

    public void guardarHecho(Hecho_D_DTO nueva) {
        try {
            repositorio.guardarHecho(nueva);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
