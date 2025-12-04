package cargadorDinamico.service;

import cargadorDinamico.domain.DinamicaDto.Hecho_D_DTO;
import cargadorDinamico.domain.HechosYColecciones.Hecho;
import cargadorDinamico.repository.DinamicoRepositorio;
import cargadorDinamico.domain.DinamicaDto.HechoDTO;
import cargadorDinamico.domain.fuente.Fuente;
import cargadorDinamico.domain.fuente.TipoDeFuente;
import cargadorDinamico.domain.*;
import cargadorDinamico.repository.HechoRepositorio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;
import java.util.UUID;

public class HechosDinamicoService {
    private final DinamicoRepositorio repositorio;
    private final HechoRepositorio hechoRepositorio;

    public HechosDinamicoService(DinamicoRepositorio repositorioNuevo, HechoRepositorio hechoRepositorio)
    {
        this.repositorio = repositorioNuevo;
        this.hechoRepositorio = hechoRepositorio;
    }

    public List<HechoDTO> obtenerHechos(){
        List<HechoDTO> dtos = repositorio.buscarHechos();
        repositorio.resetearHechos();
        dtos.forEach(dto -> {
           dto.setFuente(new Fuente(TipoDeFuente.DINAMICA, "fuenteDinamica"));
            try {
                System.out.println("Mando hecho a agregador: "+new ObjectMapper().writeValueAsString(dto));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return dtos;
    }

    public void guardarHecho(Hecho_D_DTO nueva) {
        try {
            System.out.println("Guardo hecho en dinamica: " + new ObjectMapper().writeValueAsString(nueva));
            repositorio.guardarHecho(nueva);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean actualizarHecho(String idString, Hecho_D_DTO hechoActualizado) {
        UUID id;

        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            return false;   // ID inválido
        }

        Hecho hechoExistente = hechoRepositorio.buscarPorId(id);

        if (hechoExistente == null) {
            return false;   // no existe
        }

        // ACTUALIZACIÓN
        hechoExistente.setTitulo(hechoActualizado.getTitulo());
        hechoExistente.setDescripcion(hechoActualizado.getDescripcion());

        hechoRepositorio.actualizar(hechoExistente);
        return true;  // ← importante
    }
}

