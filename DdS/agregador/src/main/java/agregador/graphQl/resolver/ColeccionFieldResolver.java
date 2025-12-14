package agregador.graphQl.resolver;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.dto.Coleccion.ColeccionDTO;
import agregador.dto.Hechos.HechoDTO;
import agregador.graphQl.dtoGraphQl.PageHechoDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.service.HechoConsultaService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ColeccionFieldResolver {

    private final HechoConsultaService hechoConsultaService;

    public ColeccionFieldResolver(HechoConsultaService hechoConsultaService) {
        this.hechoConsultaService = hechoConsultaService;
    }

    public List<HechoDTO> hechos(ColeccionDTO coleccion) {
        return hechoConsultaService.buscarHechosPorColeccion(
                UUID.fromString(coleccion.getId()),
                false
        );
    }

    public List<HechoDTO> hechosConsensuados(ColeccionDTO coleccion) {
        return hechoConsultaService.buscarHechosPorColeccion(
                UUID.fromString(coleccion.getId()),
                true
        );
    }
}
