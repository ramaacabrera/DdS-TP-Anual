package agregador.graphQl.resolver;

import agregador.domain.HechosYColecciones.Coleccion;
import agregador.dto.Coleccion.ColeccionDTO;
import agregador.dto.Hechos.FuenteDTO;
import agregador.dto.Hechos.HechoDTO;
import agregador.graphQl.dtoGraphQl.ColeccionGraphDTO;
import agregador.graphQl.dtoGraphQl.PageHechoDTO;
import agregador.graphQl.dtoGraphQl.PageRequestDTO;
import agregador.service.ColeccionConsultaService;
import agregador.service.HechoConsultaService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ColeccionFieldResolver {

    private final HechoConsultaService hechoConsultaService;
    private final ColeccionConsultaService coleccionConsultaService;

    public ColeccionFieldResolver(HechoConsultaService hechoConsultaService,
                                  ColeccionConsultaService coleccionConsultaService) {
        this.hechoConsultaService = hechoConsultaService;
        this.coleccionConsultaService = coleccionConsultaService;
    }

    public List<HechoDTO> hechos(ColeccionGraphDTO coleccion) {
        return hechoConsultaService.buscarHechosPorColeccion(
                UUID.fromString(coleccion.getId()),
                false
        );
    }

    public List<HechoDTO> hechosConsensuados(ColeccionGraphDTO coleccion) {
        return hechoConsultaService.buscarHechosPorColeccion(
                UUID.fromString(coleccion.getId()),
                true
        );
    }
    public List<FuenteDTO> fuentes(ColeccionGraphDTO coleccion) {
        return coleccionConsultaService.buscarFuentesPorColeccion(
                UUID.fromString(coleccion.getId())
        );
    }

}
