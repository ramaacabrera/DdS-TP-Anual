package gestorPublico.service;

import gestorPublico.domain.fuente.Fuente;
import gestorPublico.dto.Hechos.FuenteDTO;
import gestorPublico.repository.FuenteRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FuenteService {

    private final FuenteRepositorio fuenteRepositorio;

    public FuenteService(FuenteRepositorio fuenteRepositorio) {
        this.fuenteRepositorio = fuenteRepositorio;
    }

    public List<FuenteDTO> obtenerTodasLasFuentes() {
        List<Fuente> fuentes = fuenteRepositorio.obtenerTodas();

        return mapFuentesToDTO(fuentes);
    }

    private List<FuenteDTO> mapFuentesToDTO(List<Fuente> entidades) {
        if (entidades == null) return new ArrayList<>();

        return entidades.stream().map(f -> {
            FuenteDTO dto = new FuenteDTO();

            dto.setId(f.getId());
            dto.setDescriptor(f.getDescriptor());

            if (f.getTipoDeFuente() != null) {
                dto.setTipoDeFuente(f.getTipoDeFuente().name());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}